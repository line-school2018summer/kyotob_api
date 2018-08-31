package com.kyotob.api.mapper

import org.apache.ibatis.annotations.*

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

    //nameでuserが存在を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{name})
            """
    )fun isNameRegistered(name: String): Boolean

    //idでuserの存在を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE id=#{id)
            """
    )fun isIdRegistered(id: Int): Boolean

    //nameとpasswordの組の正誤を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE name=#{name} AND password=#{password})
            """
    )fun isNamePasswordConsistent(name: String, password: String): Boolean

    //idとpasswordの組の正誤を判定
    @Select(
            """
                SELECT EXISTS(SELECT 1 FROM users WHERE id=#{id} AND password=#{password})
            """
    )fun isIdPasswordConsistent(id: Int, password: String): Boolean

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
}