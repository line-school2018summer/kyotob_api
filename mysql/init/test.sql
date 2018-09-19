-- サンプルのレコードの挿入

-- Table 1: ルーム情報

INSERT INTO `rooms` (`id`, `name`, `recent_message`) VALUES
  (1, 'ルームA', 'い'), (2, 'ルームB', 'え'), (3, 'ルームC', 'か');

-- Table 3: ユーザー情報
INSERT INTO users (`id`, `name`, `screen_name`, `password`) VALUES
  (1, '0918nobita', 'Kodai',        'password'),
  (2, 'test',       'Test User #1', 'abcdefg'),
  (3, 'test2',      'Test User #2', '123456789');

-- Table 4: 1対1ルームの情報

INSERT INTO `pairs` (`room_id`, `user_id_1`, `user_id_2`) VALUES
  (1, 1, 2), (2, 1, 3), (3, 2, 3);

-- Table 5: メッセージの情報

INSERT INTO `messages` (`message_id`, `sender_id`, `room_id`, `content`) VALUES
  (1, 1, 1, 'あ'),
  (2, 2, 1, 'い'),
  (3, 3, 2, 'う'),
  (5, 1, 2, 'え'),
  (6, 2, 3, 'お'),
  (8, 3, 3, 'か');

-- Table 5: 時間差メッセージの情報
INSERT INTO `timer_messages` (`sender_id`, `room_id`, `content`, `image_url`, `timer`) VALUES
  (1, 1, 'あ', 'abc.png', now() + interval 1 hour),
  (2, 1, 'い', 'abc.png', now() - interval 3 hour),
  (3, 2, 'う', 'abc.png', now() + interval 5 hour),
  (1, 2, 'え', 'abc.png', now() - interval 1 hour),
  (2, 3, 'お', 'abc.png', now() + interval 2 hour),
  (3, 3, 'か', 'abc.png', now() - interval 7 hour);

-- トークン
INSERT INTO `tokens` (`user_id`, `token`) VALUES (1, 'foo'), (2, 'bar'), (3, 'hoge');
