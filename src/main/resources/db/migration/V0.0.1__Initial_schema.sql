CREATE SEQUENCE IF NOT EXISTS hibernate_sequence;

ALTER SEQUENCE hibernate_sequence owner TO orchestratordb;

CREATE TABLE IF NOT EXISTS context
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_context PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS context_attributes
(
    context_id BIGINT       NOT NULL,
    value      VARCHAR(255),
    name       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_context_attributes PRIMARY KEY (context_id, name)
);

ALTER TABLE context_attributes
    ADD CONSTRAINT fk_context_attributes_on_context FOREIGN KEY (context_id) REFERENCES context (id);

CREATE TABLE IF NOT EXISTS state
(
    id      BIGINT       NOT NULL,
    name    VARCHAR(255) NOT NULL,
    initial BOOLEAN      NOT NULL,
    final   BOOLEAN      NOT NULL,
    CONSTRAINT pk_state PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS state_machine_type
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_state_machine_type PRIMARY KEY (id)
);

ALTER TABLE state_machine_type
    ADD CONSTRAINT uc_state_machine_type_name UNIQUE (name);

CREATE TABLE IF NOT EXISTS transition
(
    id                    BIGINT       NOT NULL,
    from_state_id         BIGINT,
    to_state_id           BIGINT,
    event                 VARCHAR(255) NOT NULL,
    action                VARCHAR(255),
    state_machine_type_id BIGINT,
    CONSTRAINT pk_transition PRIMARY KEY (id)
);

ALTER TABLE transition
    ADD CONSTRAINT FK_TRANSITION_ON_FROM_STATE FOREIGN KEY (from_state_id) REFERENCES state (id);

ALTER TABLE transition
    ADD CONSTRAINT FK_TRANSITION_ON_STATEMACHINETYPE FOREIGN KEY (state_machine_type_id) REFERENCES state_machine_type (id);

ALTER TABLE transition
    ADD CONSTRAINT FK_TRANSITION_ON_TO_STATE FOREIGN KEY (to_state_id) REFERENCES state (id);

CREATE TABLE IF NOT EXISTS state_machine
(
    id                    VARCHAR(255) NOT NULL,
    current_state_id      BIGINT,
    state_machine_type_id BIGINT,
    context_id            BIGINT,
    CONSTRAINT pk_state_machine PRIMARY KEY (id)
);

ALTER TABLE state_machine
    ADD CONSTRAINT FK_STATE_MACHINE_ON_CONTEXT FOREIGN KEY (context_id) REFERENCES context (id);

ALTER TABLE state_machine
    ADD CONSTRAINT FK_STATE_MACHINE_ON_CURRENT_STATE FOREIGN KEY (current_state_id) REFERENCES state (id);

ALTER TABLE state_machine
    ADD CONSTRAINT FK_STATE_MACHINE_ON_STATE_MACHINE_TYPE FOREIGN KEY (state_machine_type_id) REFERENCES state_machine_type (id);