package com.kyotob.api.mapper

import com.kyotob.api.model.Room
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component

@Component
@Mapper
interface RoomMapper {

    @Select(
            """
                SELECT room_id, room_name
                FROM rooms
            """
    )
    fun getAllRooms(): ArrayList<Room>

    @Select(
            """
                SELECT room_id, room_name
                FROM rooms
                WHERE room_id=#{roomId}
            """
    )
    fun findByRoomId(roomId: Int): Room?

    @Select(
            """
                SELECT room_id, room_name
                FROM rooms
                WHERE room_name=#{roomName}
            """
    )
    fun findByRoomName(roomName: String): Room?

    @Insert(
            """
                INSERT INTO rooms (room_name)
                Values (#{roomName})
            """
    )
    @SelectKey(
            statement = ["SELECT LAST_INSERT_ID()"], keyProperty = "room_id",
            before = false, resultType = Int::class
    )
    fun create(roomName: String): Int

    @Update(
            """
                UPDATE rooms
                SET room_name=#{newName}
                WHERE room_id=#{roomId}
            """
    )
    fun updateName(roomId: Int, newName: String): Boolean

    @Delete(
            """
                DELETE FROM rooms
                WHERE room_id=#{roomId}
            """
    )
    fun delete(roomId: Int): Boolean
}