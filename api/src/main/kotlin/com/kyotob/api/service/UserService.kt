package com.kyotob.api.service

import com.kyotob.api.controller.BadRequestException
import com.kyotob.api.controller.Conflict
import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.controller.InternalServerError
import com.kyotob.api.model.UserRegister //User登録用のモデル
import com.kyotob.api.model.UserLogin //User認証用のモデル

import com.kyotob.api.mapper.UserDAO //User関連のMapper

import org.springframework.stereotype.Service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder //passwordのハッシュ化

import java.sql.Connection
import java.sql.DriverManager

@Service
class UserService(private val userDao: UserDAO){
    //User登録をするメソッド
    fun createUser(request: UserRegister): Boolean{
        //Userが既に登録されていればErrorを返す。
        if(userDao.isUserRegistered(request.name)) {
            throw Conflict("User name already exists")
            //return false
        }
        //Userが未登録ならばDatabaseに追加
        else{
            //passwordのハッシュ化
            val hashedPassword = BCryptPasswordEncoder().encode(request.password)

            //Databaseに追加
            userDao.insertUser(request.name, request.screenName, hashedPassword)
            return true
        }
    }

    //Userログインをするメソッド
    fun login(request: UserLogin): Boolean {
        if (!userDao.isUserRegistered(request.name)) {
            //Userが登録されていない場合
            throw UnauthorizedException("User name does not exist")
        }
        else{
            val givenPassword: String = request.password
            val storedPassword: String = userDao.getPassword(request.name)

            //passwordのハッシュ値が一致するか確認
            if(!BCryptPasswordEncoder().matches(givenPassword, storedPassword)){
                //Passwordが間違っている場合
                throw UnauthorizedException("Password is wrong")
            }
            else{
                return true
            }
        }
    }
}