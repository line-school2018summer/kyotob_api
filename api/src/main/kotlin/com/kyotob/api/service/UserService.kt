package com.kyotob.api.service

import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.model.UserRegister //User登録用のモデル
import com.kyotob.api.model.UserLogin //User認証用のモデル

import com.kyotob.api.mapper.UserDAO //User関連のMapper

import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager

@Service
class UserService(private val userDao: UserDAO){
    //User登録をするメソッド
    fun createUser(request: UserRegister): Boolean{
        //Userが既に登録されていればErrorを返す。
        if(userDao.isUserRegistered(request.name)) {
            return false
        }
        //Userが未登録ならばDatabaseに追加
        else{
            userDao.insertUser(request.name, request.screenName, request.password)
            return true
        }
    }

    //Userログインをするメソッド
    fun login(request: UserLogin): Boolean {
        if (userDao.isUserRegistered(request.name)) {
            val givenPassword: String = request.password
            val storedPassword: String = userDao.getPassword(request.name)

            return givenPassword == storedPassword
        }
        else{
            return false
        }
    }
}