package com.kyotob.api.controller

import com.kyotob.api.service.RoomService
import com.kyotob.api.service.TokenService
import com.kyotob.api.mapper.UserDao
import com.kyotob.api.model.*
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kyotob.api.WebSocketServer
import com.kyotob.api.mapper.MessageDAO
import com.kyotob.api.service.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.time.Instant.now
import kotlin.math.max
import kotlin.math.min

data class PostPairRequest (
        @JsonProperty("friend_user_name")
        val friendUserName: String
)
// Room一覧用のResponse
data class GetRoomResponse(
        @JsonProperty("room_id")
        val roomId: Int,
        @JsonProperty("room_info")
        val roomInfo: RoomInfo,
        @JsonProperty("recent_message")
        val recentMessage: String,
        @JsonProperty("created_at")
        val createdAt: Timestamp
)

// ChatListに表示するルーム情報(名前と画像URL)
data class RoomInfo(
        @JsonProperty("room_name")
        val roomName: String,
        @JsonProperty("image_url")
        val imageUrl: String
)

@RestController
@RequestMapping(produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
class RoomController(private val userService: UserService, val roomService: RoomService, private val tokenService: TokenService, private val userDao: UserDao, private val mdao: MessageDAO) {

    //部屋一覧(デバッグ用)
    @GetMapping(
            value = ["/room/all"]
    )
    fun getAllRoom(): ArrayList<Room> {
        return roomService.getAllRoomList()
    }

    //所属ルーム一覧
    @GetMapping(
            value = ["/room"]
    )
    fun getAffiliatedRoom(@RequestHeader("access_token") token: String): List<GetRoomResponse> {
        // 友達の表示名と画像URLを取得する関数
        fun getFriendUserScreenName(myUserId: Int, userId1: Int, userId2: Int): RoomInfo {
            val friendId = if(myUserId == userId1) userId2 else userId1
            return userService.getUserFromId(friendId)
        }
        // TokenからUserIdを取得する
        val userId = tokenService.verifyAccessToken(token)
        // UserIdからRoom一覧を取得する
        val rooms = roomService.getRoomListFromUserId(userId)
        //todo: group
        // Response用に整形する
        return rooms.map {
            GetRoomResponse(
                    it.roomId,
                    getFriendUserScreenName(userId, it.userId1, it.userId2),
                    it.recentMessage,
                    it.createdAt
            )
        }
    }

    //一対一ルーム取得
    @PostMapping(
            value = ["/room/pair"]
    )
    fun getPairRoom(@RequestHeader("access_token") token: String,
                     @RequestBody request: PostPairRequest): Room {
        val userId = tokenService.verifyAccessToken(token)
        //friendNameから友達のIDを取得
        val friendId = userDao.getUser(request.friendUserName).id

        val roomName = "room"
        if (userId == friendId) throw BadRequestException("自分とルームを作ろうとしている")
        val minId = min(userId, friendId)
        val maxId = max(userId, friendId)
        val pair = roomService.getPairFromTwoUserId(minId, maxId)

        //ルームが存在するかどうかで挙動が変わる
        if (pair == null) {
            val roomId = roomService.createPairRoom(minId, maxId, roomName)
            sendNotification(roomId, request.friendUserName) // 友たちにルームが出来たことを通知
            sendNotification(roomId, userDao.getnameById(userId)) // 自分自身にも通知
            return roomService.getRoomFromRoomId(roomId)
        }
        return roomService.getRoomFromRoomId(pair.roomId)
    }

    fun sendNotification(roomId: Int, userName: String) {
        for(session in WebSocketServer.sessions) {
            try {
                if(session.pathParameters["user_name"] == userName) {
                    // ルームが出来たことを知らせる
                    session.asyncRemote.sendText(
                            jacksonObjectMapper().writeValueAsString(
                                    WebSocketMessage(
                                            Timestamp(0),
                                            userName,
                                            roomId,
                                            "新規ルームが出来ました。"
                                    )
                            )
                    )
                }
            } catch (e: Exception) { // Sessionが切断されていたときの処理
                WebSocketServer.sessions.remove(session) // Sessionを削除
            }
        }
    }
}
