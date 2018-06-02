package com.brocoding.stocks.api.stockdata.domain

import com.brocoding.stocks.api.error.WRONG_PERIOD
import org.hibernate.validator.constraints.Range
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Pattern

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
data class AveragePriceRequest(@field:NotEmpty
                               @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "name should contain only alphanumeric characters")
                               val name: String,

                               val type: DataType,

                               @field:PastOrPresent(message = "start date should not be in the future")
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               val start: LocalDate,

                               @field:PastOrPresent(message = "end date should not be in the future")
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               val end: LocalDate,

                               val period: ChronoUnit,

                               @field:Range(min = 0, max = 6, message = "precision should be between 0 and 6")
                               val precision: Int = 2) {

    @field:AssertTrue(message = WRONG_PERIOD)
    private val dateRequested: Boolean = period.isDateBased

}