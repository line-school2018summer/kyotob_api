package com.kyotob.api.mapper

import com.kyotob.api.model.Room
import com.kyotob.api.model.simpleRoom
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
    fun getAllRooms(): ArrayList<simpleRoom>

    @Select(
            """
                SELECT id, name, recent_message, created, image_url
                FROM rooms
                WHERE id = #{id};
            """
    )
    fun findRoomById(id: Int): Room?

    @Select(
            """
                SELECT id, name
                FROM rooms
                WHERE id=#{roomId}
            """
    )
    fun findByRoomId(roomId: Int): simpleRoom?

    @Select(
            """
                SELECT id, name
                FROM rooms
                WHERE name=#{roomName}
            """
    )
    fun findByRoomName(roomName: String): simpleRoom?

    @Insert(
            """
                INSERT INTO rooms (name, recent_message, image_url)
                Values (#{name}, "", #{imageUrl})
            """
    )
    @SelectKey(
            statement = ["SELECT @@IDENTITY"], keyProperty = "id",
            before = false, resultType = Int::class
    )
    fun create(insertRoom: Room): Unit

    @Update(
            """
                UPDATE rooms
                SET name=#{newName}
                WHERE id=#{roomId}
            """
    )
    fun updateName(roomId: Int, newName: String): Boolean

    @Update(
            """
                UPDATE rooms
                SET recent_message=#{recentMessage}, created=CURRENT_TIMESTAMP
                WHERE id=#{roomId}
            """
    )
    fun updateRecentMessage(recentMessage: String, roomId: Int): Boolean

    @Delete(
            """
                DELETE FROM rooms
                WHERE id=#{roomId}
            """
    )
    fun delete(roomId: Int): Boolean
}