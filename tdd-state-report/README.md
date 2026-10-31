# tdd-state-report
Zerocode TDD State transition Report

A single-page HTML report that shows [Zerocode-TDD](https://github.com/authorjapps/zerocode) JSON reports
in the AWS Step Functions "execution output" (SFEO) layout. There is a list of executions
(scenarios); clicking one shows its steps table, with Input/Output and Details for each step.
Retry attempts are grouped per step.

## Setup (once)

All Python runs in a [`uv`](https://docs.astral.sh/uv/) virtual env. The tool itself uses only the
standard library; `pytest` is the only dev dependency.

```
cd tdd-state-report
uv venv .venv
uv sync
```

`uv sync` installs the package in editable mode, so `tdd-state-report` is on the venv's path.

## Build a shareable report from a reports folder (no test re-run)

```
uv run tdd-state-report ../http-testing-examples/target/zerocode-test-reports
```

- Reads every `*.json` directly inside the folder (not sub-folders) and merges them into one run.
- Writes one self-contained HTML with all the JSON embedded, ready to share. The default output is
  `zerocode-tabular-report.html` in the folder's parent, i.e. `target/zerocode-tabular-report.html`.
- Nothing is masked. Headers, cookies, tokens and bodies are shown as captured.
- Other forms:

```
# one or more report files
uv run tdd-state-report examples/zerocode-test-reports/Scenario1-b6eb6c39-7dfa-41f6-a9b9-18f55333a81a.json -o out/scenario1.html

# choose the output file
uv run tdd-state-report examples/zerocode-test-reports -o out/run-report.html

# same, without the console script
.venv/bin/python -m tdd_state_report examples/dummy_scenarios -o out/dummy-report.html
```

Files that are not Zerocode reports are skipped with a warning. If no valid report is found, the
command exits with code 1. `--template path` uses another viewer HTML (it must contain the
`/*__REPORT_DATA__*/` placeholder once).

## Use the viewer directly

Open `src/tdd_state_report/viewer/sfeo-report.html` in a browser (double-click is fine), then:

- **Open folder**: pick a `target/zerocode-test-reports/` folder.
- **Open files** or drag-and-drop: one or more `.json` reports.
- **Save shareable HTML**: downloads the page with the loaded reports embedded.

Retries: attempts with the same `correlationId` are grouped into one step with a `↻N` badge. Expand
the row (▸) or use the attempt pills (or ←/→) to see each attempt's Input/Output and assertion errors.

Over http you can also pass a file with `?file=`:

```
.venv/bin/python -m http.server 8765
# open http://localhost:8765/src/tdd_state_report/viewer/sfeo-report.html?file=/examples/aws_Scenario1-b6eb6c39-7dfa-41f6-a9b9-18f55333a81a.json
```

## Dummy data

```
# 5 fake scenarios (4 passed, 1 failed, with retries) -> examples/dummy_scenarios/ (+ merged/dummy-run-all.json)
.venv/bin/python -I scripts/gen_dummy.py examples/dummy_scenarios
```

The output is deterministic (fixed UUIDs and timestamps). The output folder must exist.

## Tests

```
uv run pytest -v
```

The tests cover the command (file collection, merge order, skipping bad files, output path,
`</script>` escaping, retry attempts kept, `python -m` entry point) on the `examples/` and
`tests/fixtures/` reports. The viewer UI (clicks, Open folder, Save shareable HTML) is checked by hand
in a browser.

## Layout

| Path | What |
|---|---|
| `src/tdd_state_report/build_report.py` | the `tdd-state-report` command: builds a shareable HTML from a folder or files |
| `src/tdd_state_report/viewer/sfeo-report.html` | the viewer, also the template the command embeds into |
| `scripts/gen_dummy.py` | generates the dummy scenarios |
| `examples/dummy_scenarios/` | generated dummy reports (raw Zerocode format) |
| `examples/zerocode-test-reports/` | real sample reports, including a retry run |
| `examples/aws_Scenario1-….json` | a sample in the `aws_*` key format |
| `tests/` | pytest tests and fixtures |
