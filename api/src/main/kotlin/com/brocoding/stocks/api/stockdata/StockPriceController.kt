package com.brocoding.stocks.api.stockdata

import com.brocoding.stocks.api.error.ErrorDetails
import com.brocoding.stocks.api.stockdata.domain.AveragePriceRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import javax.validation.Valid

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
@RestController
@RequestMapping(value = ["/stockdata"], produces = [APPLICATION_JSON_VALUE])
class StockPriceController(private val stockPriceService: StockPriceService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockPriceController::class.java)
    }

    /**
     * @param request containing name, type, start, end and period
     *
     * @return 200 and a list with the requested AveragePrices if present
     *         else 404, "Not Found" or 400 "Bad Request" if incorrect input parameters
     */
    @GetMapping("/average")
    fun calculateAverage(@Valid request: AveragePriceRequest): ResponseEntity<out Any> {

        val averagePrices = stockPriceService.calculateAveragePrice(request)

        return if (averagePrices.isEmpty())
            formNotFoundResponse(request)
        else
            ResponseEntity(averagePrices, OK)
    }

    private fun formNotFoundResponse(request: AveragePriceRequest): ResponseEntity<ErrorDetails> {

        val (name, _, start, end, _, _) = request

        val errorDetails = ErrorDetails(LocalDateTime.now(UTC), "No data is found for $name stocks between $start and $end")
        LOGGER.error("Failed to find any records for: {}, during {} till {}", name, start, end)

        return ResponseEntity(errorDetails, NOT_FOUND)
    }
}
