package com.kyotob.api.mapper

import org.apache.ibatis.annotations.*

@Mapper
interface UserDAO {

    //Userを新しく追加するSQL文
    @Insert(
            """
                INSERT INTO users
                (name, screen_name, password)
                VALUES (#{name},#{screenName}, #{password});
            """
    )fun insertUser(name: String, screenName: String, password: String)

    //nameからpasswordを割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE name=#{name}
            """
    )fun getPassword(name: String): String

    //idからpasswordを割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE id=#{userId}
            """
    )fun idToPassword(userId:Int): String

    //nameからidを割り出す
    @Select(
            """
                SELECT id
                FROM users
                WHERE name=#{name}
            """
    )fun getId(name: String): Int


    //Userが存在するかどうか判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{user_name})
            """
    )fun isUserRegistered(user_name: String): Boolean
}