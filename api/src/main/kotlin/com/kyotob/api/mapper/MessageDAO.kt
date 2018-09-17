package com.kyotob.api.mapper
import com.kyotob.api.model.GetMessageResponse // メッセージ受信用のモデル
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
@Mapper
interface MessageDAO {
    // Message取得用のSQL文
    @Select(
            """
                SELECT messages.created, users.name, users.screen_name, messages.content
                FROM messages
                LEFT JOIN users ON messages.sender_id = users.id
                WHERE messages.room_id = #{roomId};
            """
    )
    fun findMessages(roomId: Int): List<GetMessageResponse>?
    // users.user_nameからusers.user_idを割り出す
    @Select(
            """
                SELECT users.id
                FROM users
                WHERE users.name = #{user_name};
            """
    )fun getUserId(user_name: String): Int?
    // users.user_idを使って、messageをINSERTする
    @Insert(
            """
                INSERT INTO messages(sender_id, room_id, content) VALUES(#{userId}, #{roomId}, #{content});
            """
    )fun insertMessage(userId: Int, roomId: Int, content: String)

    // roomにuserが存在するかを確認する
    @Select(
            """
                select EXISTS(select 1 from pairs where room_id = #{roomId} and (user_id_1 = #{userId} or user_id_2 = #{userId}));
            """
    )fun userExitInRoom(roomId: Int, userId: Int):Boolean
}