package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp


data class Room(
    var roomId:   Int,
    var roomName: String
)

data class User(
        var userId:          Int,
        var userName:        String,
        var userScreenName: String,
        var password:         String
)

data class Pair(
        var roomId:   Int,
        var userId1: Int,
        var userId2: Int
)

data class Message(
        var messageId: Int,
        var senderId:  Int,
        var roomId:    Int,
        var content:  String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)

data class Token(
        var userId: Int,
        var token: String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)
