package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp


data class Room(
    var roomId:   Long,
    var roomName: String
)

data class User(
        var userId:          Long,
        var userName:        String,
        var userScreenName: String,
        var password:         String
)

data class Pair(
        var roomId:   Long,
        var userId1: Long,
        var userId2: Long
)

data class Message(
        var messageId: Long,
        var senderId:  Long,
        var roomId:    Long,
        var content:  String,
        @get:JsonProperty("created_at") var createdAt: Timestamp
)
