package com.kyotob.api.service

import org.springframework.stereotype.Service
import com.kyotob.api.model.Room
import com.kyotob.api.model.Pair
import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.mapper.RoomMapper
import com.kyotob.api.mapper.PairMapper
import com.kyotob.api.model.Rooms
import kotlin.math.max
import kotlin.math.min

@Service
class RoomService(private val roomMapper: RoomMapper, private val pairMapper: PairMapper) {

    //テスト用
    fun getAllRoomList(): ArrayList<Room>{
        return roomMapper.getAllRooms()
    }

    fun getRoomFromRoomId(roomId: Int): Room {
        val room: Room? = roomMapper.findByRoomId(roomId)
        room ?: throw BadRequestException("no room found")
        return room
    }

    fun getPairFromRoomId(roomId: Int): Pair? {
        return pairMapper.findByRoomId(roomId)
    }
    fun getPairFromTwoUserId(userId1: Int, userId2: Int): Pair? {
        return pairMapper.findByTwoUserId(userId1, userId2)
    }

    fun getRoomListFromUserId(userId: Int): ArrayList<Rooms> {
        return pairMapper.findRoomsByUserId(userId)
    }

    fun createPairRoom(userId1: Int, userId2: Int, roomName: String): Int {
        val minUserId = min(userId1, userId2)
        val maxUserId = max(userId1, userId2)
        val room: Room = Room(id = -1, name = roomName)
        roomMapper.create(room)
        val roomId = room.id
        pairMapper.create(roomId, minUserId, maxUserId)
        return roomId
    }

    fun deleteRoom(roomId: Int): Unit {
        pairMapper.delete(roomId)
        roomMapper.delete(roomId)
    }
}