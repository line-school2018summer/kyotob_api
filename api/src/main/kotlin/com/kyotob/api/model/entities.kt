package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class Room(val roomId: Int,
                val roomName: String)

data class User(val userId: Int,
                val userName: String,
                val userScreenName: String,
                val password: String)

data class Pair(val roomId: Int,
                val userId1: Int,
                val userId2: Int)

data class Message(val messageId: Int,
                   val senderId: Int,
                   val roomId: Int,
                   val content: String,
                   @get:JsonProperty("created_at") val createdAt: Timestamp)
