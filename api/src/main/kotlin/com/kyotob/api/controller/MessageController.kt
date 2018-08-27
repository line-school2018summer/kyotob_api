package com.kyotob.api.controller
import com.kyotob.api.model.GetMessageResponse // メッセージ受信用のモデル
import com.kyotob.api.model.SendMessage // メッセージ送信用のモデル
import com.kyotob.api.service.MessageService // Message関連のサービス
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
@RestController
class MessageController(private val messageservice: MessageService) {
    // Messageの取得
    @GetMapping(
            value = ["/message/{room_id}"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun getMessage(@PathVariable("room_id") room_id: Int, @RequestHeader("Token") token: String): List<GetMessageResponse>? {
        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (messageservice.auth(room_id, token) == false) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // Tokenの認証ができたので、メッセージを取得してResponseとして返す
        return messageservice.getmessagelist(room_id)
    }
    // Messageの送信
    @PostMapping(
            value = ["/message"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)]
    )
    fun sendMessage(@RequestBody request: SendMessage, @RequestHeader("Token") token: String):Boolean {
        // Tokenの持ち主がPair(ルーム)に存在するか確認する
        if (messageservice.auth(request.room_id, token) == false) {
            // ルーム存在しない場合はErrorを投げる
            throw UnauthorizedException("TokenError")
        }
        // メッセージ送信
        return messageservice.sendmessage(request)
    }
}