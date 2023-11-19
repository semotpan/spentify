CREATE TABLE IF NOT EXISTS outbox_event
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    timestamp      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSON         NOT NULL
);

ALTER TABLE outbox_event
    REPLICA IDENTITY FULL;
