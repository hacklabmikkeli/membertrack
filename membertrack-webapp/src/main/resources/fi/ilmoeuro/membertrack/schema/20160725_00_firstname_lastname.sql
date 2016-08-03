ALTER TABLE "PUBLIC"."SECONDARY_EMAIL"
    DROP CONSTRAINT "$secondary_email_c_no_duplicate_emails$";

ALTER TABLE "PUBLIC"."PERSON"
    DROP CONSTRAINT "$person_c_no_duplicate_emails$";

ALTER TABLE "PUBLIC"."PERSON"
    ADD COLUMN "first_name" VARCHAR(255) DEFAULT '' NOT NULL AFTER "full_name";

ALTER TABLE "PUBLIC"."PERSON"
    ADD COLUMN "last_name" VARCHAR(255) DEFAULT '' NOT NULL AFTER "first_name";

UPDATE "PUBLIC"."PERSON"
    SET "first_name" = LEFT("full_name", INSTR("full_name", ' '));

UPDATE "PUBLIC"."PERSON"
    SET "last_name" = RIGHT("full_name", LENGTH("full_name") - INSTR("full_name", ' '));

ALTER TABLE "PUBLIC"."PERSON"
    DROP COLUMN "full_name";

ALTER TABLE "PUBLIC"."PERSON"
    ADD CONSTRAINT "$person_c_no_duplicate_emails$"
        CHECK NOT EXISTS
            (SELECT
                1
             FROM
                "PUBLIC"."SECONDARY_EMAIL"
             WHERE
                "PUBLIC"."SECONDARY_EMAIL"."email" = "PUBLIC"."PERSON"."email");

ALTER TABLE "PUBLIC"."SECONDARY_EMAIL"
    ADD CONSTRAINT "$secondary_email_c_no_duplicate_emails$"
        CHECK NOT EXISTS
            (SELECT
                1
             FROM
                "PUBLIC"."PERSON"
             WHERE
                "PUBLIC"."PERSON"."email" = "PUBLIC"."SECONDARY_EMAIL"."email");
