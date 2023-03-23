# Tasks schema

# --- !Ups

CREATE TABLE user(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(40) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    image_path varchar(60)
);

CREATE TABLE post(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    user_username VARCHAR(40) NOT NULL,
    date_time TEXT NOT NULL,
    content TEXT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (user_username) REFERENCES user(username)
);

CREATE TABLE friend_request(
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status INT NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES  user(id),
    FOREIGN KEY (receiver_id) REFERENCES user(id)
);

CREATE TABLE friendship(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (friend_id) REFERENCES user(id)
);

CREATE TABLE like_post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    status INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (post_id) REFERENCES post(id)
);



# --- !Downs

DROP TABLE like_post;
DROP TABLE friendship;
DROP TABLE friend_request;
DROP TABLE post;
DROP TABLE user;