package com.kyotob.api.service

import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.mapper.TokenDao
import com.kyotob.api.model.Token
import org.springframework.stereotype.Service
import java.util.*


@Service
class TokenService(private val tokenDao: TokenDao, private val userService: UserService) {
    fun createAccessToken(userId: Int): String{
        val token: String = UUID.randomUUID().toString()
        tokenDao.create(userId, token)
        return token
    }

    fun getAccessToken(userId: Int, password: String): String {
        //idとパスワードが正しいことを確認
        if(!userService.isConsistent(userId, password)){
            //idとパスワードが一致しなければエラーを投げる
            throw UnauthorizedException("id and password not match")
        }

        else {
            //一致すれば新しいtokenを発行
            tokenDao.delete(userId)
            //とりあえずuserIdを文字列にしたやつをハッシュ化しておく
            val token: String = UUID.randomUUID().toString()
            tokenDao.create(userId, token)
            return token
        }
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