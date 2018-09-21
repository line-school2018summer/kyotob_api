package com.kyotob.api.service

import com.kyotob.api.controller.*
import com.kyotob.api.mapper.PairMapper
import com.kyotob.api.mapper.TokenDao
import com.kyotob.api.mapper.UserDao
import com.kyotob.api.model.Pair
import com.kyotob.api.model.UserLogin
import com.kyotob.api.model.UserRegister
import com.kyotob.api.model.UserResponse
import com.kyotob.api.model.UserSearch
import com.kyotob.api.model.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class UserService(private val userDao: UserDao, private val tokenDao: TokenDao, private val pairMapper: PairMapper){
    //User登録をするメソッド
    fun createUser(request: UserRegister): UserResponse{
        //Userが既に登録されていればErrorを返す。
        if(userDao.isNameRegistered(request.name)) {
            throw Conflict("User name already exists")
        }
        //Userが未登録ならば新しく登録
        //passwordのハッシュ化
        val hashedPassword = BCryptPasswordEncoder().encode(request.password)

        //usersに追加
        userDao.insertUser(request.name, request.screenName, hashedPassword, request.imageUrl)

        //tokenを取得しtokensに(id, token)の組を格納
        val token:String = createNewToken(userDao.getUser(request.name).id)

        return UserResponse(request.screenName, token, request.imageUrl)
    }

    fun getUserFromId(id: Int): User {
        return userDao.findUserById(id) ?: throw InternalServerError("getUserFromId")
    }

    //Userログインをするメソッド
    fun login(request: UserLogin): UserResponse{
        if (!userDao.isNameRegistered(request.name)) {
            //Userが登録されていない場合
            throw UnauthorizedException("User name or Password is wrong")
        }
        else{
            //Userが登録されている場合、passwordの正誤を確認
            val givenPassword: String = request.password
            val storedPassword: String = userDao.getUser(request.name).password

            //passwordのハッシュ値が一致するか確認
            if(!BCryptPasswordEncoder().matches(givenPassword, storedPassword)){
                //Passwordが間違っている場合
                throw UnauthorizedException("User Name or Password is wrong")
            }
            else{
                //passwordがが合っている場合は新規発行のtokenを返す
                val token:String = createNewToken(userDao.getUser(request.name).id)
                val user = userDao.getUser(request.name)
                return UserResponse(user.screenName, token, user.imageUrl)
            }
        }
    }

    //User検索をするメソッド
    fun searchUser(userName: String, token:String): UserSearch{
        //tokenが間違っていればエラーを投げる
        if(tokenDao.findByToken(token) == null) throw UnauthorizedException("Invalid access token")
        //tokenが合っていればuserを検索する
        else{
            //Userがいなければエラーを投げる
            if(!userDao.isNameRegistered(userName)) throw NotFound("User Not Found")
            //Userがいれば情報を取得する
            else{
                val userInfo: User = userDao.getUser(userName)
                return UserSearch(userInfo.name, userInfo.screenName, userInfo.imageUrl)
            }
        }
    }

    //tokenを新規発行or更新するメソッド
    fun createNewToken(userId: Int):String{
        val token: String = UUID.randomUUID().toString()  //新たにtokenを得る
        tokenDao.upsert(userId, token)  //tokensに格納
        return token
    }

    fun getUser(userName: String): User {
        return userDao.getUser(userName)
    }

    fun getFriend(userName: String): List<HashMap<String, String>> {
        fun getFriendId(myId: Int, pair: Pair): Int {
            if (pair.userId1 == myId) return pair.userId2
            return pair.userId1
        }

        val id: Int = userDao.getUser(userName).id
        val pairs: ArrayList<Pair> = pairMapper.findByUserId(id)
        val friendList: List<User> = pairs.map {  userDao.findUserById(getFriendId(id, it))!! }
        return friendList.map { hashMapOf("friend_screen_name" to it.screenName, "friend_name" to it.name) }
    }

    fun updateScreenName(accessToken: String, name: String, newScreenName: String) {
        //token確認
        searchUser(name, accessToken)
        val user = userDao.getUser(name)
        userDao.updateScreenName(user.id, newScreenName)
    }
}
