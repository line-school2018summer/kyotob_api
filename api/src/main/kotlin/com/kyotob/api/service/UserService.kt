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
class UserService(private val userDao: UserDao,
                  private val tokenDao: TokenDao,
                  private val pairMapper: PairMapper) {

    fun createUser(request: UserRegister): UserResponse {
        // User が既に登録されていれば例外を投げる
        if (userDao.isNameRegistered(request.name)) {
            throw Conflict("User name already exists")
        }
        // User が未登録ならば新しく登録
        // password のハッシュ化
        val hashedPassword = BCryptPasswordEncoder().encode(request.password)

        // users に追加
        userDao.insertUser(request.name, request.screenName, hashedPassword, request.imageUrl)

        // token を取得し tokens に (id, token) の組を格納
        val token:String = createNewToken(userDao.getUser(request.name).id)

        return UserResponse(request.screenName, token)
    }

    fun getUserFromId(id: Int): User {
        return userDao.findUserById(id) ?: throw InternalServerError("getUserFromId")
    }

    fun login(request: UserLogin): UserResponse {
        if (!userDao.isNameRegistered(request.name)) {
            // User が登録されていない場合
            throw UnauthorizedException("User name or Password is wrong")
        } else {
            // User が登録されている場合、password の正誤を確認
            val givenPassword: String = request.password
            val storedPassword: String = userDao.getUser(request.name).password

            // password のハッシュ値が一致するか確認
            if (!BCryptPasswordEncoder().matches(givenPassword, storedPassword)) {
                // Password が間違っている場合
                throw UnauthorizedException("User Name or Password is wrong")
            } else {
                // password が合っている場合は新規に発行した token を返す
                val token:String = createNewToken(userDao.getUser(request.name).id)
                val user = userDao.getUser(request.name)
                return UserResponse(user.screenName, token)
            }
        }
    }

    fun searchUser(userName: String, token:String): UserSearch {
        if (tokenDao.findByToken(token) == null) {
            // token が間違っていればエラー
            throw UnauthorizedException("Invalid access token")
        } else if (!userDao.isNameRegistered(userName)) {
            // User がいなければエラー
            throw NotFound("User Not Found")
        } else {  // User がいれば情報を取得
            val userInfo: User = userDao.getUser(userName)
            return UserSearch(userInfo.name, userInfo.screenName, userInfo.imageUrl)
        }
    }

    // token を新規発行 or 更新する
    fun createNewToken(userId: Int): String {
        val token: String = UUID.randomUUID().toString()  // 新たに token を得る
        tokenDao.upsert(userId, token)  // tokens に格納
        return token
    }

    fun getUser(userName: String) = userDao.getUser(userName)

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
        // token 確認
        searchUser(name, accessToken)
        val user = userDao.getUser(name)
        userDao.updateScreenName(user.id, newScreenName)
    }
}
