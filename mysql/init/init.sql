USE kyotob;

-- Table 1: ルーム情報を格納するテーブルを作成
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 3: ユーザー情報を格納するテーブルを作成
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL UNIQUE,
  `screen_name` varchar(20) NOT NULL,
  `password` char(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 4: 1対1ルームの情報を格納するテーブルを作成
CREATE TABLE `pairs` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `user_id_1` int NOT NULL,
  `user_id_2` int NOT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 5: メッセージの情報を格納するテーブルを作成
CREATE TABLE `messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `room_id` int NOT NULL,
  `content` varchar(140) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 6:アクセストークン
CREATE TABLE `tokens` (
  `user_id` int UNIQUE NOT NULL,
  `token` varchar(200) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX(token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- サンプルのレコードの挿入

-- Table 1: ルーム情報

INSERT INTO rooms (id, name) VALUES (1, "ルームA");
INSERT INTO rooms (id, name) VALUES (2, "ルームB");
INSERT INTO rooms (id, name) VALUES (3, "ルームC");

-- Table 3: ユーザー情報

INSERT INTO users (id, name, screen_name, password) VALUES (1, "0918nobita", "Kodai", "password");
INSERT INTO users (id, name, screen_name, password) VALUES (2, "test", "Test User #1", "abcdefg");
INSERT INTO users (id, name, screen_name, password) VALUES (3, "test2", "Test User #2", "123456789");

-- Table 4: 1対1ルームの情報

INSERT INTO pairs (room_id, user_id_1, user_id_2) VALUES (1, 1, 2);
INSERT INTO pairs (room_id, user_id_1, user_id_2) VALUES (2, 1, 3);
INSERT INTO pairs (room_id, user_id_1, user_id_2) VALUES (3, 2, 3);

-- Table 5: メッセージの情報

INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (1, 1, 1, "あ");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (2, 2, 1, "い");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (3, 3, 1, "う");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (4, 3, 2, "え");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (5, 2, 2, "お");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (6, 1, 2, "か");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (7, 2, 3, "き");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (8, 1, 3, "く");
INSERT INTO messges (message_id, sender_id, room_id, content, created) VALUES (9, 3, 3, "け");
