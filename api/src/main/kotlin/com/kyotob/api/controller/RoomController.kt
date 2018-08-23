package com.kyotob.api.controller

import com.kyotob.api.service.RoomService
import com.kyotob.api.service.TokenService
import com.kyotob.api.model.Room
import com.kyotob.api.model.Token
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import kotlin.math.max
import kotlin.math.min

data class PostPairRequest (
        val friendUserName: String,
        val roomName: String
)
@RestController
class RoomController(private val roomService: RoomService, private val tokenService: TokenService) {

    //部屋一覧(デバッグ用)
    @GetMapping(
            value = ["/room/all"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getAllRoom(): ArrayList<Room> {
        return roomService.getAllRoomList()
    }

    //所属ルーム一覧
    @GetMapping(
            value = ["/room"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getAffiliatedRoom(@RequestParam("access_token") token: String): List<Room> {
        val uId = tokenService.verifyAccessToken(token)
        return roomService.getRoomListFromUserId(uId)
    }


    //一対一ルーム取得
    @PostMapping(
            value = ["/room/pair"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getPairRoom(@RequestParam("access_token") token: String,
                     @RequestBody request: PostPairRequest): Room {
        val uId = tokenService.verifyAccessToken(token)
        //Todo:friendNameから友達のIDを取得
        // とりあえず100
        val friendId = 100
        val roomName = request.roomName
        if (uId == friendId) throw BadRequestException("自分とルームを作ろうとしている")
        val minId = min(uId,friendId)
        val maxId = max(uId,friendId)
        val pair = roomService.getPairFromTwoUserId(minId, maxId)
        if (pair == null) {
            val roomId = roomService.createPairRoom(minId, maxId, roomName)
            return roomService.getRoomFromRoomId(roomId)
        }
        return roomService.getRoomFromRoomId(pair.roomId)
    }
}