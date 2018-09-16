package com.kyotob.api.service
import com.kyotob.api.mapper.MessageDAO // Message関連のMapper
import com.kyotob.api.mapper.TokenDao // Token関連のMapper
import com.kyotob.api.model.*
import org.springframework.stereotype.Service
@Service
class MessageService(private val mdao: MessageDAO, private val tdao: TokenDao) {
    // 認証時に呼ぶメソッド
    fun auth(roomId: Int, token: String): Int {
        // Tokenから情報を取得する
        val userInfoByToken: Token? = tdao.findByToken(token)
        userInfoByToken?.let {
            // roomにuserIdが存在するか
            if(mdao.userExitInRoom(roomId, userInfoByToken.userId)) return userInfoByToken.userId
            // 存在しなければfalseを返す
            return -1
        } ?: run {
            // userInfoByTokenがnullの場合はfalseを返す
            return -1
        }

    }
    // メッセージ取得時に呼ぶメソッド
    fun getMessageList(roomId: Int): List<GetMessageResponse>? {
        // メッセージ取得
        return mdao.findMessages(roomId)
    }
    // メッセージ送信時に呼ぶメソッド
    fun sendMessage(request: SendMessageRequest, roomId: Int, userIdByToken: Int): Boolean {
        mdao.insertMessage(userIdByToken, roomId, request.content)
        return true
    }

}