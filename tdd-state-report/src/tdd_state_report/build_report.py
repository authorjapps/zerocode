"""Build one shareable SFEO HTML report from existing Zerocode JSON reports (no test re-run).

Usage (uv venv):
  tdd-state-report <folder | file.json ...> [-o out.html] [--template path]
  python -m tdd_state_report <folder | file.json ...> [-o out.html] [--template path]

- A folder means every *.json directly inside it (not recursive), e.g. target/zerocode-test-reports/.
- All reports are embedded in the HTML, nothing is masked, so the file can be shared as-is.
- Default output: zerocode-tabular-report.html in the folder's parent (target/ for
  target/zerocode-test-reports/). For file arguments, next to the first file's folder.
"""
import argparse
import json
import sys
from pathlib import Path

PLACEHOLDER = "/*__REPORT_DATA__*/"
DEFAULT_TEMPLATE = Path(__file__).resolve().parent / "viewer" / "sfeo-report.html"
DEFAULT_NAME = "test-state-report.html"


def parse_args(argv):
    parser = argparse.ArgumentParser(description="Build a shareable SFEO HTML from Zerocode JSON reports.")
    parser.add_argument("inputs", nargs="+", help="a reports folder, or one or more JSON report files")
    parser.add_argument("-o", "--output", help="output HTML path (default: " + DEFAULT_NAME + " next to the input)")
    parser.add_argument("--template", default=str(DEFAULT_TEMPLATE), help="viewer HTML with the data placeholder")
    return parser.parse_args(argv)


def collect_files(inputs):
    files = []
    for item in map(Path, inputs):
        if item.is_dir():
            files.extend(sorted(path for path in item.iterdir() if path.suffix.lower() == ".json"))
            continue
        files.append(item)
    return files


def default_output(inputs):
    first = Path(inputs[0]).resolve()
    base = first.parent if first.is_dir() else first.parent.parent
    return base / DEFAULT_NAME


def load_report(path):
    """Return the parsed report, or None (with a warning) when it is not a Zerocode report."""
    try:
        doc = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        print(f"WARN skipped {path}: {error}", file=sys.stderr)
        return None
    if not isinstance(doc, dict) or not isinstance(doc.get("results"), list):
        print(f"WARN skipped {path}: not a Zerocode report (no 'results' list)", file=sys.stderr)
        return None
    return doc


def first_request_time(result):
    steps = result.get("steps") or []
    if not steps:
        return ""
    first = steps[0]
    return str(first.get("requestTimeStamp") or first.get("RequestTime") or "")


def merge_reports(docs):
    """Same rules as mergeReports() in the viewer: concatenate, sort by first request time."""
    results = [result for doc in docs for result in doc["results"]]
    results.sort(key=lambda result: (first_request_time(result) == "", first_request_time(result)))
    stamps = sorted(doc["timeStamp"] for doc in docs if doc.get("timeStamp"))
    return {"timeStamp": stamps[0] if stamps else "", "results": results}


def render_html(template_path, report):
    template = Path(template_path).read_text(encoding="utf-8")
    if template.count(PLACEHOLDER) != 1:
        raise SystemExit(f"ERROR template has no single {PLACEHOLDER} placeholder: {template_path}")
    data = json.dumps(report, ensure_ascii=False).replace("</", "<\\/")
    return template.replace(PLACEHOLDER, data)


def main(argv=None):
    args = parse_args(argv)
    files = collect_files(args.inputs)
    docs = [doc for doc in map(load_report, files) if doc is not None]
    if not docs:
        raise SystemExit("ERROR no Zerocode JSON reports found in: " + ", ".join(args.inputs))
    report = merge_reports(docs)
    output = Path(args.output) if args.output else default_output(args.inputs)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(render_html(args.template, report), encoding="utf-8")
    skipped = len(files) - len(docs)
    print(f"Read {len(docs)} report file(s), {len(report['results'])} scenario(s), skipped {skipped}.")
    print(f"Written: {output}")


if __name__ == "__main__":
    main(sys.argv[1:])
