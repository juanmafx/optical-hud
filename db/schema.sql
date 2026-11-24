-- Optional SQLite schema for archiving channel experiment results.
-- The Android app does not use a database and never stores message text.
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS experiment_run (
    id                INTEGER PRIMARY KEY,
    seed              INTEGER NOT NULL,
    unit_ms           INTEGER NOT NULL CHECK (unit_ms >= 170),
    frame_interval_ms REAL    NOT NULL CHECK (frame_interval_ms >= 0),
    message_length    INTEGER NOT NULL CHECK (message_length > 0),
    code_version      TEXT    NOT NULL CHECK (length(code_version) > 0),
    created_at        TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    UNIQUE (seed, unit_ms, frame_interval_ms, message_length, code_version)
) STRICT;

CREATE TABLE IF NOT EXISTS experiment_point (
    run_id               INTEGER NOT NULL REFERENCES experiment_run (id) ON DELETE CASCADE,
    jitter_fraction      REAL    NOT NULL CHECK (jitter_fraction >= 0),
    jitter_sigma_ms      REAL    NOT NULL CHECK (jitter_sigma_ms >= 0),
    trials               INTEGER NOT NULL CHECK (trials > 0),
    successful_messages  INTEGER NOT NULL CHECK (successful_messages BETWEEN 0 AND trials),
    message_success_rate REAL    NOT NULL CHECK (message_success_rate BETWEEN 0 AND 1),
    success_ci95_lower   REAL    NOT NULL CHECK (success_ci95_lower BETWEEN 0 AND 1),
    success_ci95_upper   REAL    NOT NULL CHECK (success_ci95_upper BETWEEN 0 AND 1),
    character_error_rate REAL    NOT NULL CHECK (character_error_rate >= 0),
    PRIMARY KEY (run_id, jitter_fraction),
    CHECK (success_ci95_lower <= message_success_rate AND message_success_rate <= success_ci95_upper)
) STRICT, WITHOUT ROWID;

CREATE INDEX IF NOT EXISTS idx_experiment_point_jitter
    ON experiment_point (jitter_fraction, message_success_rate);
