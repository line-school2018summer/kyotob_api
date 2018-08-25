package com.kyotob.api.service

import org.springframework.stereotype.Service
import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.model.Token
import com.kyotob.api.mapper.TokenDao
import java.util.*


@Service
class TokenService(private val tokenDao: TokenDao) {

    fun getAccessToken(userId: Int, password: String): String {
        //Todo: idとパスワードが正しいことを確認

        //
        tokenDao.delete(userId)
        //Todo: いい感じのトークンを返す
        //とりあえずuserIdを文字列にしたやつをハッシュ化しておく
        val token: String = UUID.randomUUID().toString()
        tokenDao.create(userId, token)
        return token
    }

    fun verifyAccessToken(accessToken: String): Int {
        val token: Token? = tokenDao.findByToken(accessToken)
        token ?: throw UnauthorizedException("invalid token")
        return token.userId
    }

    fun deleteAccessToken(userId: Int) {
        tokenDao.delete(userId)
    }

}