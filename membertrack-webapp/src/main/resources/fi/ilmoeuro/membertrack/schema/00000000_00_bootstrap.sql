CREATE TABLE "PUBLIC"."MIGRATION" (
    "pk"                INTEGER             IDENTITY(1,1),
    "id"                UUID                NOT NULL,
    "file_name"         VARCHAR(4096)       NOT NULL,
    CONSTRAINT "$migration_pk$"
        PRIMARY KEY ("pk"),
    CONSTRAINT "$migration_u_id$"
        UNIQUE ("id"),
    CONSTRAINT "$migation_u_file_name$"
        UNIQUE ("file_name")
);