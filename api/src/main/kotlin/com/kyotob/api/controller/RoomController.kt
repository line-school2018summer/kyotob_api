package com.kyotob.api.controller

import com.kyotob.api.service.RoomService
import com.kyotob.api.service.TokenService
import com.kyotob.api.mapper.UserDao
import com.kyotob.api.model.Room
import com.fasterxml.jackson.annotation.*
import com.kyotob.api.mapper.groupMapper
import com.kyotob.api.service.UserService
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import kotlin.math.max
import kotlin.math.min

data class UserNameRequest(
        @JsonProperty("user_name")
        val userName: String
)

data class PostGroupRequest (
        @JsonProperty("room_name")
        val roomName: String,
        @JsonProperty("user_name_list")
        val userNameList: List<UserNameRequest>
)


@RestController
@RequestMapping(produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
class RoomController(private val roomService: RoomService, private val tokenService: TokenService, private val userService: UserService) {

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
    fun getAffiliatedRoom(@RequestHeader("access_token") token: String): List<Room> {
        val uId = tokenService.verifyAccessToken(token)
        return roomService.getRoomListFromUserId(uId)
    }


    //一対一ルーム取得
    @PostMapping(
            value = ["/room/pair"]
    )
    fun getPairRoom(@RequestHeader("access_token") token: String,
                     @RequestBody request: UserNameRequest): Room {
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
            return roomService.getRoomFromRoomId(roomId)
        }
        return roomService.getRoomFromRoomId(pair.roomId)
    }

    @PostMapping(
            value = ["/room"]
    )
    fun createGroupRoom(@RequestHeader("access_token") token: String,
                    @RequestBody request: PostGroupRequest): Room{
        tokenService.verifyAccessToken(token)
        val userIdList: List<Int> = request.userNameList.map {userService.getUser(it.userName).id}
        roomService.createGroupRoom(request.roomName, userIdList)
    }

    @PutMapping(
            value = ["/room/{room_id}"]
    )
    fun addMemberToGroup(@RequestHeader("access_token") token: String,
                         @PathVariable("room_id") roomId: Int,
                         @RequestBody request: PostGroupRequest): ResponseEntity<HttpStatus> {
        tokenService.verifyAccessToken(token)
        roomService.updateName(roomId,request.roomName)
        if (request.userNameList.isEmpty()) throw BadRequestException("empty list")
        val userIdList = request.userNameList.map {userService.getUser(it.userName).id}
        roomService.appendUsersIntoGroup(roomId, userIdList)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
