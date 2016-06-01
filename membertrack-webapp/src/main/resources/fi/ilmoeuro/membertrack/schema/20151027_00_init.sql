CREATE TABLE "PUBLIC"."PERSON" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "full_name"         VARCHAR(255)        NOT NULL,
    "email"             VARCHAR(255)        NOT NULL,
    CONSTRAINT "person_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "person_u_id"
        UNIQUE ("id"),
    CONSTRAINT "person_u_email"
        UNIQUE ("email")
);

CREATE TABLE "PUBLIC"."PERSON_HOLVI_HANDLE" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "person_id"         VARCHAR(255)        NOT NULL,
    "handle"            VARCHAR(255)        NOT NULL,
    CONSTRAINT "person_holvi_handle_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "person_holvi_handle_u_id"
        UNIQUE ("id"),
    CONSTRAINT "person_holvi_handle_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id"),
    CONSTRAINT "person_holvi_handle_u_handle"
        UNIQUE ("handle")
);

CREATE TABLE "PUBLIC"."ACCOUNT" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "person_id"         UUID                NOT NULL,
    "hash"              VARCHAR(4096)       NOT NULL,
    "salt"              VARCHAR(4096)       NOT NULL,
    CONSTRAINT "account_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "account_u_id"
        UNIQUE ("id"),
    CONSTRAINT "account_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id")
);

CREATE TABLE "PUBLIC"."PHONE_NUMBER" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "person_id"         UUID                NOT NULL,
    "phone_number"      VARCHAR(255)        NOT NULL,
    CONSTRAINT "phone_number_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "phone_number_u_id"
        UNIQUE ("id"),
    CONSTRAINT "phone_number_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id")
);

CREATE TABLE "PUBLIC"."SERVICE" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "title"             VARCHAR(255)        NOT NULL,
    "description"       VARCHAR(255)        NOT NULL,
    CONSTRAINT "service_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "service_u_id"
        UNIQUE ("id"),
    CONSTRAINT "service_u_title"
        UNIQUE ("title")
);

CREATE TABLE "PUBLIC"."SUBSCRIPTION_PERIOD" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "service_id"        UUID                NOT NULL,
    "person_id"         UUID                NOT NULL,
    "start_date"        DATE                NOT NULL,
    "length_unit"       VARCHAR(255)        NOT NULL,
    "length"            BIGINT              NOT NULL,
    "payment"           INTEGER             NOT NULL,
    "approved"          BIT(1)              NOT NULL,
    CONSTRAINT "subscription_period_pk"
        PRIMARY KEY ("pk"),
    CONSTRAINT "subscription_period_u_id"
        UNIQUE ("id"),
    CONSTRAINT "subscription_period_fk_service_id"
        FOREIGN KEY ("service_id") REFERENCES "SERVICE" ("id"),
    CONSTRAINT "subscription_period_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id"),
    CONSTRAINT "subscription_period_c_length_unit"
        CHECK ("length_unit" = 'YEAR' OR
               "length_unit" = 'DAY')
);