insert into ROLE (ID, NAME) values
(1, 'ADMIN'),
(2, 'USER')
;

insert into USER (ID, USERNAME, PASSWORD, CREATED, LAST_MODIFIED) values
(1, 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', NOW(), NOW()),
(2, 'user', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', NOW(), NOW())
;

insert into USER_ROLES (USER_ID, ROLES_ID) values
(1, 1),
(2, 2)
;
