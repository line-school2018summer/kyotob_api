package com.kyotob.api.controller

import com.kyotob.api.service.RoomService
import com.kyotob.api.service.TokenService
import com.kyotob.api.mapper.UserDao
import com.kyotob.api.model.Room
import com.kyotob.api.model.Token
import com.fasterxml.jackson.annotation.*
import com.kyotob.api.model.Pair
import com.kyotob.api.service.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import kotlin.math.max
import kotlin.math.min

data class PostPairRequest (
        @JsonProperty("friend_user_name")
        val friendUserName: String
)

data class GetRoomResponse (
        @JsonProperty("room_id")
        val roomId: Int,
        @JsonProperty("room_name")
        val roomName: String,
        @JsonProperty("recent_message")
        val recentMessage: String
)

@RestController
@RequestMapping(produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
class RoomController(private val userService: UserService, val roomService: RoomService, private val tokenService: TokenService, private val userDao: UserDao) {

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

        fun getFriendUserScreenName(userId: Int, pair: Pair): String {
            val friendId = if(userId == pair.userId1) pair.userId2 else pair.userId1
            return userService.getUserFromId(friendId).screenName
        }

        val userId = tokenService.verifyAccessToken(token)
        val roomList = roomService.getPairRoomListFromUserId(userId)
        //todo: group
        //todo: recentmessage
        return roomList.map { GetRoomResponse(it.id,
                getFriendUserScreenName(userId, roomService.getPairFromRoomId(it.id)!!),
                "recentMessage") }
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
            return roomService.getRoomFromRoomId(roomId)
        }
        return roomService.getRoomFromRoomId(pair.roomId)
    }
}
