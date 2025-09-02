INSERT IGNORE INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin');

# TODO: alter table query, will fix later to modify ta create table statement itself

ALTER TABLE committees CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE meetings CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE committee_memberships CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
