INSERT INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin'),
       ('not_username', '{noop}password', 'username@gmail.com', 'admin', 'admin');

INSERT INTO members (
    first_name, last_name, institution, post, qualification, email,
    created_by, created_date, modified_by, modified_date, uuid, first_name_nepali, last_name_nepali
)
VALUES
    ('Hari',   'Bahadur',    'Nepal Engineering College',              'प्रा.',     'MSc IT',                  'hari.bahadur@example.com',   'username', '2025-07-04', 'username', '2025-07-04', UUID(), 'हरि', 'बहादुर'),
    ('Gita',   'Oli',        'Ministry of Education',                  'डा.',                 'MPA',                     'gita.oli@example.com',        'username', '2025-07-05', 'username', '2025-07-05', UUID(), 'गिता', 'ओली'),
    ('Bikash', 'Lama',       'Nist College',                           'डा.',                     'PhD Computer Science',    'bikash.lama@example.com',     'username', '2025-07-06', 'username', '2025-07-06', UUID(), 'विकाश', 'लामा'),
    ('Sunita', 'Maharjan',   'St. Xaviers College',                    'प्रा.',                'MSc Environmental Science','sunita.maharjan@example.com','username', '2025-07-07', 'username', '2025-07-07', UUID(), 'सुनिता', 'महार्जन'),
    ('Kamal',  'Pandey',     'Pulchowk Campus',                        'डा.',     'PhD Civil Engineering',   'kamal.pandey@example.com',    'username', '2025-07-12', 'username', '2025-07-12', UUID(), 'कमल', 'पाण्डे'),
    ('Deepa',  'Gurung',     'Patan College for Professional Studies', 'प्रा.',              'MBS',                     'deepa.gurung@example.com',    'username', '2025-07-13', 'username', '2025-07-13', UUID(), 'दीपा', 'गुरुङ'),
    ('Nabin',  'Tamang',     'Softwarica College',                     'प्रा.',                 'MSc Computer Science',    'nabin.tamang@example.com',    'username', '2025-07-14', 'username', '2025-07-14', UUID(), 'नविन', 'तामाङ'),
    ('Anita',  'Shrestha',   'Tribhuvan University',                  'डा.',       'MA Sociology',           'anita.shrestha@example.com',  'username', '2025-07-15', 'username', '2025-07-15', UUID(), 'अनिता', 'श्रेष्ठ'),
    ('Ramesh', 'Karki',      'Nepal Telecom',                         'डा.',               'BSc CSIT',               'ramesh.karki@example.com',    'username', '2025-07-16', 'username', '2025-07-16', UUID(), 'रामेश', 'कार्की'),
    ('Sita',   'Basnet',     'Kathmandu University',                  'डा.',          'PhD Biotechnology',      'sita.basnet@example.com',     'username', '2025-07-17', 'username', '2025-07-17', UUID(), 'सीता', 'बास्नेत'),
    ('Prakash','Rana',       'Nepal Police Academy',                  'डा.',     'M.Ed',                   'prakash.rana@example.com',    'username', '2025-07-18', 'username', '2025-07-18', UUID(), 'प्रकाश', 'राना'),
    ('Meena',  'Thapa',      'Nepal Rastra Bank',                     'डा.',                'MA Economics',           'meena.thapa@example.com',     'username', '2025-07-19', 'username', '2025-07-19', UUID(), 'मीना', 'थापा'),
    ('Dipesh', 'KC',         'Kathford College',                      'प्रा.',                      'MSc IT',                 'dipesh.kc@example.com',       'username', '2025-07-20', 'username', '2025-07-20', UUID(), 'दिपेश', 'के.सी.'),
    ('Sarita', 'Dhakal',     'Pokhara University',                    'प्रा.',                'PhD Management',         'sarita.dhakal@example.com',   'username', '2025-07-21', 'username', '2025-07-21', UUID(), 'सरिता', 'ढकाल'),
    ('Bijay',  'Gurung',     'Nepal Electricity Authority',           'प्रा.',      'BE Electrical',          'bijay.gurung@example.com',    'username', '2025-07-22', 'username', '2025-07-22', UUID(), 'विजय', 'गुरुङ'),
    ('Rojina', 'Maharjan',   'Prime College',                         'प्रा.',                 'MSc CSIT',               'rojina.maharjan@example.com', 'username', '2025-07-23', 'username', '2025-07-23', UUID(), 'रोजिना', 'महार्जन'),
    ('Suman',  'Bista',      'Everest Engineering College',           'डा.',                     'PhD Information Systems','suman.bista@example.com',    'username', '2025-07-24', 'username', '2025-07-24', UUID(), 'सुमन', 'बिष्ट');



