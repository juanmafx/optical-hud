#!/usr/bin/env python3
# Builds reports/tests/summary.md from JUnit XML produced by a real Gradle test run.
import re
import sys
import xml.etree.ElementTree as ElementTree
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RESULTS = ROOT / "android-app/core/build/test-results/test"
TEST_SOURCES = ROOT / "android-app/core/src/test"
OUTPUT = ROOT / "reports/tests/summary.md"


def main():
    suites = sorted(
        (ElementTree.parse(path).getroot() for path in RESULTS.glob("TEST-*.xml")),
        key=lambda suite: suite.get("name"),
    )
    if not suites:
        sys.exit("No JUnit XML found. Run the core test task first.")
    totals = {key: sum(int(suite.get(key)) for suite in suites) for key in ("tests", "failures", "errors", "skipped")}
    declared = sum(
        len(re.findall(r"^\s*@Test\b", path.read_text(encoding="utf-8"), re.MULTILINE))
        for path in TEST_SOURCES.rglob("*.kt")
    )
    if declared != totals["tests"]:
        sys.exit(f"{declared} tests declared but {totals['tests']} executed")
    passed = totals["tests"] - totals["failures"] - totals["errors"] - totals["skipped"]
    lines = [
        "# Test Report",
        "",
        f"- Generated: {datetime.now(timezone.utc).strftime('%Y-%m-%dT%H:%M:%SZ')}",
        "- Command: `gradle :core:test` (Gradle 9.9.2, JDK 17)",
        "- Scope: JVM tests of the `core` module. No instrumented or physical-device tests were run.",
        f"- Total: {totals['tests']} | Passed: {passed} | Failed: {totals['failures']} | "
        f"Errors: {totals['errors']} | Skipped: {totals['skipped']}",
        "",
        "| Suite | Tests | Failed | Errors | Skipped | Time (s) |",
        "| --- | ---: | ---: | ---: | ---: | ---: |",
    ]
    for suite in suites:
        lines.append(
            f"| `{suite.get('name').removeprefix('com.juanmafx.opticalhud.core.')}` | {suite.get('tests')} | "
            f"{suite.get('failures')} | {suite.get('errors')} | {suite.get('skipped')} | {suite.get('time')} |"
        )
    lines += ["", "## Test Cases", ""]
    for suite in suites:
        lines.append(f"### {suite.get('name').rsplit('.', 1)[-1]}")
        lines.append("")
        for case in suite.findall("testcase"):
            failed = case.find("failure") is not None or case.find("error") is not None
            status = "FAILED" if failed else "skipped" if case.find("skipped") is not None else "passed"
            lines.append(f"- {status}: {case.get('name').removesuffix('()')}")
        lines.append("")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text("\n".join(lines).rstrip() + "\n", encoding="utf-8")
    print(f"{totals['tests']} tests, {passed} passed")
    if passed != totals["tests"]:
        sys.exit(1)


if __name__ == "__main__":
    main()
