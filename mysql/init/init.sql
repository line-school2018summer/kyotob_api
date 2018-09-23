USE kyotob;

-- Table 1: ルーム情報を格納するテーブルを作成
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `recent_message` varchar(140) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `image_url` varchar(40) NOT NULL DEFAULT 'abc.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 3: ユーザー情報を格納するテーブルを作成
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL UNIQUE,
  `screen_name` varchar(20) NOT NULL,
  `password` char(64) NOT NULL,
  `user_image` varchar(40) NOT NULL DEFAULT 'abc.png',
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
  `content_type` varchar(10) NOT NULL DEFAULT "string",
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

-- Table 7: 時間指定メッセージの情報を格納するテーブルを作成
CREATE TABLE `timer_messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `room_id` int NOT NULL,
  `content` varchar(140) NOT NULL,
  `image_url` varchar(140) NOT NULL DEFAULT 'def.png',
  `kidoku_num` int NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `timer` timestamp NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 8: グループルーム情報を格納するテーブルを作成
CREATE TABLE `users_rooms` (
  `room_id` int NOT NULL,
  `user_id` int NOT NULL,
  unique(`room_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
