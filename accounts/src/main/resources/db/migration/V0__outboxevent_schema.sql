CREATE TABLE IF NOT EXISTS outboxevent
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    timestamp     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aggregatetype VARCHAR(255) NOT NULL,
    aggregateid   VARCHAR(255) NOT NULL,
    type          VARCHAR(255) NOT NULL,
    payload       JSONB        NOT NULL
);

ALTER TABLE outboxevent
    REPLICA IDENTITY FULL;
