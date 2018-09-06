package com.kyotob.api.service

import com.kyotob.api.controller.UnauthorizedException
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


@SpringBootTest
@ExtendWith(SpringExtension::class)
class RoomServiceTest {

    @Autowired
    lateinit var dataSource: DataSource


    @Autowired
    lateinit var roomService: RoomService

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
        val roomList = roomService.getAllRoomList()
        val roomIdList: List<Int> = roomList.map {it -> it.id}
        val assertedRoomIdList = (1..10).toList()
        assertEquals(assertedRoomIdList,roomIdList)
    }

    @Test
    fun test2() {
        val roomList = roomService.getRoomListFromUserId(1)
        val roomIdList: List<Int> = roomList.map{it -> it.id}
        val assertedRoomIdList = listOf<Int>(1,6)
        assertEquals(assertedRoomIdList,roomIdList)
    }

    @Test
    fun test3() {
        //userId1,2の順番が正しいことを確認
        val roomId = roomService.createPairRoom(4,2,"roomname")
        assertEquals(2,roomService.getPairFromRoomId(roomId)!!.userId1)
    }
}