-- Insert Committees
INSERT INTO committees (
    committee_name,
    committee_description,
    created_by,
    created_date,
    modified_by,
    modified_date,
    uuid,
    status,
    max_no_of_meetings
)
VALUES
    ('Academic Committee',                 'Oversee academic policies and curriculum development', 1, CURDATE(), 'username', CURDATE(), UUID(), 'ACTIVE', 10),
    ('Events Committee',                   'Plan and organize all institutional events and seminars', 1, CURDATE(), 'username', CURDATE(), UUID(),'ACTIVE', 10),
    ('Research and Development Committee', 'Promote research and innovation', 1, CURDATE(), 'username', CURDATE(), UUID(), 'ACTIVE', 10),
    ('Disciplinary Committee',             'Handle student and staff disciplinary issues', 1, CURDATE(), 'username', CURDATE(), UUID(), 'ACTIVE', 10),
    ('Student Welfare Committee',          'Addresse student concerns and well-being', 1, CURDATE(), 'username', CURDATE(), UUID(),'ACTIVE', 10),
    ('IT and Infrastructure Committee',    'Manage IT resources and campus infrastructure', 2, CURDATE(), 'username', CURDATE(), UUID(), 'ACTIVE', 10);

-- Insert Meetings
INSERT INTO meetings (
    committee_id, meeting_title, meeting_description,
    meeting_held_date, meeting_held_place, meeting_held_time,
    created_by, updated_by, created_date, updated_date, uuid
)
VALUES
    -- meetings for committee 1
    (1, 'Syllabus Update Discussion',         'Discuss Updated Syllabus',             '2025-07-18', 'Pulchowk Campus',       '14:30:00', 'username', 'username', '2025-07-13', '2025-07-13',  UUID()),
    (1, 'Annual Seminar Planning',            'Organize the annual institutional seminar',                        '2025-07-22', 'Auditorium',     '11:00:00', 'username', 'username', '2025-07-14', '2025-07-14', UUID()),
    (1, 'Research Grant Proposals Review',    'Assessment of new research funding requests',                        '2025-07-25', 'Innovation Hub', '13:00:00', 'username', 'username', '2025-07-21', '2025-07-21', UUID()),
    (1, 'Review of Recent Incidents',         'Addressing recent disciplinary cases and policy updates',            '2025-07-28', 'Admin Office 1', '10:00:00', 'username', 'username', '2025-07-22', '2025-07-22', UUID()),

    -- meetings for committee 2
    (2, 'Canteen and Hostel Feedback Session','Discussing feedback from students on facilities.',                    '2025-07-29', 'Student Lounge', '15:00:00', 'username', 'username', '2025-07-23', '2025-07-23', UUID()),
    (2, 'Campus Wi-Fi Upgrade Plan',          'Finalizing the plan to upgrade network infrastructure.',              '2025-08-01', 'IT Department',  '11:00:00', 'username', 'username', '2025-07-25', '2025-07-25', UUID());

-- Insert Decisions
INSERT INTO decisions (meeting_id, decision, uuid)
VALUES
    -- decisions for meeting 1
    (1, 'पाठ्यक्रममा नविकरणीय ऊर्जा सम्बन्धी नयाँ मोड्युल थपिनेछ।', UUID()),
    (1, 'हालको डाटा स्ट्रक्चर पाठ्यक्रमलाई नयाँ सामग्रीहरूसँग अद्यावधिक गरिनेछ।', UUID()),
    (1, 'आधुनिक इन्जिनियरिङ प्रवृत्तिहरूमा अतिथि व्याख्यान शृङ्खला आयोजना गरिनेछ।', UUID()),
    (1, 'अन्तिम वर्षको परियोजनाका लागि अन्तर-विभागीय प्रस्ताव स्वीकृत गरिएको छ।', UUID()),
    (1, 'सेमिनारको शीर्षक "प्रविधिमा नवीनता" रहनेछ।', UUID()),

    -- decisions for meeting 2
    (2, 'क्याम्पसभरि Wi-Fi 6 मा स्तरोन्नति गर्ने प्रस्ताव स्वीकृत गरिएको छ।', UUID()),
    (2, 'दुई हप्ताभित्र तीन फरक विक्रेताबाट दरभाउपत्रहरू सङ्कलन गरिनेछ।', UUID()),
    (2, 'पुस्तकालयका कम्प्युटरहरू नयाँ SSD र थप RAM सहित स्तरोन्नति गरिनेछ।', UUID()),
    (2, 'सञ्जाल सुरक्षालाई सुदृढ गर्न नयाँ फायरवाल कार्यान्वयन गरिनेछ।', UUID()),

    -- decisions for meeting 3
    (3, 'मुख्य वक्ताहरूको छनोट अर्को सातासम्ममा अन्तिम गरिनेछ।', UUID()),
    (3, 'कार्यक्रमको बजेट रू. ३,००,००० मा अन्तिम गरिएको छ।', UUID()),
    (3, 'लजिस्टिक व्यवस्थापनका लागि विद्यार्थी स्वयंसेवक समिति गठन गरिनेछ।', UUID()),

    -- decisions for meeting 4
    (4, 'क्वान्टम कम्प्युटिङ अनुसन्धानका लागि भौतिकशास्त्र विभागको अनुदान प्रस्ताव स्वीकृत गरिएको छ।', UUID()),
    (4, 'विद्यार्थी परियोजनाका लागि बौद्धिक सम्पत्ति अधिकारसम्बन्धी नयाँ नीति प्रारूप तयार गरिनेछ।', UUID()),
    (4, 'नयाँ 3D प्रिन्टिङ प्रयोगशालाका लागि बजेट सैद्धान्तिक रूपमा स्वीकृत गरिएको छ; अन्तिम दरभाउ आवश्यक।', UUID()),
    (4, 'कृत्रिम बुद्धिमत्ता अनुसन्धानमा काठमाडौँ विश्वविद्यालयसँग सहकार्य सुरु गरिनेछ।', UUID()),

    -- decisions for meeting 5
    (5, 'मुद्दा #001: नक्कली कार्यमा संलग्न विद्यार्थीलाई औपचारिक चेतावनी दिइनेछ र उसले कार्य पुनः पेश गर्नुपर्नेछ।', UUID()),
    (5, 'परीक्षासम्बन्धी आचारसंहिता अद्यावधिक गरी सबै विद्यार्थीलाई पठाइनेछ।', UUID()),
    (5, 'शैक्षिक निष्ठामाथि कार्यशाला सबै पहिलो वर्षका विद्यार्थीका लागि अनिवार्य बनाइनेछ।', UUID()),
    (5, 'मुद्दा #002 छात्रावास नियम उल्लङ्घनसम्बन्धी निर्णय थप प्रमाण नआएसम्मका लागि स्थगित गरिएको छ।', UUID()),

    -- decisions for meeting 6
    (6, 'हालको क्यान्टिन सेवाप्रदायकसँगको सम्झौता नकारात्मक प्रतिक्रियाका आधारमा पुनरावलोकन गरिनेछ।', UUID()),
    (6, 'छात्रावास ब्लक B मा नयाँ पानी शुद्धीकरण प्रणाली जडान गरिनेछ।', UUID()),
    (6, 'परीक्षा अवधिमा मानसिक स्वास्थ्य परामर्श सेवा सप्ताहन्तमा पनि विस्तार गरिनेछ।', UUID()),
    (6, 'नयाँ खेलकुद सुविधाको माग मूल्याङ्कन गर्न सर्वेक्षण गरिनेछ।', UUID());

