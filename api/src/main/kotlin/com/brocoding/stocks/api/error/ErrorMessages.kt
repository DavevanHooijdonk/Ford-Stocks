package com.brocoding.stocks.api.error

/**
 * Created by Dave van Hooijdonk on 2-6-2018.
 */

const val VALIDATION_FAILED = "Bad Request, Validation Failed"
const val PARAM_MISSING = "Request parameters are missing: name, type, start, end and period are mandatory"
const val WRONG_TYPE = "type should be: OPEN, HIGH, LOW, CLOSE, ADJ_CLOSE or VOLUME"
const val WRONG_PERIOD = "period should be: DAYS, WEEKS, MONTHS, YEARS or DECADES"