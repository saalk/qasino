-- privileges
INSERT IGNORE INTO "privilege" VALUES
(1,'READ_PRIVILEGE'),
(2,'WRITE_PRIVILEGE');
-- roles
INSERT IGNORE INTO "role" VALUES
(1,'ROLE_ADMIN'),
(2,'ROLE_USER');
-- roles privileges
INSERT IGNORE INTO "roles_privileges" VALUES
(2,1),
(2,2),
(1,2);
-- users
INSERT IGNORE INTO "visitor"
("alias_seq","balance","enabled","is_using2fa","month","secured_loan", "week","weekday" , "year" , "visitor_id", "created" , "username","alias", "email", "password");
VALUES
(1, 0, FALSE, FALSE, 7, 0, '34', 25, 2024, 1, '20240825-23:58-31454', 'user', 'User', 'user@email.com', '$2a$10$tKrPCqGgH0fmdjr2Z2zHF.McalzxCVJZ4CO4wiY8M4D/5tlVuBWui'),
(1, 0, FALSE, FALSE, 7, 0, '35', 26, 2024, 2, '20240826-00:01-03961', 'admin', 'Admin', 'admin@email.com', '$2a$10$wOQI1gNBUGDiHlOl4TGC9e1ZqQDzDjAmtRwDzl6kuFQ.pvMtV13ry'),
(1, 973, FALSE, FALSE, 7, 973, '35', 26, 2024, 3, '20240826-00:06-40753', '123', 'alias123', 'email@acme.com', '$2a$10$IjY1/i2fgfyxK8YOcbjZGuLOJWYFuow19V31mncIJE0wevee/DUNm');
-- user roles
INSERT IGNORE INTO "users_roles"
(2, 1),
(2, 2),
(1, 2);
