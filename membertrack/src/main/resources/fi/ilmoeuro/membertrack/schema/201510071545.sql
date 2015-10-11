CREATE TABLE "PUBLIC"."DATA_EVENT" (
    "id"                INTEGER             IDENTITY(1,1),
    "type"              VARCHAR(255)        NOT NULL,
    "email"             VARCHAR(255)        NOT NULL,
    "time"              TIMESTAMP           NOT NULL,
    "target_table"      VARCHAR(255)        NOT NULL,
    "target_id"         VARCHAR(255)        NOT NULL,
    CONSTRAINT "audit_event_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "audit_event_c_type"
        CHECK (
            "type" = 'CREATE' OR
            "type" = 'UPDATE' OR
            "type" = 'DELETE'
        )
);

CREATE TABLE "PUBLIC"."PERSON" (
    "id"                INTEGER             IDENTITY(1,1),
    "email"             VARCHAR(255)        NOT NULL,
    CONSTRAINT "person_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "person_u_email"
        UNIQUE ("email")
);

CREATE TABLE "PUBLIC"."ORGANIZATION" (
    "id"                INTEGER             IDENTITY(1,1),
    "name"              VARCHAR(255)        NOT NULL,
    CONSTRAINT "organization_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "organization_u_name"
        UNIQUE ("name")
);

CREATE TABLE "PUBLIC"."ACCOUNT" (
    "id"                INTEGER             IDENTITY(1,1),
    "person_id"         INTEGER             NOT NULL,
    "organization_id"   INTEGER             NOT NULL,
    "hash"              VARCHAR(255)        NOT NULL,
    "salt"              VARCHAR(255)        NOT NULL,
    CONSTRAINT "account_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "account_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id"),
    CONSTRAINT "account_fk_organization_id"
        FOREIGN KEY ("organization_id") REFERENCES "ORGANIZATION" ("id"),
    CONSTRAINT "account_u_person_id"
        UNIQUE ("person_id")
);

CREATE TABLE "PUBLIC"."MEMBERSHIP" (
    "id"                INTEGER             IDENTITY(1,1),
    "person_id"         INTEGER             NOT NULL,
    "organization_id"   INTEGER             NOT NULL,
    CONSTRAINT "membership_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "membership_fk_person_id"
        FOREIGN KEY ("person_id") REFERENCES "PERSON" ("id"),
    CONSTRAINT "membership_fk_organization_id"
        FOREIGN KEY ("organization_id") REFERENCES "ORGANIZATION" ("id")
);

CREATE TABLE "PUBLIC"."PHONE_NUMBER" (
    "id"                INTEGER             IDENTITY(1,1),
    "membership_id"     INTEGER             NOT NULL,
    "phone_number"      VARCHAR(255)        NOT NULL,
    CONSTRAINT "phone_number_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "phone_number_fk_membership_id"
        FOREIGN KEY ("membership_id") REFERENCES "MEMBERSHIP" ("id")
);

CREATE TABLE "PUBLIC"."SERVICE" (
    "id"                INTEGER             IDENTITY(1,1),
    "title"             VARCHAR(255)        NOT NULL,
    "description"       VARCHAR(255)        NOT NULL,
    "organization_id"   INTEGER             NOT NULL,
    CONSTRAINT "service_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "service_fk_organization_id"
        FOREIGN KEY ("organization_id") REFERENCES "ORGANIZATION" ("id"),
    CONSTRAINT "service_u_title"
        UNIQUE ("title")
);

CREATE TABLE "PUBLIC"."SERVICE_SUBSCRIPTION" (
    "id"                INTEGER             IDENTITY(1,1),
    "service_id"        INTEGER             NOT NULL,
    "membership_id"     INTEGER             NOT NULL,
    "start_time"        TIMESTAMP           NOT NULL,
    "length"            INTEGER             NOT NULL,
    CONSTRAINT "service_subscription_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "service_subscription_fk_service_id"
        FOREIGN KEY ("service_id") REFERENCES "SERVICE" ("id"),
    CONSTRAINT "service_subscription_fk_membership_id"
        FOREIGN KEY ("membership_id") REFERENCES "MEMBERSHIP" ("id")
);