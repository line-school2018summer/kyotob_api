package com.kyotob.api.service

import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.model.Token
import com.kyotob.api.mapper.TokenMapper


@Service
class TokenService(private val tokenMapper: TokenMapper) {

    fun getAccessToken(userId: Int, password: String): String {
        //Todo: idとパスワードが正しいことを確認

        //
        tokenMapper.delete(userId)
        //Todo: いい感じのトークンを返す
        //とりあえずuserIdを文字列にしたやつをハッシュ化しておく
        val encoder = BCryptPasswordEncoder()
        val digest:String = encoder.encode(Integer.toString(userId))
        tokenMapper.create(userId, digest)
        return digest
    }

    fun verifyAccessToken(accessToken: String): Int {

        val token: Token? = tokenMapper.findByToken(accessToken)
        token ?: throw UnauthorizedException("invalid token")
        return token.userId
    }

    fun deleteAccessToken(userId: Int) {
        tokenMapper.delete(userId)
    }

}