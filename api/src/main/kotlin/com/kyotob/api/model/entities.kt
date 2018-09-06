package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
// users.user_nameからusers.user_idを割り出す用
data class UserId(
        val userId: Int
)
// Messageの取得時に返すResponseの項目
data class GetMessageResponse(
        val createdAt: Timestamp,
        val userName: String,
        val userScreenName: String,
        val content: String
)
// Message送信時のRequestの項目
// Tokenで認証するので、user_nameが無くても誰か分かるんですが、あったほうが、Messageに追加しやすいので付けてます。
data class SendMessageRequest(
        @JsonProperty("user_name") val userName: String,
        @JsonProperty("content") val content: String
)

//User登録用のクラス
data class UserRegister(
        val name: String,
        @JsonProperty("screen_name") val screenName: String,
        val password: String
)

//Userログインのクラス
data class UserLogin(
        val name: String,
        val password: String
)

//User登録のResponseでTokenを返すためのクラス
data class UserResponse(
        val token: String
)

//User検索のクラス
data class UserSearch(
        val name: String,
        val screenName: String
)

data class Room(
        val id: Int,
        val name: String
)

data class User(
        val id: Int,
        val name: String,
        val screenName: String,
        val password: String
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
