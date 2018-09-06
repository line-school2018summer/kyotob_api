package com.kyotob.api.controller
import com.kyotob.api.model.GetMessageResponse // メッセージ受信用のモデル
import com.kyotob.api.model.SendMessageRequest // メッセージ送信用のモデル
import com.kyotob.api.service.MessageService // Message関連のサービス
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
@RestController
class MessageController(private val messageService: MessageService) {
    // Messageの取得
    @GetMapping(
            value = ["/room/{room_id}/messages"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getMessage(@PathVariable("room_id") roomId: Int, @RequestHeader("token") token: String): List<GetMessageResponse>? {
        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (messageService.auth(roomId, token) == false) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // Tokenの認証ができたので、メッセージを取得してResponseとして返す
        return messageService.getMessageList(roomId)
    }
    // Messageの送信
    @PostMapping(
            value = ["/room/{room_id}/messages"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun sendMessage(@PathVariable("room_id") roomId: Int, @RequestBody request: SendMessageRequest, @RequestHeader("token") token: String):Boolean {
        // バリデーション
        // 空文字を入力した場合
        if(request.content.length == 0) throw BadRequestException("no content")
        // 100文字以上の場合
        if(request.content.length > 100) throw BadRequestException("over 100 content")

        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (messageService.auth(roomId, token) == false) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // メッセージ送信
        return messageService.sendMessage(request, roomId)
    }
}