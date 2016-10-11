ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    DROP CONSTRAINT "$subscription_period_c_length_unit$";

ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    DROP CONSTRAINT "$subscription_period_c_length_gt0$";

ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    ADD COLUMN "end_date"
        DATE
        DEFAULT (DATE '2099-01-01')
        NOT NULL
        AFTER "start_date";

UPDATE "PUBLIC"."SUBSCRIPTION_PERIOD"
    SET "end_date" = DATEADD('DAY', "length", "start_date")
    WHERE "length_unit" = 'DAY';

UPDATE "PUBLIC"."SUBSCRIPTION_PERIOD"
    SET "end_date" = (DATE '2015-12-31')
    WHERE "length_unit" = 'YEAR' AND
        "start_date" < (DATE '2016-01-01');

UPDATE "PUBLIC"."SUBSCRIPTION_PERIOD"
    SET "end_date" = (DATE '2016-12-31')
    WHERE "length_unit" = 'YEAR' AND
        "start_date" > (DATE '2016-01-01');

ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    ADD CONSTRAINT "$subscription_period_c_end_date_ge_start_date$"
    CHECK ("end_date" >= "start_date");

ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    DROP COLUMN "length";

ALTER TABLE "PUBLIC"."SUBSCRIPTION_PERIOD"
    DROP COLUMN "length_unit";