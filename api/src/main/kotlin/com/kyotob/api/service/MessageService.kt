package com.kyotob.api.service
import com.kyotob.api.mapper.MessageDAO // Message関連のMapper
import com.kyotob.api.mapper.TokenDao // Token関連のMapper
import com.kyotob.api.model.GetMessageResponse // Message受信時のレスポンス用モデル
import com.kyotob.api.model.SendMessageRequest // Message送信時のリクエスト用モデル
import com.kyotob.api.model.UserId // users.user_nameからusers.user_idを割り出す用
import com.kyotob.api.model.Token // TokenDao使用時のモデル
import org.springframework.stereotype.Service
@Service
class MessageService(private val mdao: MessageDAO, private val tdao: TokenDao) {
    // 認証時に呼ぶメソッド
    fun auth(roomId: Int, token: String): Boolean {
        // Tokenから情報を取得する
        val userInfoByToken: Token? = tdao.findByToken(token)
        // userが存在しない場合はfalseを返す
        if(userInfoByToken == null) return false

        // roomにuserIdが存在するか
        if(mdao.userExitInRoom(roomId, userInfoByToken.userId)) return true
        // 存在しなければfalseを返す
        return false
    }
    // メッセージ取得時に呼ぶメソッド
    fun getMessageList(roomId: Int): List<GetMessageResponse>? {
        // メッセージ取得
        return mdao.findMessages(roomId)
    }
    // メッセージ送信時に呼ぶメソッド
    fun sendMessage(request: SendMessageRequest, roomId: Int): Boolean {
        // users.user_nameからusers.user_idを割り出す
        val userId: UserId? = mdao.getUserId(request.userName)
        if (userId == null) {
            // 該当しない場合はfalseを返す
            return false
        } else {
            // users.user_idを使って、messageをINSERTする
            mdao.insertMessage(userId.userId, roomId, request.content)
        }
        return true
    }
}