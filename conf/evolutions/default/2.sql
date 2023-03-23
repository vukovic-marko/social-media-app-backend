# Tasks schema

# --- !Ups
insert into user (id, username, email, password, image_path)
values (1, "user1", "user1@domain.com", "$2a$10$0rjlnNeR27W6SDubNeHiJOwtGPHY0I5jnX3Lim0OKw2Dk0CSf7Tve", "blank-profile-picture.png");

insert into user (id, username, email, password, image_path)
values (2, "user2", "user2@domain.com", "$2a$10$tnlwTkZoYAn6Yn5txPl3q.L2s.Q0DfaGmOJFw4XbNX3J0fbKizjly", "blank-profile-picture.png");

insert into user (id, username, email, password, image_path)
values (3, "user3", "user3@domain.com", "$2a$10$6tHQToI5iYSLn51ty8eQM.MFQbc0.m9Wl1iR312Utqu2.z1jBPBZW", "blank-profile-picture.png");

insert into user (id, username, email, password, image_path)
values (4, "user4", "user4@domain.com", "$2a$10$zObN2AFuhDDsupPgXRkaleEGbY00Ll8Dmvzi8vY8d6Qs5LCEjZeyu", "blank-profile-picture.png");

insert into user (id, username, email, password, image_path)
values (5, "user5", "user5@domain.com", "$2a$10$k/xqu2iinctz8NAc/60/ruDhHi4L1J0WDncf5DsvP7B670NVsICfK", "blank-profile-picture.png");

insert into post (id, user_id, user_username, date_time, content)
values (1, 1, "user1", "2022-07-14T09:01:00","post #1");

insert into post (id, user_id, user_username, date_time, content)
values (2, 1, "user1", "2022-07-14T09:02:00","post #2");

insert into post (id, user_id, user_username, date_time, content)
values (3, 2, "user2", "2022-07-14T09:03:00","post #1");

insert into post (id, user_id, user_username, date_time, content)
values (4, 4, "user4", "2022-07-14T09:04:00","post #1");

insert into friend_request (id, sender_id, receiver_id, status)
values (1, 3, 4, 2);

insert into friend_request (id, sender_id, receiver_id, status)
values (2, 3, 5, 0);

insert into friend_request (id, sender_id, receiver_id, status)
values (3, 3, 1, 2);

insert into friendship (id, user_id, friend_id)
values (1, 3, 4);

insert into friendship (id, user_id, friend_id)
values (2, 4, 3);

insert into friendship (id, user_id, friend_id)
values (3, 3, 1);

insert into friendship (id, user_id, friend_id)
values (4, 1, 3);

insert into like_post (id, user_id, post_id, status)
values (1, 3, 4, 1);

# --- !Downs

DELETE FROM like_post;
DELETE FROM friendship;
DELETE FROM friend_request;
DELETE FROM post;
DELETE FROM user;