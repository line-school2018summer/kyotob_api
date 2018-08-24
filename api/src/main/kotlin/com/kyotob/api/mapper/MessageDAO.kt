package com.kyotob.api.mapper

import com.kyotob.api.model.GetMessageAuth // 認証時に使うモデル
import com.kyotob.api.model.GetMessageResponse // メッセージ受信用のモデル
import com.kyotob.api.model.UserId // user_idを割り出す用のモデル
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface MessageDAO {

    // Message取得用のSQL文
    @Select(
            """
                SELECT kyotob.messages.created, kyotob.users.name, kyotob.users.screen_name, kyotob.messages.content FROM kyotob.messages LEFT JOIN kyotob.users ON kyotob.messages.sender_id = kyotob.users.id WHERE kyotob.messages.room_id = #{room_id};
            """
    )
    fun findMessages(room_id: Int): List<GetMessageResponse>

    // 認証用のSQL文
    @Select(
            """
                SELECT kyotob.pairs.room_id FROM kyotob.pairs WHERE kyotob.pairs.room_id = #{room_id} and (kyotob.pairs.user_id_1 = (SELECT kyotob.tokens.user_id FROM kyotob.tokens WHERE kyotob.tokens.token = #{token}) or kyotob.pairs.user_id_2 = (SELECT kyotob.tokens.user_id FROM kyotob.tokens WHERE kyotob.tokens.token = #{token}));

            """
    )fun autorization(room_id: Int, token: String): GetMessageAuth

    // users.user_nameからusers.user_idを割り出す
    @Select(
            """
                SELECT kyotob.users.id FROM kyotob.users WHERE kyotob.users.name = #{user_name};
            """
    )fun getuserid(user_name: String): UserId

    // users.user_idを使って、messageをINSERTする
    @Insert(
            """
                INSERT INTO kyotob.messages(sender_id, room_id, content) VALUES(#{user_id}, #{room_id}, #{content});
            """
    )fun insertmessage(user_id: Int, room_id: Int, content: String)
}