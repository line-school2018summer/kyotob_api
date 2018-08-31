package com.kyotob.api.service

import com.kyotob.api.controller.Conflict
import com.kyotob.api.controller.UnauthorizedException
import com.kyotob.api.mapper.UserDao
import com.kyotob.api.model.UserLogin
import com.kyotob.api.model.UserRegister
import com.kyotob.api.model.UserResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userDao: UserDao, private val tokenService: TokenService){
    //User登録をするメソッド
    fun createUser(request: UserRegister): UserResponse{
        //Userが既に登録されていればErrorを返す。
        if(userDao.isNameRegistered(request.name)) {
            throw Conflict("User name already exists")
            //return false
        }
        //Userが未登録ならば新しく登録
        else{
            //passwordのハッシュ化
            val hashedPassword = BCryptPasswordEncoder().encode(request.password)

            //usersに追加
            userDao.insertUser(request.name, request.screenName, hashedPassword)

            //tokenを取得
            //val token: String = tokenService.getAccessToken(userDao.getId(request.name), request.password)

            val token: String = "token"

            return UserResponse(token)
        }
    }

    //Userログインをするメソッド
    fun login(request: UserLogin): Boolean {
        if (!userDao.isNameRegistered(request.name)) {
            //Userが登録されていない場合
            throw UnauthorizedException("User name does not exist")
        }
        else{
            //Userが登録されている場合、passwordの正誤を確認
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