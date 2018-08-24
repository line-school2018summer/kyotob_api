package com.kyotob.api.service

import com.kyotob.api.mapper.MessageDAO // Message関連のMapper
import com.kyotob.api.model.GetMessageResponse // Message受信時のレスポンス用モデル
import com.kyotob.api.model.SendMessage // Message送信時のリクエスト用モデル
import org.springframework.stereotype.Service

@Service
class MessageService(private val mdao: MessageDAO) {
    // 認証時に呼ぶメソッド
    fun auth(room_id: Int, token: String): Boolean {
        // roomにtokenの持ち主がいないときはfalseを返す
        if (mdao.autorization(room_id, token) == null) return false
        // 存在すればTrueを返す
        return true
    }

    // メッセージ取得時に呼ぶメソッド
    fun getmessagelist(room_id: Int): List<GetMessageResponse> {
        // メッセージ取得
        return mdao.findMessages(room_id)
    }

    // メッセージ送信時に呼ぶメソッド
    fun sendmessage(request: SendMessage): Boolean {
        // users.user_nameからusers.user_idを割り出す
        val user_id = mdao.getuserid(request.user_name)
        if (user_id == null) {
            // 該当しない場合はfalseを返す
            return false
        } else {
            // users.user_idを使って、messageをINSERTする
            mdao.insertmessage(user_id.user_id, request.room_id, request.content)
        }

        return true
    }
}