-- create database mmms_db;
-- use mmms_db;

CREATE TABLE app_users
(
    uid       INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50),
    lastname  VARCHAR(50),
    username  VARCHAR(50) UNIQUE,
    email     VARCHAR(100),
    password VARCHAR(100)
);

CREATE TABLE committees (
        committee_id INT AUTO_INCREMENT PRIMARY KEY,
        uuid VARCHAR(36) NOT NULL UNIQUE,
        committee_name VARCHAR(255) NOT NULL,
        committee_description TEXT NOT NULL,
        created_by INT NOT NULL,
        created_date DATE NOT NULL,
        modified_by VARCHAR(255) NOT NULL,
        modified_date DATE NOT NULL,
        status VARCHAR(255) NOT NULL,
        minuteLanguage VARCHAR(255) /*NOT NULL*/,
        openingParagraph TEXT /*NOT NULL*/,
        max_no_of_meetings INT,
        FOREIGN KEY (created_by) REFERENCES app_users(uid)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;





CREATE TABLE members_new (
                         member_id INT AUTO_INCREMENT PRIMARY KEY,
                         member_uuid VARCHAR(36) NOT NULL UNIQUE,

                         member_first_name VARCHAR(255) NOT NULL,
                         member_last_name VARCHAR(255) NOT NULL,

                         member_username VARCHAR(255) NOT NULL UNIQUE,

                         member_institution VARCHAR(255),
                         member_post VARCHAR(255),
                         member_title VARCHAR(255) NOT NULL,
                         member_email VARCHAR(255) NOT NULL,

                         member_created_by VARCHAR(255) NOT NULL,
                         member_created_date DATE NOT NULL,
                         member_modified_by VARCHAR(255) NOT NULL,
                         member_modified_date DATE NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;



CREATE TABLE committee_memberships (
           committee_id INT NOT NULL,
           uuid VARCHAR(36) NOT NULL UNIQUE,
           member_id INT NOT NULL,
           role VARCHAR(255) NOT NULL,
           PRIMARY KEY (committee_id, member_id),
           FOREIGN KEY (committee_id) REFERENCES committees(committee_id),
           FOREIGN KEY (member_id) REFERENCES members_new(member_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE meetings (
          meeting_id INT AUTO_INCREMENT PRIMARY KEY,
          uuid VARCHAR(36) NOT NULL UNIQUE,

          meeting_title VARCHAR(255) NOT NULL,
          meeting_description TEXT,
          meeting_held_date DATE NOT NULL,
          meeting_held_time TIME NOT NULL,  -- Added this line for LocalTime
          meeting_held_place VARCHAR(255) NOT NULL,
          created_by VARCHAR(255) NOT NULL,
          updated_by VARCHAR(255) NOT NULL,
          created_date DATE NOT NULL,
          updated_date DATE NOT NULL,

          committee_id INT NOT NULL,
          FOREIGN KEY (committee_id) REFERENCES committees(committee_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE meeting_attendees (
           member_id INT NOT NULL,
           meeting_id INT NOT NULL,

           PRIMARY KEY (member_id, meeting_id),
           FOREIGN KEY (member_id) REFERENCES members_new(member_id),
           FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
);

CREATE TABLE meeting_invitees(
           member_id INT NOT NULL,
           meeting_id INT NOT NULL,

           PRIMARY KEY (member_id, meeting_id),
           FOREIGN KEY (member_id) REFERENCES members_new(member_id),
           FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE decisions (
           decision_id INT AUTO_INCREMENT PRIMARY KEY,
           uuid VARCHAR(36) NOT NULL UNIQUE,

           meeting_id INT NOT NULL,
           decision TEXT,
           FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE agendas (
                           agenda_id INT AUTO_INCREMENT PRIMARY KEY,
                           uuid VARCHAR(36) NOT NULL UNIQUE,

                           meeting_id INT NOT NULL,
                           agenda TEXT,
                           FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

