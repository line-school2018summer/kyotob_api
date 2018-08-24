package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

// MapperでSELECTしたときにBooleanで返す方法が分からなかったので、仕方なく作った。
data class GetMessageAuth(
        val room_id: Int
)

// MapperでSELECTしたときにBooleanで返す方法が分からなかったので、仕方なく作った。
data class UserId(
        val user_id: Int
)

// Messageの取得時に返すResponseの項目
data class GetMessageResponse(
        val created_at: Timestamp,
        val user_name: String,
        val user_screen_name: String,
        val message: String
)

// Message送信時のRequestの項目
// Tokenで認証するので、user_nameが無くても誰か分かるんですが、あったほうが、Messageに追加しやすいので付けてます。
data class SendMessage(
        val user_name: String,
        val room_id: Int,
        val content: String
)

data class Room(val id: Int,
                val name: String)

data class User(val id: Int,
                val name: String,
                val screenName: String,
                val password: String)

data class Pair(val roomId: Int,
                val userId1: Int,
                val userId2: Int)

data class Message(val messageId: Int,
                   val senderId: Int,
                   val roomId: Int,
                   val content: String,
                   @get:JsonProperty("created_at") val createdAt: Timestamp)
