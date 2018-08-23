package com.kyotob.api.mapper

import com.kyotob.api.model.Room
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component

@Component
@Mapper
interface RoomMapper {

    @Select(
            """
                SELECT id, name
                FROM rooms
            """
    )
    fun getAllRooms(): ArrayList<Room>

    @Select(
            """
                SELECT id, name
                FROM rooms
                WHERE id=#{roomId}
            """
    )
    fun findByRoomId(roomId: Int): Room?

    @Select(
            """
                SELECT id, name
                FROM rooms
                WHERE name=#{roomName}
            """
    )
    fun findByRoomName(roomName: String): Room?

    @Insert(
            """
                INSERT INTO rooms (name)
                Values (#{roomName})
            """
    )
    @SelectKey(
            statement = ["SELECT LAST_INSERT_ID()"], keyProperty = "id",
            before = false, resultType = Int::class
    )
    fun create(roomName: String): Int

    @Update(
            """
                UPDATE rooms
                SET name=#{newName}
                WHERE id=#{roomId}
            """
    )
    fun updateName(roomId: Int, newName: String): Boolean

    @Delete(
            """
                DELETE FROM rooms
                WHERE id=#{roomId}
            """
    )
    fun delete(roomId: Int): Boolean
}