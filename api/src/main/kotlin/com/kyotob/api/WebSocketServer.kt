package com.kyotob.api

import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint


@ServerEndpoint("/{user_name}")
class WebSocketServer {

    companion object {
        // sessionを記録しておくためのArray
        // MessagingAPIの挙動をトリガにして、sessionに対してMessageを送信できるよう、可視性をPublicにしている
        val sessions = CopyOnWriteArraySet<Session>()
    }


    // Socket通信を開始するリクエストを処理する
    @OnOpen
    fun onOpen(@PathParam("user_name") userId: String, session: Session) {
        println("SESSION STARTED BY: $userId") // System Console
        session.asyncRemote.sendText("WebSocket通信を開始します。") // クライアントに通信の開始を知らせる
        sessions.add(session) // Sessionを追加
    }

    // Messageを送信するリクエストを処理する(通信が増えるだけなので基本的に使う気はない)
    @OnMessage
    fun onMessage(@PathParam("user_name") userId: String, message: String, session: Session) {
        println("MESSAGE WAS SENT BY: $userId") // System Console
        session.asyncRemote.sendText("You can't send message")
    }


    // Socket通信を終了するリクエストを処理する
    @OnClose
    fun onClose(@PathParam("user_name") userId: String, session: Session) {
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