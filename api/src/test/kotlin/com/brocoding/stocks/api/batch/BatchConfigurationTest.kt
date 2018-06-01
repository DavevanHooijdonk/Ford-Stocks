package com.brocoding.stocks.api.batch

import com.brocoding.stocks.api.FordStocksApi
import com.brocoding.stocks.api.stockdata.domain.DataType.ADJ_CLOSE
import com.brocoding.stocks.api.stockdata.domain.DataType.CLOSE
import com.brocoding.stocks.api.stockdata.domain.DataType.HIGH
import com.brocoding.stocks.api.stockdata.domain.DataType.LOW
import com.brocoding.stocks.api.stockdata.domain.DataType.OPEN
import com.brocoding.stocks.api.stockdata.domain.DataType.VOLUME
import com.brocoding.stocks.api.stockdata.domain.StockData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.job.SimpleJob
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal.valueOf
import java.time.LocalDate
import kotlin.test.assertTrue

/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RunWith(SpringRunner::class)
//@ContextConfiguration()
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [FordStocksApi::class])
class StockPriceControllerTest {

    @Mock
    private lateinit var jobBuilderFactory: JobBuilderFactory

    @Mock
    private lateinit var stepBuilderFactory: StepBuilderFactory

    @Mock
    private lateinit var jobLauncher: JobLauncher

    @Mock
    private lateinit var jobRegistry: JobRegistry

    @Mock
    private lateinit var mongoTemplateMock: MongoTemplate

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var jobRepository: JobRepository

    @Test
    fun `batch should properly run a job on startup and initialize mongo`() {
        // When the application starts

        // Then
        assertEquals("COMPLETED", jobRepository.getLastJobExecution("readCSVFileJob", JobParameters()).exitStatus.exitCode)
        val findAll = mongoTemplate.findAll<StockData>("StockData")
        assertTrue {
            findAll.isNotEmpty() }
    }

    @Test
    fun `processor should correctly convert from StockDataCSV to StockData`() {
        // Given
        val batchConfiguration = BatchConfiguration(emptyArray(), jobBuilderFactory, stepBuilderFactory, jobLauncher, jobRegistry, mongoTemplateMock)
        val name = "Banana"
        val open = valueOf(2.149165)
        val high = valueOf(2.173495)
        val low = valueOf(2.149165)
        val close = valueOf(2.15322)
        val closeAdj = valueOf(0.003195)
        val volume = valueOf(1089200)
        val stockDataCsv = StockDataCSV(name, "6/1/72", open, high, low, close, closeAdj, volume)
        val referenceStockData = StockData(name, LocalDate.of(1972, 6, 1), mapOf(
                OPEN to open,
                HIGH to high,
                LOW to low,
                CLOSE to close,
                ADJ_CLOSE to closeAdj,
                VOLUME to volume
        ))

        // When
        val resultStockData = batchConfiguration.processor().process(stockDataCsv)

        // Then
        assertEquals(resultStockData, referenceStockData)
    }

    @Test
    fun `onStartUp should run all the jobs in the repository`() {
        // Given
        val batchConfiguration = BatchConfiguration(emptyArray(), jobBuilderFactory, stepBuilderFactory, jobLauncher, jobRegistry, mongoTemplateMock)
        given(jobRegistry.jobNames).willAnswer { listOf("job1", "job2") }
        given(jobRegistry.getJob(anyString())).willAnswer { SimpleJob() }

        // When
        batchConfiguration.onStartUp()

        // Then
        verify(jobRegistry, times(2)).getJob(anyString())
        verify(jobLauncher, times(2)).run(any(), anyOrNull())
    }


}