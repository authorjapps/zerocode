import json
import re
import shutil
import subprocess
import sys
from pathlib import Path

import pytest

from tdd_state_report import build_report

ROOT = Path(__file__).resolve().parent.parent
EXAMPLES = ROOT / "examples"
FIXTURES = Path(__file__).resolve().parent / "fixtures"
EMBEDDED = re.compile(r'<script id="report-data" type="application/json">([\s\S]*?)</script>')


def embedded_report(html_path):
    match = EMBEDDED.search(Path(html_path).read_text(encoding="utf-8"))
    assert match, "no embedded report-data script"
    return json.loads(match.group(1))


def write_json(path, doc):
    path.write_text(json.dumps(doc), encoding="utf-8")
    return path


def scenario(name, request_time):
    return {"scenarioName": name, "steps": [{"name": "step", "requestTimeStamp": request_time}]}


def raw_step_count(folder):
    files = [path for path in Path(folder).iterdir() if path.suffix == ".json"]
    docs = [json.loads(path.read_text(encoding="utf-8")) for path in files]
    return sum(len(result.get("steps") or []) for doc in docs for result in doc["results"])


def test_collect_files_takes_only_top_level_json(tmp_path):
    write_json(tmp_path / "b.json", {})
    write_json(tmp_path / "a.JSON", {})
    (tmp_path / "notes.txt").write_text("x")
    (tmp_path / "sub").mkdir()
    write_json(tmp_path / "sub" / "c.json", {})
    names = [path.name for path in build_report.collect_files([str(tmp_path)])]
    assert names == ["a.JSON", "b.json"]


def test_collect_files_keeps_file_arguments(tmp_path):
    first = write_json(tmp_path / "x.json", {})
    assert build_report.collect_files([str(first)]) == [first]


def test_default_output_for_folder_is_its_parent(tmp_path):
    folder = tmp_path / "target" / "zerocode-test-reports"
    folder.mkdir(parents=True)
    expected = (tmp_path / "target" / build_report.DEFAULT_NAME).resolve()
    assert build_report.default_output([str(folder)]) == expected


def test_default_output_for_file_is_parent_of_its_folder(tmp_path):
    folder = tmp_path / "target" / "reports"
    folder.mkdir(parents=True)
    report = write_json(folder / "r.json", {})
    expected = (tmp_path / "target" / build_report.DEFAULT_NAME).resolve()
    assert build_report.default_output([str(report)]) == expected


def test_load_report_skips_bad_json_and_non_reports(tmp_path, capsys):
    broken = tmp_path / "broken.json"
    broken.write_text("{not json", encoding="utf-8")
    other = write_json(tmp_path / "other.json", {"name": "not a report"})
    assert build_report.load_report(broken) is None
    assert build_report.load_report(other) is None
    warnings = capsys.readouterr().err
    assert "WARN skipped" in warnings and "no 'results' list" in warnings


def test_merge_sorts_by_first_request_time_and_keeps_earliest_stamp():
    late = {"timeStamp": "2026-01-02T00:00:00", "results": [scenario("late", "2026-01-02T10:00:00.000")]}
    early = {"timeStamp": "2026-01-01T00:00:00", "results": [scenario("early", "2026-01-01T10:00:00.000")]}
    no_steps = {"results": [{"scenarioName": "no steps", "steps": []}]}
    merged = build_report.merge_reports([late, no_steps, early])
    assert [result["scenarioName"] for result in merged["results"]] == ["early", "late", "no steps"]
    assert merged["timeStamp"] == "2026-01-01T00:00:00"


def test_render_rejects_template_without_single_placeholder(tmp_path):
    none = tmp_path / "none.html"
    none.write_text("<html></html>", encoding="utf-8")
    twice = tmp_path / "twice.html"
    twice.write_text(build_report.PLACEHOLDER * 2, encoding="utf-8")
    for template in (none, twice):
        with pytest.raises(SystemExit):
            build_report.render_html(template, {"results": []})


def test_render_escapes_closing_script_tag():
    report = {"results": [{"steps": [{"response": "</script><b>x</b>"}]}]}
    html = build_report.render_html(build_report.DEFAULT_TEMPLATE, report)
    assert "</script><b>" not in html
    assert "<\\/script><b>" in html


def test_bundled_viewer_has_the_placeholder():
    template = build_report.DEFAULT_TEMPLATE
    assert template.is_file()
    assert template.read_text(encoding="utf-8").count(build_report.PLACEHOLDER) == 1


def test_main_exits_when_no_valid_report(tmp_path):
    write_json(tmp_path / "other.json", {"name": "not a report"})
    with pytest.raises(SystemExit) as raised:
        build_report.main([str(tmp_path), "-o", str(tmp_path / "out.html")])
    assert "no Zerocode JSON reports" in str(raised.value.code)
    assert not (tmp_path / "out.html").exists()


def test_main_writes_default_output_next_to_folder(tmp_path):
    folder = tmp_path / "target" / "zerocode-test-reports"
    shutil.copytree(EXAMPLES / "dummy_scenarios", folder)
    build_report.main([str(folder)])
    report = embedded_report(tmp_path / "target" / build_report.DEFAULT_NAME)
    assert len(report["results"]) == 5


@pytest.mark.parametrize("folder", ["dummy_scenarios", "zerocode-test-reports"])
def test_examples_keep_every_step_and_retry_attempt(tmp_path, folder):
    output = tmp_path / "report.html"
    build_report.main([str(EXAMPLES / folder), "-o", str(output)])
    report = embedded_report(output)
    steps = sum(len(result.get("steps") or []) for result in report["results"])
    assert steps == raw_step_count(EXAMPLES / folder)


def test_retry_example_has_repeated_correlation_ids(tmp_path):
    output = tmp_path / "report.html"
    retry_file = next((EXAMPLES / "zerocode-test-reports").glob("Retry-execution-*.json"))
    build_report.main([str(retry_file), "-o", str(output)])
    steps = embedded_report(output)["results"][0]["steps"]
    ids = [step.get("correlationId") for step in steps]
    assert len(ids) > len(set(ids)), "retry attempts were collapsed"


@pytest.mark.parametrize("source", [
    EXAMPLES / "aws_Scenario1-b6eb6c39-7dfa-41f6-a9b9-18f55333a81a.json",
    FIXTURES / "aws_failed_two_steps.json",
])
def test_aws_format_files_build(tmp_path, source):
    output = tmp_path / "report.html"
    build_report.main([str(source), "-o", str(output)])
    expected = json.loads(source.read_text(encoding="utf-8"))["results"]
    assert embedded_report(output)["results"] == expected


def test_skips_non_report_next_to_valid_one(tmp_path, capsys):
    shutil.copy(FIXTURES / "aws_failed_two_steps.json", tmp_path)
    write_json(tmp_path / "other.json", {"name": "not a report"})
    build_report.main([str(tmp_path), "-o", str(tmp_path / "out.html")])
    captured = capsys.readouterr()
    assert "skipped 1" in captured.out
    assert "WARN skipped" in captured.err


def test_module_entry_point_runs(tmp_path):
    output = tmp_path / "report.html"
    source = FIXTURES / "aws_failed_two_steps.json"
    command = [sys.executable, "-m", "tdd_state_report", str(source), "-o", str(output)]
    completed = subprocess.run(command, capture_output=True, text=True, check=False)
    assert completed.returncode == 0, completed.stderr
    assert "Written:" in completed.stdout
    assert output.is_file()
