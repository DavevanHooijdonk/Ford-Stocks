package com.brocoding.stocks.api.stockdata

import com.brocoding.stocks.api.stockdata.StockPriceController
import com.brocoding.stocks.api.stockdata.domain.AveragePrice
import com.brocoding.stocks.api.stockdata.domain.DataType.CLOSE
import com.nhaarman.mockito_kotlin.any
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate.of
import java.util.Collections.emptyList

/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RunWith(SpringRunner::class)
@WebMvcTest(StockPriceController::class)
@WithMockUser
class StockPriceControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var stockPriceService: StockPriceService

    @Test
    fun `calculateAveragePrice should return Not Found if there is no data available`() {
        // Given
        given(stockPriceService.calculateAveragePrice(any())).willReturn(emptyList())

        // Then
        mvc.perform(get("/stockdata/average?name=ford&type=CLOSE&start=2018-01-01&end=2018-03-31&period=DAYS&precision=2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andExpect { jsonPath("$.timestamp").exists() }
                .andExpect { jsonPath("$.message").exists() }
    }

    @Test
    fun `calculateAveragePrice should return a helpful Bad Request if there is no data available`() {
        // Given
        given(stockPriceService.calculateAveragePrice(any())).willReturn(emptyList())

        // Then
        mvc.perform(get("/stockdata/average?name=ford&type=ADJ_CLOSE&start=2012-02-01&end=2012-02-29&period=HOURS&precision=-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect { jsonPath("$.timestamp").exists() }
                .andExpect { jsonPath("$.message").exists() }
                .andExpect { jsonPath("$.invalidParameters").exists() }
                .andExpect { jsonPath("$.details").exists() }
                .andExpect { jsonPath("$.invalidParameters.precision").exists() }
                .andExpect { jsonPath("$.invalidParameters.period").exists() }
                .andExpect { jsonPath("$.invalidParameters.start").doesNotExist() }
                .andExpect { jsonPath("$.invalidParameters.end").doesNotExist() }
    }

    @Test
    fun `calculateAveragePrice should return the average closing per week in a month properly`() {
        // Given
        val date = of(2012, 2, 1)
        val avgPrice1 = AveragePrice("ford","WEEK 1", CLOSE, date, date.plusWeeks(1).minusDays(1), BigDecimal.valueOf(5))
        val avgPrice2 = AveragePrice("ford","WEEK 2", CLOSE, date.plusWeeks(1), date.plusWeeks(2).minusDays(1), BigDecimal.valueOf(10))
        val avgPrice3 = AveragePrice("ford","WEEK 3", CLOSE, date.plusWeeks(2), date.plusWeeks(3).minusDays(1), BigDecimal.valueOf(15))
        val avgPrice4 = AveragePrice("ford","WEEK 4", CLOSE, date.plusWeeks(3), date.plusWeeks(4).minusDays(1), BigDecimal.valueOf(20))
        given(stockPriceService.calculateAveragePrice(any())).willReturn(listOf(avgPrice1, avgPrice2, avgPrice3, avgPrice4))

        // Then
        mvc.perform(get("/stockdata/average?name=ford&type=ADJ_CLOSE&start=2012-02-01&end=2012-02-29&period=WEEKS&precision=0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect { jsonPath("$.CLOSE").exists() }
                .andExpect { jsonPath("$.WEEK").exists() }
                .andExpect { jsonPath("$.2012-02-01").exists() }
                .andExpect { jsonPath("$.15").exists() }
                .andExpect { jsonPath("$.2012-02-29").exists() }
                .andExpect { jsonPath("$.end").exists() }
    }
}
