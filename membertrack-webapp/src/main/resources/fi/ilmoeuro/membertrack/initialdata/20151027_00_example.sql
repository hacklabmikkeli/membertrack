INSERT INTO 
    "PUBLIC"."PERSON" ("id", "email")
VALUES
    (1, 'example@example.com');

INSERT INTO
    "PUBLIC"."PHONE_NUMBER" ("person_id", "phone_number")
VALUES
    (1, '+123456789');

INSERT INTO
    "PUBLIC"."SERVICE" ("id", "title", "description")
VALUES
    (1, 'Tilankaytto', 'Tilankayttomaksut');

INSERT INTO
    "PUBLIC"."SERVICE_SUBSCRIPTION" 
        ("service_id",
         "person_id",
         "start_time",
         "length")
VALUES
    (1, 1, NOW(), 1000);