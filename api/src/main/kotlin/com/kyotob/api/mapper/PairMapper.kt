package com.kyotob.api.mapper


import com.kyotob.api.model.*
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

    // pairsテーブルとroomsテーブルを内部結合させて新着メッセージと時間を一緒に取得する
    @Select(
            """
                SELECT pairs.room_id, pairs.user_id_1, pairs.user_id_2, rooms.content, rooms.created
                FROM pairs INNER JOIN rooms
                ON pairs.room_id = rooms.id
                WHERE pairs.user_id_1=#{userId} OR pairs.user_id_2=#{userId}
                ORDER BY rooms.created DESC
            """
    ) fun findRoomsByUserId(userId: Int): ArrayList<Rooms>

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
