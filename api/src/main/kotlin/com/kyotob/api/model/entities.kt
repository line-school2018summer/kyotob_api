package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
// Messageの取得時に返すResponseの項目
data class GetMessageResponse(
        @JsonProperty("created_at") val createdAt: Timestamp,
        @JsonProperty("user_name") val userName: String,
        @JsonProperty("user_screen_name") val userScreenName: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("content_type") val contentType: String
)
// Message送信時のRequestの項目
// Tokenで認証するので、user_nameが無くても誰か分かるんですが、あったほうが、Messageに追加しやすいので付けてます。
data class SendMessageRequest(
        @JsonProperty("content") val content: String,
        @JsonProperty("content_type") val contentType: String
)

// TimerMessageの取得時に返すResponseの項目
data class GetTimerMessageResponse(
        val createdAt: Timestamp,
        val userName: String,
        val userScreenName: String,
        val content: String,
        val imageUrl: String
)
// TimerMessage送信時のRequestの項目
data class SendTimerMessageRequest(
        @JsonProperty("content") val content: String,
        @JsonProperty("image_url") val imageUrl: String,
        val timer: Int
)


//User登録用のクラス
data class UserRegister(
        val name: String,
        @JsonProperty("screen_name") val screenName: String,
        val password: String,
        @JsonProperty("image_url") val imageUrl: String = "abc.jpeg"
)

//Userログインのクラス
data class UserLogin(
        val name: String,
        val password: String
)

//User登録のResponseでTokenを返すためのクラス
data class UserResponse(
        @JsonProperty("screen_name")
        val screenName: String,
        val token: String
)

//User検索のクラス
data class UserSearch(
        val name: String,
        @JsonProperty("screen_name") val screenName: String,
        @JsonProperty("image_url") val imageUrl: String
)

data class simpleRoom(
        val id: Int,
        val name: String
)

data class Room(
        val id: Int,
        val name: String,
        val recentMessage: String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)

data class User(
        val id: Int,
        val name: String,
        val screenName: String,
        val password: String,
        @JsonProperty("image_url") val imageUrl: String
)

data class Pair(
        val roomId: Int,
        val userId1: Int,
        val userId2: Int
)

data class Token(
        val userId: Int,
        val token: String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)

data class Message(
        val messageId: Int,
        val senderId: Int,
        val roomId: Int,
        val content: String,
        @get:JsonProperty("created_at") val createdAt: Timestamp
)

data class UsersRooms(
        val roomId: Int,
        val userId: Int
)

data class Rooms(
        val roomId: Int,
        val userId1: Int,
        val userId2: Int,
        val recentMessage: String,
        val createdAt: Timestamp
)

// サーバーからWebSocketのプロトコルを使ってメッセージを送るときに使うメッセージ
data class WebSocketMessage(
        val createdAt: Timestamp,
        val screenName: String,
        val roomId: Int,
        val content: String
)

