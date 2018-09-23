package com.kyotob.api.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.kyotob.api.model.*

import com.kyotob.api.service.UserService  // User 関連サービス
import org.springframework.http.HttpStatus

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class UpdateUserRequest (
        @JsonProperty("new_screen_name")
        val newName: String,
        @JsonProperty("new_icon_path")
        val newIconPath: String
)

@RestController
class UserController(private val userService: UserService){
    // User の登録
    @PostMapping(
            value = ["/user"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun createUser(@RequestBody request: UserRegister): UserResponse =
            userService.createUser(request)

    // User のログイン
    @PostMapping(
            value = ["user/login"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun loginUser(@RequestBody request: UserLogin): UserResponse =
            userService.login(request)

    // User 検索
    @GetMapping(
            value = ["user/search/{user_name}"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun searchUser(@PathVariable("user_name") userName: String,
                   @RequestHeader("access_token") token:String): UserSearch =
            userService.searchUser(userName, token)

    @PutMapping( value = ["user/{user_name}"], produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)] )
    fun putUser(@PathVariable("user_name") userName: String,
                @RequestHeader("access_token") token: String,
                @RequestBody request: UpdateUserRequest): ResponseEntity<String> {
        userService.updateUser(token, userName, request.newName, request.newIconPath)
        val status = HttpStatus.NO_CONTENT
        return ResponseEntity(status)
    }

    @GetMapping(
            value = ["user/{user_name}/friends"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getFriends(@PathVariable("user_name") userName: String,
                   @RequestHeader("access_token") token: String): List<HashMap<String, String>> =
            userService.getFriend(userName)
}
