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
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.bind.DatatypeConverter
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8


@RestController
class ImageController() {
//    val BASEPATH = "/tmp/"

    val BASEPATH = "/home/ec2-user/images/"

    data class FileUploadResponse(
            val path: String
    )

    // 画像のアップロード
    // Imageを取得し、urlを返す
    @PostMapping(value = ["/image/upload"])
    fun upfile(@RequestParam("file") file: MultipartFile): FileUploadResponse  {


        // 空のファイルが投げられたとき
        if (file.isEmpty){
            throw BadRequestException("Empty File")
        }
        // タイムスタンプを作る
        val df = SimpleDateFormat("yyyyMMddHHmmss") // 時間のフォーマットを指定
        val  date = Date(System.currentTimeMillis()) // 現在時刻を取得

        // スラッシュとかをなくすためにハッシュ化
        var fileName = hashString(BASEPATH + df.format(date) + "_" + file.originalFilename)
        // タイムスタンプと拡張子を付与
        fileName = df.format(date) + "_" + fileName + "." + File(file.originalFilename).extension

        // 保存先のパスを定義
        val savePath = Paths.get(BASEPATH + fileName)

        // ファイルの保存
        try {
            Files.newOutputStream(savePath, StandardOpenOption.CREATE).use { os ->
                os.write(file.bytes)
            }
        } catch (ex: IOException) {
            throw BadRequestException("Fail to Save")
        }

        return FileUploadResponse(fileName)
    }

    // 画像のダウンロード
    @GetMapping(value = ["/image/download/{id}"])
    fun getFile(@PathVariable("id") id: String): HttpEntity<ByteArray> {
        val path: Path = Paths.get(BASEPATH, id)
        lateinit var byteArray: ByteArray
        // ファイルを取得し、byteArrayに保存
        try {
            byteArray = Files.readAllBytes(path)
        } catch (e: IOException) {
            e.printStackTrace()
            throw BadRequestException("Not Found")
        }

        // レスポンスデータとして返却
        val headers = HttpHeaders()
        // 拡張子によって、contentTypeを変更する
        if(File(id).extension == "mp3") {
            headers.contentType = MediaType.parseMediaType("audio/mp3")
        } else {
            headers.contentType = MediaType.IMAGE_JPEG
        }
        headers.contentLength = byteArray.size.toLong()
        return HttpEntity<ByteArray>(byteArray, headers)
    }
}

// ハッシュ化の関数
private fun hashString(input: String): String {
    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
            .getInstance("MD5")
            .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString().substring(0,7)
}