CREATE TABLE USERS (
    email VARCHAR (250),
    username VARCHAR(250) PRIMARY KEY,
    password VARCHAR(250)
);

CREATE TABLE USER_PERMISSIONS(
    id uuid not null PRIMARY KEY,
    sid VARCHAR (250) NOT NULL,
    target VARCHAR (250) NOT NULL,
    target_id VARCHAR (250),
    permission VARCHAR(250) NOT NULL
);
