package com.kyotob.api.service

import com.kyotob.api.TestDataResources
import com.kyotob.api.controller.UnauthorizedException
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@ExtendWith(SpringExtension::class)
class TestTokenService {
    @Autowired
    lateinit var testDataResources: TestDataResources

    @Autowired
    lateinit var tokenService: TokenService

    @Before
    fun setup(){
        testDataResources.beforeTestExecution()
    }

    @Test
    fun testTokenService() {
        //digest生成
        val digest1 = tokenService.getAccessToken(1, "pass")
        //digestからuserIdを確認できる
        Assertions.assertEquals(1, tokenService.verifyAccessToken(digest1))
    }

    @Test
    fun testDeleteToken() {
        val digest1 = tokenService.getAccessToken(1, "pass")
        //デリートした後で同じようにverifyするとunauthorized投げる
        tokenService.deleteAccessToken(1)
        val exception = Assertions.assertThrows(UnauthorizedException::class.java, {
             tokenService.verifyAccessToken(digest1)
        })
    }

    @Test
    fun testInvalidToken() {
        val digest1 = tokenService.getAccessToken(1, "pass")
        tokenService.deleteAccessToken(1)
        //token再発行したのちふるいやつでverifyするとunauthorized投げる
        val digest2 = tokenService.getAccessToken(1, "pass")
        val exception = Assertions.assertThrows(UnauthorizedException::class.java, {
            tokenService.verifyAccessToken(digest1)
        })
    }
}