package com.kyotob.api.service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kyotob.api.WebSocketServer
import com.kyotob.api.WebSocketServer.Companion.sessions
import com.kyotob.api.mapper.MessageDAO // Message関連のMapper
import com.kyotob.api.mapper.TokenDao // Token関連のMapper
import com.kyotob.api.model.*
import org.springframework.stereotype.Service
@Service
class MessageService(private val mdao: MessageDAO, private val tdao: TokenDao) {
    // 認証時に呼ぶメソッド(UserIdを返す)
    fun auth(roomId: Int, token: String): Int {
        // Tokenから情報を取得する
        val userInfoByToken: Token? = tdao.findByToken(token)
        // userが存在しない場合はfalseを返す
        if(userInfoByToken == null) return -1

        // roomにuserIdが存在するか
        if(mdao.userExitInRoom(roomId, userInfoByToken.userId)) return userInfoByToken.userId
        // 存在しなければfalseを返す
        return -1
    }
    // メッセージ取得時に呼ぶメソッド
    fun getMessageList(roomId: Int): List<GetMessageResponse>? {
        // メッセージ取得
        return mdao.findMessages(roomId)
    }
    // メッセージ送信時に呼ぶメソッド
    fun sendMessage(request: SendMessageRequest, roomId: Int, userIdByToken: Int): Boolean {
        mdao.insertMessage(userIdByToken, roomId, request.content)

        //WebSocketを使って、メッセージの新着を知らせる
        sendNotification(request, roomId)
        return true
    }

    fun sendNotification(request: SendMessageRequest, roomId: Int) {
        for(session in WebSocketServer.sessions) {
            try {
                if(session.pathParameters["user_name"] == request.userName) {
                    val messages: List<GetMessageResponse>? = mdao.findMessages(roomId) // roomのメッセージをすべて取得する
                    // 新着メッセージを知らせる
                    session.asyncRemote.sendText(
                            jacksonObjectMapper().writeValueAsString(
                                    WebSocketMessage(
                                            messages!!.last().createdAt,
                                            messages.last().userScreenName,
                                            roomId,
                                            messages.last().content
                                    )
                            )
                    )
                }
            } catch (e: Exception) { // Sessionが切断されていたときの処理
                WebSocketServer.sessions.remove(session) // Sessionを削除
            }
        }
    }
}