package com.kyotob.api.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Component
@Mapper
interface GroupMapper {

    @Select(
            """
                SELECT user_id
                FROM users_rooms
                WHERE room_id = #{roomId};
            """
    )
    fun findByRoomId(roomId: Int): List<Int>

    @Select(
            """
                SELECT room_id
                FROM users_rooms
                WHERE user_id = #{userId};
            """
    )
    fun findByUserId(userId: Int): List<Int>


    @Insert(
            """
                INSERT INTO users_rooms(room_id, user_id) VALUES(#{roomId}, #{userId});
            """
    )
    fun insertUsersRooms(userId: Int, roomId: Int)
}
