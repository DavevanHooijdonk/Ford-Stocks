package com.brocoding.stocks.api.stockdata.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
@Document(collection = "StockData")
data class StockData(val name: String,
                     val date: LocalDate,
                     val dailyData: Map<DataType, BigDecimal>) {
    @Id
    val id: BigInteger? = null

}

