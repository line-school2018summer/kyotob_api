package com.kyotob.api.service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kyotob.api.WebSocketServer
import com.kyotob.api.mapper.RoomMapper
import com.kyotob.api.mapper.TimerMessageDAO
import com.kyotob.api.model.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
@Service

class TimerMessageService(private val tmdao: TimerMessageDAO, private val roomMapper: RoomMapper) {
    // メッセージ取得時に呼ぶメソッド
    fun getMessageList(roomId: Int, recieverId: Int): List<GetTimerMessageResponse>? {

        // メッセージを取得
        val messages = tmdao.findMessages(roomId, recieverId)
        // 既読フラグを立てる
        tmdao.updateKidoku(roomId, recieverId)

        return messages
    }

    // メッセージ送信時に呼ぶメソッド
    fun sendMessage(request: SendTimerMessageRequest, roomId: Int, userIdByToken: Int): Boolean {
        // Messageを追加する
        tmdao.insertMessage(userIdByToken, roomId, request.content, request.imageUrl, request.timer)
        return true
    }

    @Scheduled(cron = "*/5 * * * *", zone = "Asia/Tokyo") // ５分おきに実行
    fun sendNotification(request: SendMessageRequest, roomId: Int) {
        for(session in WebSocketServer.sessions) {
            try {
                // userNameからuserIdを取得する
                val recieverId = tmdao.geIdByName(session.pathParameters["user_name"]!!)!!
                // 新着メッセージを取得
                val messages: List<GetTimerMessageResponse>? = tmdao.findMessages(roomId, recieverId)
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
            } catch (e: Exception) { // Sessionが切断されていたときの処理
                WebSocketServer.sessions.remove(session) // Sessionを削除
            }
        }
    }
}