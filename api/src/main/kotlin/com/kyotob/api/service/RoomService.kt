package com.kyotob.api.service

import org.springframework.stereotype.Service
import com.kyotob.api.model.simpleRoom
import com.kyotob.api.model.Pair
import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.controller.InternalServerError
import com.kyotob.api.mapper.RoomMapper
import com.kyotob.api.mapper.PairMapper
import com.kyotob.api.mapper.groupMapper
import com.kyotob.api.model.Room
import com.kyotob.api.model.Rooms
import kotlin.math.max
import kotlin.math.min

@Service
class RoomService(private val roomMapper: RoomMapper, private val pairMapper: PairMapper, private val groupMapper: groupMapper) {

    //テスト用
    fun getAllRoomList(): ArrayList<simpleRoom> {
        return roomMapper.getAllRooms()
    }

    fun getRoomFromRoomId(roomId: Int): simpleRoom {
        val simpleRoom: simpleRoom? = roomMapper.findByRoomId(roomId)
        simpleRoom ?: throw BadRequestException("no room found")
        return simpleRoom
    }

    fun getPairFromRoomId(roomId: Int): Pair? {
        return pairMapper.findByRoomId(roomId)
    }

    fun getUsersFromRoomId(roomId: Int): List<Int> {
        return groupMapper.findByRoomId(roomId)
    }

    fun getPairFromTwoUserId(userId1: Int, userId2: Int): Pair? {
        return pairMapper.findByTwoUserId(userId1, userId2)
    }

    fun getRoomListFromUserId(userId: Int): List<simpleRoom> {
        val pairs = pairMapper.findByUserId(userId)
        val groupRoomIds = groupMapper.findByUserId(userId)
        //pairsテーブルとroomsテーブルは整合性がなければならない
        //nullがあれば例外を投げる
        val RoomList = pairs.map {
            roomMapper.findByRoomId(it.roomId) ?: throw InternalServerError("pairsとroomsとに整合性がない")
        } +
        groupRoomIds.map {
                    roomMapper.findByRoomId(it) ?: throw InternalServerError("groupsとroomsに整合性がない")
        }
        return RoomList
    }

    fun getPairRoomsListFromUserId(userId: Int): ArrayList<Rooms> {
        return pairMapper.findRoomsByUserId(userId)
    }

    fun getGroupRoomsListFromUserId(userId: Int): List<Room> {
        val idList = groupMapper.findByUserId(userId)
        return idList.map {roomMapper.findRoomById(it)!!}
    }

    fun createPairRoom(userId1: Int, userId2: Int, roomName: String): Int {
        val minUserId = min(userId1, userId2)
        val maxUserId = max(userId1, userId2)
        val simpleRoom: simpleRoom = simpleRoom(id = -1, name = roomName)
        roomMapper.create(simpleRoom)
        val roomId = simpleRoom.id
        pairMapper.create(roomId, minUserId, maxUserId)
        return roomId
    }

    fun createGroupRoom(roomName: String, userIdList: List<Int>) : Int {
        val simpleRoom: simpleRoom = simpleRoom(id = -1, name = roomName)
        roomMapper.create(simpleRoom)
        val roomId = simpleRoom.id
        userIdList.distinct().map {groupMapper.insertUsersRooms(it, roomId)}
        return roomId
    }

    fun appendUsersIntoGroup(roomId: Int, userIdList: List<Int>) {
        userIdList.distinct().map {groupMapper.insertUsersRooms(it, roomId)}
    }

    fun deleteRoom(roomId: Int): Unit {
        pairMapper.delete(roomId)
        roomMapper.delete(roomId)
    }

    fun updateName(roomId: Int, newName: String): Unit {
        roomMapper.updateName(roomId, newName)
    }
}
