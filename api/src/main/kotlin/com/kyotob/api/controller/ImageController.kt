package com.kyotob.api.controller

import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType


@RestController
class ImageController() {
    val BASEPATH = "/tmp/"


    // 画像のアップロード
    // Imageを取得し、urlを返す
    @PostMapping(value = ["/image/upload"])
    fun upfile(@RequestParam("file") file: MultipartFile): String {
        // 空のファイルが投げられたとき
        if (file.isEmpty){
            throw BadRequestException("Empty File")
        }
        // ファイル名の定義
        val savePath = Paths.get(BASEPATH + LocalDateTime.now().toString() + "_" + file.originalFilename)

        // ファイルの保存
        try {
            Files.newOutputStream(savePath, StandardOpenOption.CREATE).use { os ->
                os.write(file.bytes)
            }
        } catch (ex: IOException) {
            throw BadRequestException("Fail to Save")
        }

        return "Success"
    }

    // 画像のダウンロード
    @GetMapping(value = ["/image/download/{id}"])
    fun getFile(@PathVariable("id") id: String): HttpEntity<ByteArray> {
        val path: Path = Paths.get(BASEPATH, id)
        lateinit var byteArray: ByteArray
        try {
            byteArray = Files.readAllBytes(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // レスポンスデータとして返却
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        headers.contentLength = byteArray.size.toLong()
        return HttpEntity<ByteArray>(byteArray, headers)
    }
}

