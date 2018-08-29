package com.kyotob.api.controller

import com.kyotob.api.model.UserRegister //User登録用のモデル
import com.kyotob.api.model.UserLogin //User認証用のモデル

import com.kyotob.api.service.UserService // User関連のサービス

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


@RestController
class UserController(private val userService: UserService){
    //Userの登録
    @PostMapping(
            value = ["/user"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun createuser(@RequestBody request: UserRegister):Boolean {
        return userService.createUser(request)
    }

    //Userのログイン
    @PostMapping(
            value = ["user/login"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun loginuser(@RequestBody request: UserLogin): Boolean{
        return userService.login(request)
    }

}