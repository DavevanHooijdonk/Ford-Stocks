package com.brocoding.stocks.api.error

import java.time.LocalDateTime

/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
data class ErrorDetails(val timestamp: LocalDateTime,
                        val message: String,
                        val invalidParameters: List<String>? = null,
                        val details: List<String>? = null)