package com.kyotob.api.mapper

import org.apache.ibatis.annotations.*
import com.kyotob.api.model.User
import org.springframework.stereotype.Component

@Component
@Mapper
interface UserDao {

    // user を新しく追加する
    @Insert(
            """
                INSERT INTO users
                (name, screen_name, password, user_image)
                VALUES (#{name},#{screenName}, #{password}, #{userImage});
            """
    ) fun insertUser(name: String, screenName: String, password: String, userImage: String)

    // name で user の存在を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{name})
            """
    ) fun isNameRegistered(name: String): Boolean

    // name から user の情報を取得
    @Select(
            """
                SELECT id, name, screen_name, password, user_image
                FROM users
                WHERE name=#{name}
            """
    ) fun getUser(name: String): User

    @Select (
            """
                SELECT id, name, screen_name, password, user_image
                FROM users
                WHERE id=#{id}
            """
    ) fun findUserById(id: Int): User?


    @Select(
            """
                SELECT name
                FROM users
                WHERE id=#{id}
            """
    ) fun getNameById(id: Int): String

    // id から password を割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE id=#{userId}
            """
    ) fun idToPassword(userId:Int): String

    @Update(
            """
                UPDATE users
                SET screen_name=#{newScreenName}
                WHERE id=#{userId}
            """
    ) fun updateScreenName(userId: Int, newScreenName: String): Unit
}
