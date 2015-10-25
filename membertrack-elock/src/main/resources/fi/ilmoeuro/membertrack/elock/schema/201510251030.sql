CREATE TABLE "PUBLIC"."APPROVED_PHONE_NUMBER" (
    "id"                INTEGER             IDENTITY(1,1),
    "number"            VARCHAR(255)        NOT NULL,
    CONSTRAINT "approved_phone_number_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "approved_phone_number_u_number"
        UNIQUE ("number")
);