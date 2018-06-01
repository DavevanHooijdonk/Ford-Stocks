package com.brocoding.stocks.api.stockdata

import com.brocoding.stocks.api.stockdata.domain.AveragePrice
import com.brocoding.stocks.api.stockdata.domain.AveragePriceRequest
import com.brocoding.stocks.api.stockdata.domain.StockData
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigDecimal.valueOf
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
interface StockPriceService {

    /**
     * @param request containing the information regarding the name, period, dates ect.
     *
     * @return a List of AveragePrices based on the requested period and start and end dates
     */
    fun calculateAveragePrice(request: AveragePriceRequest): List<AveragePrice>

}


@Service
class StockPriceServiceBasic(private val stockPriceRepository: StockPriceRepository) : StockPriceService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockPriceServiceBasic::class.java)
    }

    override fun calculateAveragePrice(request: AveragePriceRequest): List<AveragePrice> {

        return try {

            stockPriceRepository.findByNameIgnoreCaseAndByDateBetween(request.name, request.start, request.end)
                    .groupBy { request.period.between(request.start, it.date) }
                    .mapTo(ArrayList()) { (amount, stockData) -> createAveragePrice(amount, stockData, request) }

        } catch (e: DataAccessException) {

            LOGGER.error("Data retrieval from the database went wrong due to: {}", e.message)
            emptyList()

        }
    }

    private fun createAveragePrice(amount: Long, stockData: List<StockData>, request: AveragePriceRequest): AveragePrice {

        val (name, type, startDate, endDate, periodUnit, precision) = request

        val stockPriceDataSize = valueOf(stockData.size.toLong())

        val description = "${periodUnit.name.dropLast(1)} ${amount + 1}"
        val start = startDate.plus(amount, periodUnit)
        val end = calculateEndDate(amount, startDate, endDate, periodUnit)
        val averagePrice = stockData
                .mapNotNull { it.dailyData[type] }
                .reduce(BigDecimal::add)
                .divide(stockPriceDataSize, precision, RoundingMode.HALF_EVEN)

        return AveragePrice(name, description, type, start, end, averagePrice)
    }

    private fun calculateEndDate(amount: Long, startDate: LocalDate, endDate: LocalDate, periodUnit: ChronoUnit): LocalDate {

        val calculatedDate = startDate.plus(amount + 1, periodUnit).minusDays(1)

        return if (endDate.isBefore(calculatedDate)) endDate else calculatedDate
    }
}
