-- サンプルのレコードの挿入

-- Table 1: ルーム情報

INSERT INTO `rooms` (`id`, `name`) VALUES (1, 'ルームA');
INSERT INTO `rooms` (`id`, `name`) VALUES (2, 'ルームB');
INSERT INTO `rooms` (`id`, `name`) VALUES (3, 'ルームC');

-- Table 3: ユーザー情報
INSERT INTO users (`id`, `name`, `screen_name`, `password`) VALUES (1, '0918nobita', 'Kodai', 'password');
INSERT INTO users (`id`, `name`, `screen_name`, `password`) VALUES (2, 'test', 'Test User #1', 'abcdefg');
INSERT INTO users (`id`, `name`, `screen_name`, `password`) VALUES (3, 'test2', 'Test User #2', '123456789');

-- Table 4: 1対1ルームの情報

INSERT INTO `pairs` (`room_id`, `user_id_1`, `user_id_2`) VALUES (1, 1, 2);
INSERT INTO `pairs` (`room_id`, `user_id_1`, `user_id_2`) VALUES (2, 1, 3);
INSERT INTO `pairs` (`room_id`, `user_id_1`, `user_id_2`) VALUES (3, 2, 3);

-- Table 5: メッセージの情報

INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (1, 1, 1, 'あ');
INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (2, 2, 1, 'い');
INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (3, 3, 2, 'う');
INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (5, 1, 2, 'え');
INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (6, 2, 3, 'お');
INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES (8, 3, 3, 'か');

-- トークン
INSERT INTO `tokens` (`user_id`, `token`) VALUES (1, 'foo')
INSERT INTO `tokens` (`user_id`, `token`) VALUES (2, 'bar')
INSERT INTO `tokens` (`user_id`, `token`) VALUES (3, 'hoge')
