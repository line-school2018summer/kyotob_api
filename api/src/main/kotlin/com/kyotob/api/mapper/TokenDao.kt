package com.kyotob.api.mapper

import com.kyotob.api.model.Token
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component

@Component
@Mapper
interface TokenDao {
    @Select(
            """
                SELECT user_id, token, created
                FROM tokens
            """
    )
    fun getTokenList(): ArrayList<Token>

    @Select(
            """
                SELECT user_id, token, created
                From tokens
                WHERE token=#{token}
            """
    )
    fun findByToken(token: String): Token?

    @Insert(
            """
                INSERT INTO tokens (user_id, token)
                VALUES (#{userId}, #{token})
            """
    )
    fun create(userId: Int, token: String): Boolean

    @Delete(
            """
                DELETE FROM tokens
                WHERE user_id=#{userId}
            """
    )
    fun delete(userId: Int): Boolean
}
