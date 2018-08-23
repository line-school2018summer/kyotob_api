package com.kyotob.api.service

import org.springframework.stereotype.Service
import com.kyotob.api.model.Room
import com.kyotob.api.model.Pair
import com.kyotob.api.controller.InternalServerError
import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.mapper.RoomMapper
import com.kyotob.api.mapper.PairMapper

@Service
class RoomService(private val roomMapper: RoomMapper, private val pairMapper: PairMapper) {

    fun getAllRoomList(): ArrayList<Room>{
        return roomMapper.getAllRooms()
    }

    fun getRoomFromRoomId(roomId: Int): Room {
        val room: Room? = roomMapper.findByRoomId(roomId)
        room ?: throw BadRequestException("no room found")
        return room
    }

    fun getRoomListFromUserId(userId: Int): List<Room> {
        val pairs = pairMapper.findByUserId(userId)
        //pairsテーブルとroomsテーブルは整合性がなければならない
        //nullがあれば例外を投げる
        return pairs.map {roomMapper.findByRoomId(it.roomId) ?:
                            throw InternalServerError("pairsとroomsとに整合性がない")}
    }

    fun createPairRoom(userId1: Int, userId2: Int, roomName: String): Unit {
        val roomId: Int = roomMapper.create(roomName)
        pairMapper.create(roomId, userId1, userId2)
    }

    fun deleteRoom(roomId: Int): Unit {
        pairMapper.delete(roomId)
        roomMapper.delete(roomId)
    }
}