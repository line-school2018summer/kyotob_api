package com.kyotob.api

import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.csv.CsvDataSet
import org.dbunit.operation.DatabaseOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import javax.sql.DataSource

@Component
class TestDataResources {
    @Autowired
    lateinit private var dataSource: DataSource


     fun beforeTestExecution() {
            var con: DatabaseConnection? = null
        try {
            // テストデータのインサート
            con = DatabaseConnection(dataSource.connection)
            DatabaseOperation.CLEAN_INSERT.execute(con!!, CsvDataSet(File("src/test/resources/testData")))
        }finally{
            con?.close()
        }
    }

}