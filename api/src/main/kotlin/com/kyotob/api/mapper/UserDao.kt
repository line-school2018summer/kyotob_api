package com.kyotob.api.mapper

import com.kyotob.api.controller.RoomInfo
import org.apache.ibatis.annotations.*
import com.kyotob.api.model.User
import org.springframework.stereotype.Component

@Component
@Mapper
interface UserDao {

    //userを新しく追加する
    @Insert(
            """
                INSERT INTO users
                (name, screen_name, password, user_image)
                VALUES (#{name},#{screenName}, #{password}, #{userImage});
            """
    )fun insertUser(name: String, screenName: String, password: String, userImage: String)

    //nameでuserの存在を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{name})
            """
    )fun isNameRegistered(name: String): Boolean

    //nameからuserの情報を取得
    @Select(
            """
                SELECT id, name, screen_name, password, user_image
                FROM users
                WHERE name=#{name}
            """
    )fun getUser(name: String): User

    // Room一覧の表示名と画像URLを取得するSQL
    @Select(
            """
                SELECT screen_name, user_image
                From users
                WHERE id=#{id}
            """
    )fun getUserFromId(id: Int): RoomInfo?

    @Select(
            """
                SELECT name
                FROM users
                WHERE id=#{id}
            """
    )fun getnameById(id: Int): String

    //idからpasswordを割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE id=#{userId}
            """
    )fun idToPassword(userId:Int): String

    @Update(
            """
                UPDATE users
                SET screen_name=#{newScreenName}
                WHERE id=#{userId}
            """
    )fun updateScreenName(userId: Int, newScreenName: String): Unit
}
