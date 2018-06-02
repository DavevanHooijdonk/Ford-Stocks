package com.brocoding.stocks.api.stockdata

import com.brocoding.stocks.api.stockdata.domain.AveragePrice
import com.brocoding.stocks.api.stockdata.domain.AveragePriceRequest
import com.brocoding.stocks.api.stockdata.domain.DataType.OPEN
import com.brocoding.stocks.api.stockdata.domain.StockData
import com.nhaarman.mockito_kotlin.anyOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.dao.QueryTimeoutException
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.time.LocalDate
import java.time.temporal.ChronoUnit.WEEKS
import java.time.temporal.ChronoUnit.MONTHS

/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RunWith(MockitoJUnitRunner::class)
class StockPriceServiceTest {

    private val date = LocalDate.of(2001, 1, 1)

    @Mock
    private lateinit var stockPriceRepository: StockPriceRepository

    private lateinit var stockPriceService: StockPriceService

    private val stockData1 = StockData("ford", date.minusDays(5), mapOf(OPEN to TEN))
    private val stockData2 = StockData("ford", date.minusDays(6), mapOf(OPEN to ZERO))
    private val stockData3 = StockData("ford", date.plusDays(51), mapOf(OPEN to TEN))

    @Before
    fun init() {
        stockPriceService = StockPriceServiceBasic(stockPriceRepository)
    }

    @Test
    fun `calculateAveragePrice should return emptyList if a DataAccessException is thrown`() {
        // Given
        val priceRequest = mock(AveragePriceRequest::class.java)
        given(stockPriceRepository.findByNameAndByDateBetween(anyOrNull(), anyOrNull(), anyOrNull())).willThrow(QueryTimeoutException::class.java)

        // When
        val result = stockPriceService.calculateAveragePrice(priceRequest)

        // Then
        assertEquals(emptyList<AveragePrice>(), result)
    }

    @Test
    fun `calculateAveragePrice should return the correct startdate, type, precision, average and period`() {
        // Given
        val precision = 6
        val startDate = date.minusDays(6)
        val priceRequest = AveragePriceRequest("ford", OPEN, startDate, date.plusDays(40), WEEKS, precision)

        given(stockPriceRepository.findByNameAndByDateBetween(anyOrNull(), anyOrNull(), anyOrNull())).willAnswer { listOf(stockData1, stockData2, stockData3) }

        // When
        val result = stockPriceService.calculateAveragePrice(priceRequest)

        // Then
        assertEquals("The result does not have the expected startDate", startDate, result.first().start)
        assertEquals("The result does not have the expected type", OPEN, result.first().type)
        assertEquals("The result does not have the expected size", 2, result.size)
        assertTrue("The result does not have the expected averages", result.map { it.average }.contains(valueOf(5000000, 6)))
        assertTrue("The result does not have the expected period", WEEKS.name.contains(result.first().description.dropLast(2), true))
    }

    @Test
    fun `calculateAveragePrice should return the requested end date if it is earlier the calculated last period date`() {
        // Given
        val endDate = date.plusDays(52)
        val priceRequest = AveragePriceRequest("ford", OPEN, date.minusDays(20), endDate, MONTHS)
        given(stockPriceRepository.findByNameAndByDateBetween(anyOrNull(), anyOrNull(), anyOrNull())).willAnswer { listOf(stockData1, stockData2, stockData3) }

        // When
        val result = stockPriceService.calculateAveragePrice(priceRequest)

        // Then
        assertEquals("The last date does not correspond tot he end date", endDate, result.last().end)
    }
}
