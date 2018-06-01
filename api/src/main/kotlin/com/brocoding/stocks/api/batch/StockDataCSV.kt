package com.brocoding.stocks.api.batch

import org.springframework.batch.item.ResourceAware
import org.springframework.core.io.Resource
import java.math.BigDecimal

data class StockDataCSV(var name: String? = null,
                        var date: String? = null,
                        var open: BigDecimal? = null,
                        var high: BigDecimal? = null,
                        var low: BigDecimal? = null,
                        var close: BigDecimal? = null,
                        var adjClose: BigDecimal? = null,
                        var volume: BigDecimal? = null) : ResourceAware {

    override fun setResource(resource: Resource?) {
        name = resource?.filename?.dropLast(4)
    }
}