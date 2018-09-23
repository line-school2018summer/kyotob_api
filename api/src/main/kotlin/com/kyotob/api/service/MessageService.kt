package com.kyotob.api.service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kyotob.api.WebSocketServer
import com.kyotob.api.mapper.*
import com.kyotob.api.model.*
import org.springframework.stereotype.Service

@Service
class MessageService(private val mdao: MessageDAO, private val tdao: TokenDao, private val roomMapper: RoomMapper, private val udao: UserDao, private val pairMapper: PairMapper) {
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

    // メッセージ取得
    fun getMessageList(roomId: Int): List<GetMessageResponse>? =
            mdao.findMessages(roomId)

    // メッセージ送信時に呼ぶメソッド
    fun sendMessage(request: SendMessageRequest, roomId: Int, userIdByToken: Int): Boolean {
        // Messageを追加する
        mdao.insertMessage(userIdByToken, roomId, request.content, request.contentType)

        // RoomテーブルのRecentMessageを更新する
        roomMapper.updateRecentMessage(request.content, roomId)

        //WebSocketを使って、メッセージの新着を知らせる
        sendNotification(udao.getNameById(pairMapper.findByRoomId(roomId)!!.userId1), roomId) // 送信者に通知を送る
        sendNotification(udao.getNameById(pairMapper.findByRoomId(roomId)!!.userId2), roomId) // 受信者に通知を送る
        return true
    }

    fun sendNotification(userName: String, roomId: Int) {
        for(session in WebSocketServer.sessions) {
            try {
                if(session.pathParameters["user_name"] == userName) {
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
