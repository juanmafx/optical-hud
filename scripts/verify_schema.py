#!/usr/bin/env python3
# Applies db/schema.sql to an in-memory database, imports the committed experiment CSV, and checks constraints.
import csv
import sqlite3
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
POINT_COLUMNS = (
    "jitter_fraction",
    "jitter_sigma_ms",
    "trials",
    "successful_messages",
    "message_success_rate",
    "success_ci95_lower",
    "success_ci95_upper",
    "character_error_rate",
)
INTEGER_COLUMNS = {"trials", "successful_messages"}


def convert(column, value):
    return int(value) if column in INTEGER_COLUMNS else float(value)


def main():
    connection = sqlite3.connect(":memory:")
    connection.executescript((ROOT / "db" / "schema.sql").read_text(encoding="utf-8"))
    connection.execute("PRAGMA foreign_keys = ON")
    rows = list(csv.DictReader((ROOT / "reports/experiments/channel-seed-42.csv").open(encoding="utf-8")))
    first = rows[0]
    run_id = connection.execute(
        "INSERT INTO experiment_run (seed, unit_ms, frame_interval_ms, message_length, code_version) VALUES (?, ?, ?, ?, ?)",
        (int(first["seed"]), int(first["unit_ms"]), float(first["frame_interval_ms"]), int(first["message_length"]), "2.0.0"),
    ).lastrowid
    placeholders = ", ".join("?" for _ in range(len(POINT_COLUMNS) + 1))
    statement = f"INSERT INTO experiment_point (run_id, {', '.join(POINT_COLUMNS)}) VALUES ({placeholders})"
    for row in rows:
        connection.execute(statement, (run_id, *(convert(column, row[column]) for column in POINT_COLUMNS)))
    stored = connection.execute("SELECT COUNT(*) FROM experiment_point").fetchone()[0]
    if stored != len(rows):
        sys.exit("Stored row count does not match the CSV")
    try:
        connection.execute(
            "INSERT INTO experiment_point VALUES (?, 9.0, 1.0, 10, 11, 0.5, 0.1, 0.9, 0.0)", (run_id,)
        )
    except sqlite3.IntegrityError:
        print(f"Schema verified: {stored} rows imported, invalid row rejected")
        return
    sys.exit("Constraint violation was not rejected")


if __name__ == "__main__":
    main()
