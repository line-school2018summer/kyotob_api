package com.kyotob.api.mapper


import com.kyotob.api.model.Pair
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component

@Component
@Mapper
interface PairMapper {

    @Select(
            """
                FROM pairs
            """
    )
    fun getAllPairs(): ArrayList<Pair>

    @Select(
            """
                SELECT room_id, user_id_1, user_id_2
                FROM pairs
                WHERE room_id=#{roomId}
            """
    )
    fun findByRoomId(roomId: Int): Pair?

    @Select(
            """
                SELECT room_id, user_id_1, user_id_2
                FROM pairs
                WHERE user_id_1=#{userId} OR user_id_2=#{userId}
            """
    )
    fun findByUserId(userId: Int): ArrayList<Pair>

    @Select(
            """
                SELECT room_id, user_id_1, user_id_2
                FROM pairs
                WHERE user_id_1=#{userId1} AND user_id_2=#{userId2}
            """
    )
    fun findByTwoUserId(userId1: Int, userId2: Int): Pair?

    @Insert(
            """
                INSERT INTO pairs
                Values (#{roomId}, #{userId1}, #{userId2})
            """
    )
    fun create(roomId: Int, userId1: Int, userId2: Int): Unit

    @Delete(
            """
                DELETE FROM pairs
                WHERE room_id=#{roomId}
            """
    )
    fun delete(roomId: Int): Boolean
}
