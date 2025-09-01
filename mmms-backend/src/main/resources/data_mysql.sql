INSERT IGNORE INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin');

# TODO: alter table query, will fix later to modify ta create table statement itself
ALTER TABLE committees
    MODIFY committee_name VARCHAR(255)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci NOT NULL,
    MODIFY committee_description TEXT
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci NOT NULL;


ALTER TABLE meetings
    MODIFY meeting_title VARCHAR(255)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci NOT NULL,
    MODIFY meeting_description TEXT
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci,
    MODIFY meeting_held_place VARCHAR(255)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci NOT NULL;
