package com.brocoding.stocks.api.stockdata.domain

import org.hibernate.validator.constraints.Range
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.PastOrPresent

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
data class AveragePriceRequest(@field:NotEmpty
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

    @field:AssertTrue(message = "period should be: DAYS, WEEKS, MONTHS, YEARS or DECADES")
    private val dateRequested: Boolean = period.isDateBased

}