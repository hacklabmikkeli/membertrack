CREATE TABLE "PUBLIC"."PERSON" (
    "id"                INTEGER             IDENTITY(1,1),
    "email"             VARCHAR(255)        NOT NULL,
    CONSTRAINT "person_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "person_u_email"
        UNIQUE ("email")
);

CREATE TABLE "PUBLIC"."ACCOUNT" (
    "id"                INTEGER             IDENTITY(1,1),
    "person_id"         INTEGER             NOT NULL,
    "hash"              VARCHAR(255)        NOT NULL,
    "salt"              VARCHAR(255)        NOT NULL,
    CONSTRAINT "account_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "account_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id")
);

CREATE TABLE "PUBLIC"."PHONE_NUMBER" (
    "id"                INTEGER             IDENTITY(1,1),
    "person_id"         INTEGER             NOT NULL,
    "phone_number"      VARCHAR(255)        NOT NULL,
    CONSTRAINT "phone_number_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "phone_number_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id")
);

CREATE TABLE "PUBLIC"."SERVICE" (
    "id"                INTEGER             IDENTITY(1,1),
    "title"             VARCHAR(255)        NOT NULL,
    "description"       VARCHAR(255)        NOT NULL,
    CONSTRAINT "service_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "service_u_title"
        UNIQUE ("title")
);

CREATE TABLE "PUBLIC"."SERVICE_SUBSCRIPTION" (
    "id"                INTEGER             IDENTITY(1,1),
    "service_id"        INTEGER             NOT NULL,
    "person_id"         INTEGER             NOT NULL,
    "start_time"        TIMESTAMP           NOT NULL,
    "length"            INTEGER             NOT NULL,
    CONSTRAINT "service_subscription_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "service_subscription_fk_service_id"
        FOREIGN KEY ("service_id") REFERENCES "SERVICE" ("id"),
    CONSTRAINT "service_subscription_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id")
);