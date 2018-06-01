package com.brocoding.stocks.api.stockdata.domain

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
data class AveragePrice(val name: String,
                        val description: String,
                        val type: DataType,
                        val start: LocalDate,
                        val end: LocalDate,
                        val average: BigDecimal)