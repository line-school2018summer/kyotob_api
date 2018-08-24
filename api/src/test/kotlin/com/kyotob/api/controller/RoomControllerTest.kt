package com.kyotob.api.controller

import com.kyotob.api.ApiApplication
import com.kyotob.api.service.RoomService
import com.kyotob.api.model.Room
import com.kyotob.api.controller.UnauthorizedException
import net.bytebuddy.description.method.MethodDescription
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.dbunit.DataSourceDatabaseTester
import org.dbunit.operation.DatabaseOperation
import org.dbunit.util.fileloader.CsvDataFileLoader
import org.springframework.util.ResourceUtils
import javax.sql.DataSource
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(classes = [ApiApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
class RoomControllerTest {

    @Autowired
    lateinit var dataSource: DataSource


    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

/*    lateinit var roomService: RoomService*/

    @BeforeEach
    fun setup() {
        //DBに値を挿入
        val databaseTester = DataSourceDatabaseTester(dataSource)
        databaseTester.setUpOperation = DatabaseOperation.CLEAN_INSERT

        val loader = CsvDataFileLoader()
        databaseTester.dataSet = loader.loadDataSet(ResourceUtils.getURL("classpath:testData/"))

        databaseTester.onSetup()
    }

    @Test
    fun test1() {
        //test /room?{token}
        val result = testRestTemplate.getForEntity("/room?access_token=1",List::class.java)

        val body = result.body
        assertEquals(2, body?.size)
        assertEquals(hashMapOf("id" to 1, "name" to "room1"),result.body?.get(0))
        assertEquals(hashMapOf("id" to 6, "name" to "room6"),result.body?.get(1))
    }

    @Test
    fun test2() {
        //test /room?{token}
        //userとの結合がまだなのでuid=2の人としかroom作れない
        val request = PostPairRequest(friendUserName = "2")
        //すでにroomがある時
        val result = testRestTemplate.postForEntity("/room/pair?access_token=1",request,Room::class.java)
        val body = result.body
        assertEquals(1,body?.id)
        println(body.toString())
        //roomがないときはroom作る
        val result2 = testRestTemplate.postForEntity("/room/pair?access_token=3",request,Room::class.java)
        val body2 = result2.body
        assertTrue( body2!!.id > 10 )
        println(body2.toString())
    }
}
