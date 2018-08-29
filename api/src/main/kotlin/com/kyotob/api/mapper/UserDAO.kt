package com.kyotob.api.mapper

import org.apache.ibatis.annotations.*

@Mapper
interface UserDAO {

    //Userを新しく追加するSQL文
    @Insert(
            """
                INSERT INTO users
                (name, screen_name, password)
                VALUES (#{name},#{screen_name}, #{password});
            """
    )fun insertUser(name: String, screen_name: String, password: String)

    //user_nameからuser_passwordを割り出す
    @Select(
            """
                SELECT password
                FROM users
                WHERE name=#{user_name}
            """
    )fun getPassword(user_name: String)

    //Userが存在するかどうか判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{user_name})
            """
    )fun isUserRegistered(user_name: String): Boolean
}