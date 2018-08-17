package com.kyotob.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp


data class Room(
    var room_id:   Long,
    var room_name: String
)

data class user(
        var user_id:          Long,
        var user_name:        String,
        var user_screen_name: String,
        var password:         String
)

data class pair(
        var room_id:   Long,
        var user_id_1: Long,
        var user_id_2: Long
)

data class message(
        var message_id: Long,
        var sender_id:  Long,
        var room_id:    Long,
        var content:  String,
        @get:JsonProperty("created_at") var created_at: Timestamp
)
