INSERT INTO
    "PUBLIC"."SERVICE" ("id", "title", "description")
VALUES 
    ('bee4ce2c-7200-4125-874f-2e6c5cca6343', 'Jäsenmaksut', ''), 
    ('798aacc6-aafa-4d76-b503-18b2653616ef', 'Tilankäyttömaksut', '');

INSERT INTO
    "PUBLIC"."PERSON" ("id", "full_name", "email")
VALUES
    ('dedf5c4b-c223-4013-bd97-6fbf850d18d1', 'Mr. Admin', 'admin@example.com');

INSERT INTO
    "PUBLIC"."ACCOUNT" ("id", "person_id", "salt", "hash")
VALUES
    ( '995b0a1b-9361-4dcd-bf8a-8bab0c28d495'
    , 'dedf5c4b-c223-4013-bd97-6fbf850d18d1'
    , 'admin'
    , 'a90df67816fd16f347f5d8b9d7ee55fb'
    );