package com.kyotob.api.mapper

import org.apache.ibatis.annotations.*
import com.kyotob.api.model.User

@Mapper
interface UserDao {

    //userを新しく追加する
    @Insert(
            """
                INSERT INTO users
                (name, screen_name, password)
                VALUES (#{name},#{screenName}, #{password});
            """
    )fun insertUser(name: String, screenName: String, password: String)

    //nameでuserの存在を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{name})
            """
    )fun isNameRegistered(name: String): Boolean

    //nameからuserの情報を取得
    @Select(
            """
                SELECT id, name, screen_name, password
                FROM users
                WHERE name=#{name}
            """
    )fun getUser(name: String): User

    //idからpasswordを割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE id=#{userId}
            """
    )fun idToPassword(userId:Int): String
}