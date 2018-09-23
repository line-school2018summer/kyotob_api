package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
import java.time.LocalDateTime

// Message の取得時に返す Response の項目
data class GetMessageResponse (
        @JsonProperty("created_at") val createdAt: Timestamp,
        @JsonProperty("user_name") val userName: String,
        @JsonProperty("user_screen_name") val userScreenName: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("content_type") val contentType: String
)

// Message 送信時の Request の項目
// Token で認証するので、user_name が無くても誰か分かるんですが、あったほうが、Message に追加しやすいので付けてます。
data class SendMessageRequest (
        @JsonProperty("content") val content: String,
        @JsonProperty("content_type") val contentType: String
)

// TimerMessage の取得時に返す Response の項目
data class GetTimerMessageResponse (
        @JsonProperty("created_at") val createdAt: Timestamp,
        @JsonProperty("user_name") val userName: String,
        @JsonProperty("user_screen_name") val userScreenName: String,
        @JsonProperty("content") val content: String,
        @JsonProperty("image_url") val imageUrl: String
)

// TimerMessage 送信時の Request の項目
data class SendTimerMessageRequest (
        @JsonProperty("content") val content: String,
        @JsonProperty("image_url") val imageUrl: String,
        val timer: Int
)


// User 登録用のクラス
data class UserRegister (
        val name: String,
        @JsonProperty("screen_name") val screenName: String,
        val password: String,
        @JsonProperty("image_url") val imageUrl: String = "abc.jpeg"
)

// User ログインのクラス
data class UserLogin (
        val name: String,
        val password: String
)

// User 登録の Response で Token を返すためのクラス
data class UserResponse (
        @JsonProperty("screen_name")
        val screenName: String,
        val token: String,
        @JsonProperty("image_url")
        val imageUrl: String
)

// User 検索のクラス
data class UserSearch (
        val name: String,
        @JsonProperty("screen_name") val screenName: String,
        @JsonProperty("image_url") val imageUrl: String
)

data class simpleRoom (
        val id: Int,
        val name: String
)

data class Room (
        val id: Int,
        val name: String,
        val recentMessage: String = "",
        @get:JsonProperty("created_at") var createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.MAX),
        @JsonProperty("image_url") val imageUrl: String = "abc.png"
)

data class User (
        val id: Int,
        val name: String,
        val screenName: String,
        val password: String,
        @JsonProperty("image_url") val imageUrl: String
)

data class Pair (
        val roomId: Int,
        val userId1: Int,
        val userId2: Int
)

data class Token (
        val userId: Int,
        val token: String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)

data class Rooms (
        val roomId: Int,
        val userId1: Int,
        val userId2: Int,
        val recentMessage: String,
        val createdAt: Timestamp
)

// サーバーから WebSocket のプロトコルを使ってメッセージを送るときに使うメッセージ
data class WebSocketMessage (
        val createdAt: Timestamp,
        val screenName: String,
        val roomId: Int,
        val content: String
)

