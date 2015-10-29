INSERT INTO 
    "PUBLIC"."PERSON" ("id", "email")
VALUES
    (1, 'verylongemailohmygodhowisthisevenpossibleiamconfused@example.com'),
    (2, 'john.doe@example.com'),
    (3, 'matti.meikalainen@example.com');

INSERT INTO
    "PUBLIC"."PHONE_NUMBER" ("person_id", "phone_number")
VALUES
    (1, '+123456789'),
    (1, '+251531513'),
    (2, '+164135315'),
    (2, '+631261443'),
    (3, '+727245244');

INSERT INTO
    "PUBLIC"."SERVICE" ("id", "title", "description")
VALUES
    (1, 'Jäsenmaksut', 'Jäsenmaksut'),
    (2, 'Tilankäyttö', 'Tilankäyttomaksut');

INSERT INTO
    "PUBLIC"."SERVICE_SUBSCRIPTION" 
        ("service_id",
         "person_id",
         "start_time",
         "length",
         "payment")
VALUES
    (1, 1, {ts '2015-1-1 00:00:00.00'}, 365*86400, 2000),
    (2, 1, {ts '2015-1-1 00:00:00.00'}, 30*86400, 3000),
    (2, 1, {ts '2015-9-1 00:00:00.00'}, 30*86400, 3500),
    (1, 2, {ts '2015-1-1 00:00:00.00'}, 365*86400, 2000),
    (1, 3, {ts '2015-1-1 00:00:00.00'}, 365*86400, 2000);
