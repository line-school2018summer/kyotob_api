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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import kotlin.math.max
import kotlin.math.min

data class UserNameRequest(
        @JsonProperty("user_name")
        val userName: String
)
// Room一覧用のResponse
data class GetRoomResponse(
        @JsonProperty("room_id")
        val roomId: Int,
        @JsonProperty("room_name")
        val roomName: String,
        @JsonProperty("image_url")
        val imageUrl: String,
        @JsonProperty("recent_message")
        val recentMessage: String,
        @JsonProperty("created_at")
        val createdAt: Timestamp
)


data class PostGroupRequest (
        @JsonProperty("room_name")
        val roomName: String,
        @JsonProperty("user_name_list")
        val userNameList: List<UserNameRequest>,
        @JsonProperty("icon_path")
        val iconPath: String
)


@RestController
@RequestMapping(produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
class RoomController(private val userService: UserService, val roomService: RoomService, private val tokenService: TokenService, private val userDao: UserDao, private val mdao: MessageDAO) {

    //所属ルーム一覧
    @GetMapping(
            value = ["/room"]
    )
    fun getAffiliatedRoom(@RequestHeader("access_token") token: String): List<GetRoomResponse> {
        // 友達の表示名を取得する関数
        fun getFriendUserScreenName(myUserId: Int, userId1: Int, userId2: Int): User {
            val friendId = if(myUserId == userId1) userId2 else userId1
            return userService.getUserFromId(friendId)
        }
        // TokenからUserIdを取得する
        val userId = tokenService.verifyAccessToken(token)
        // UserIdからRoom一覧を取得する
        val rooms = roomService.getPairRoomsListFromUserId(userId)
        val groupRooms = roomService.getGroupRoomsListFromUserId(userId)

        // Response用に整形する
        return rooms.map {
            GetRoomResponse(
                    it.roomId,
                    getFriendUserScreenName(userId, it.userId1, it.userId2).screenName,
                    getFriendUserScreenName(userId, it.userId1, it.userId2).imageUrl,
                    it.recentMessage,
                    it.createdAt
            )
        } + groupRooms.map {
            GetRoomResponse(
                    it.id,
                    it.name,
                    it.imageUrl,
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
                     @RequestBody request: UserNameRequest): simpleRoom {
        val userId = tokenService.verifyAccessToken(token)
        //friendNameから友達のIDを取得
        val friendId = userService.getUser(request.userName).id

        val roomName = "room"
        if (userId == friendId) throw BadRequestException("自分とルームを作ろうとしている")
        val minId = min(userId, friendId)
        val maxId = max(userId, friendId)
        val pair = roomService.getPairFromTwoUserId(minId, maxId)

        //ルームが存在するかどうかで挙動が変わる
        if (pair == null) {
            val roomId = roomService.createPairRoom(minId, maxId, roomName)
            sendNotification(roomId, request.userName) // 友たちにルームが出来たことを通知
            sendNotification(roomId, userDao.getnameById(userId)) // 自分自身にも通知
            return roomService.getRoomFromRoomId(roomId)
        }
        return roomService.getRoomFromRoomId(pair.roomId)
    }

    @PostMapping(
            value = ["/room"]
    )
    fun createGroupRoom(@RequestHeader("access_token") token: String,
                    @RequestBody request: PostGroupRequest): simpleRoom{
        tokenService.verifyAccessToken(token)
        val userIdList: List<Int> = request.userNameList.map {userService.getUser(it.userName).id}
        val id = roomService.createGroupRoom(request.roomName, userIdList, request.iconPath)
        return roomService.getRoomFromRoomId(id)
    }

    @PutMapping(
            value = ["/room/{room_id}"]
    )
    fun addMemberToGroup(@RequestHeader("access_token") token: String,
                         @PathVariable("room_id") roomId: Int,
                         @RequestBody request: PostGroupRequest): ResponseEntity<HttpStatus> {
        tokenService.verifyAccessToken(token)
        roomService.updateName(roomId, request.roomName)
        if (request.userNameList.isEmpty()) throw BadRequestException("empty list")
        val userIdList = request.userNameList.map { userService.getUser(it.userName).id }
        roomService.appendUsersIntoGroup(roomId, userIdList)
        return ResponseEntity(HttpStatus.NO_CONTENT)
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
