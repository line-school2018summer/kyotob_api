package com.kyotob.api.mapper
import com.kyotob.api.model.GetTimerMessageResponse
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Component
@Mapper
interface TimerMessageDAO {
    // Message取得用のSQL文
    @Select(
            """
                SELECT timer_messages.created, users.name, users.screen_name, timer_messages.content, timer_messages.image_url
                FROM timer_messages
                LEFT JOIN users ON timer_messages.sender_id = users.id
                WHERE
                    timer_messages.room_id =#{roomId} AND
                    timer_messages.kidoku_num = 0 AND
                    timer_messages.timer <= now() AND
                    timer_messages.sender_id != #{recieverId}
            """
    )
    fun findMessages(roomId: Int, recieverId: Int): List<GetTimerMessageResponse>

    // 既読をつける
    @Update(
            """
                UPDATE timer_messages
                SET kidoku_num = 1
                WHERE
                    timer_messages.room_id =#{roomId} AND
                    timer_messages.kidoku_num = 0 AND
                    timer_messages.timer <= now() AND
                    timer_messages.sender_id != #{recieverId}
            """
    )
    fun updateKidoku(roomId: Int, recieverId: Int)

    // UserNameからUserIdを取得する
    @Select(
            """
                SELECT users.name
                FROM users
                WHERE name = #{userId} LIMIT 1
            """
    ) fun geIdByName(userName: String): Int?

    // users.user_idを使って、messageをINSERTする
    @Insert(
            """
                INSERT INTO `timer_messages` (`sender_id`, `room_id`, `content`, `image_url`, `timer`)
                VALUES (#{userId}, #{roomId}, #{content}, #{image}, now() + interval #{timer} hour)
            """
    )fun insertMessage(userId: Int, roomId: Int, content: String, image: String, timer: Int)
}