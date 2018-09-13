package com.kyotob.api

import com.google.gson.Gson
import com.kyotob.api.model.WebSocketMSG
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

// Test用 "http://localhost:8080/"にアクセスすると
// user_id:abcに対して、指定のroomをリロードしろという意図のjsonデータを送る
@RestController
class Test {
    @RequestMapping(value = ["/"])
    fun index(){
        // (session.pathParameters["user_id"] == "abc")の"abc"をuserIdに置き換えることで、
        // 特定のユーザーに対して、サーバーから
        // 「特定のroomに新着メッセージがある」という意図のJsonメッセージを送る
        for(session in WebSocketServer.sessions) {
            try{
                if(session.pathParameters["user_id"] == "abc")
                // Jsonを送る方法は要検討。
                // RoomIdは動的に変える
                    session.asyncRemote.sendObject(Gson().toJson(WebSocketMSG(1)))
            } catch (e: Exception) {} // ClientがCloseせずにConnectionを切断したときの例外
        }
    }
}


@ServerEndpoint("/chat/{user_id}")
class WebSocketServer {

    companion object {
        // sessionを記録しておくためのArray
        // MessagingAPIの挙動をトリガにして、sessionに対してMessageを送信できるよう、可視性をPublicにしている
        val sessions = CopyOnWriteArraySet<Session>()
    }


    // Socket通信を開始するリクエストを処理する
    @OnOpen
    fun onOpen(@PathParam("user_id") userId: String, session: Session) {
        println("SESSION STARTED BY: $userId") // System Console
        session.asyncRemote.sendText("WebSocket通信を開始します。") // クライアントに通信の開始を知らせる
        sessions.add(session) // Sessionを追加
    }

    // Messageを送信するリクエストを処理する(通信が増えるだけなので基本的に使う気はない)
    @OnMessage
    fun onMessage(@PathParam("user_id") userId: String, message: String, session: Session) {
        println("MESSAGE WAS SENT BY: $userId") // System Console
        session.asyncRemote.sendText("You can't send message")
    }


    // Socket通信を終了するリクエストを処理する
    @OnClose
    fun onClose(@PathParam("user_id") userId: String, session: Session) {
        println("SESSION CLOSED BY: $userId") // System Console
        session.asyncRemote.sendText("WebSocket通信を終了します。") // クライアントに通信の終了を知らせる
        sessions.remove(session) // Sessionを削除
    }

    // ERRORのログを取る
    @OnError
    fun onError(session: Session, t: Throwable) {
        println("ERROR OCCURED AT: $session") // System Console
    }
}