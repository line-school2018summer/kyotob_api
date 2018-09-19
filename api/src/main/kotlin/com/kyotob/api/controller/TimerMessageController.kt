package com.kyotob.api.controller
import com.kyotob.api.model.*
import com.kyotob.api.service.MessageService // Message関連のサービス
import com.kyotob.api.service.TimerMessageService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
@RestController
class TimerMessageController(private val messageService: MessageService, private val timerMessageService: TimerMessageService) {
    // Messageの取得
    @GetMapping(
            value = ["/room/{room_id}/messages/timer"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getMessage(@PathVariable("room_id") roomId: Int, @RequestHeader("access_token") token: String): List<GetTimerMessageResponse>? {
        val userId: Int = messageService.auth(roomId, token)
        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (userId == -1) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // Tokenの認証ができたので、メッセージを取得してResponseとして返す
        return timerMessageService.getMessageList(roomId, userId)
    }
    // Messageの送信
    @PostMapping(
            value = ["/room/{room_id}/messages/timer"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun sendMessage(@PathVariable("room_id") roomId: Int, @RequestBody request: SendTimerMessageRequest, @RequestHeader("access_token") token: String):Boolean {
        // バリデーション
        // 空文字を入力した場合
        if(request.content.isEmpty()) throw BadRequestException("no content")
        // 100文字以上の場合
        if(request.content.length > 100) throw BadRequestException("over 100 content")
        // 画像がない場合
        if(request.imageUrl.isEmpty()) throw BadRequestException("no content")

        val userIdByToken = messageService.auth(roomId, token)
        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (userIdByToken == -1) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // メッセージ送信
        return timerMessageService.sendMessage(request, roomId, userIdByToken)
    }
}