-- Insert Committee Memberships
INSERT INTO committee_memberships (committee_id, member_id, role, uuid)
VALUES
    -- Committee 1 (8 members)
    (1, 1, 'Member-Secretary', UUID()),
    (1, 2, 'Coordinator', UUID()),
    (1, 3, 'Member', UUID()),
    (1, 4, 'Member', UUID()),
    (1, 5, 'Member-Secretary', UUID()),
    (1, 6, 'Member', UUID()),

    -- Committee 2 (5 members)
    (2, 9,  'Coordinator', UUID()),
    (2, 10, 'Member', UUID()),
    (2, 11, 'Member-Secretary', UUID()),
    (2, 12, 'Member', UUID()),
    (2, 13, 'Member', UUID()),

    -- Committee 3 (3 members)
    (3, 14, 'Coordinator', UUID()),
    (3, 15, 'Member', UUID()),
    (3, 16, 'Member', UUID()),

    -- Committee 4 (1 member)
    (4, 17, 'Coordinator', UUID()),
    (4,  7, 'Member', UUID()),
     (4, 8, 'Member', UUID());




INSERT INTO meeting_attendees (member_id, meeting_id)
VALUES
    -- Meeting 1 attendees. meeting 1 belongs to committee 1, so only members belonging to committee 1 should be present

    -- Furthermore while populatig the meeting_attendees, make sure that the meeting coordinator is part of the meeting_attendee
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1),

    -- Meeting 2 attendees
    (3, 2),
    (1, 2),
    (4, 2),
    (5, 2),
    (7, 2),

    -- Meeting 3 attendees
    (5, 3),
    (8, 3),
    (1, 3),
    (2, 3),

    -- Meeting 4 attendees
    (1, 4),
    (2, 4),
    (7, 4),
    (8, 4),

    -- Meeting 5 attendees
    (9, 5),
    (10, 5),
    (11, 5),
    (12, 5),

    -- Meeting 6 attendees
    (10, 6),
    (12, 6),
    (9, 6),
    (11, 6);


INSERT INTO agendas (meeting_id, agenda, uuid)
VALUES
    (1, 'क्याम्पसभरि Wi-Fi 6 मा स्तरोन्नति गर्ने प्रस्ताव स्वीकृत गरिएको छ।', UUID()),
    (1, 'दुई हप्ताभित्र तीन फरक विक्रेताबाट दरभाउपत्रहरू सङ्कलन गरिनेछ।', UUID()),
    (1, 'पुस्तकालयका कम्प्युटरहरू नयाँ SSD र थप RAM सहित स्तरोन्नति गरिनेछ।', UUID()),
    (1, 'सञ्जाल सुरक्षालाई सुदृढ गर्न नयाँ फायरवाल कार्यान्वयन गरिनेछ।', UUID());
