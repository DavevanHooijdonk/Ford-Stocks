package com.brocoding.stocks.api.error

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime.now
import java.time.ZoneOffset.UTC
import java.util.Collections.singletonList


/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RestControllerAdvice
class ValidationHandlerController {

    /**
     * @param exception IllegalArgumentException which contains information about the argument
     *
     * @return ErrorDetails containing detailed information about wrong input
     */
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ErrorDetails {

        return ErrorDetails(now(UTC), VALIDATION_FAILED, details = singletonList(PARAM_MISSING))
    }

    /**
     * @param exception BindingException which contains information about wrong fields validation
     *
     * @return ErrorDetails containing detailed information about wrong input
     */
    @ExceptionHandler(BindException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleBindException(exception: BindException): ErrorDetails {

        val fieldErrors = exception.bindingResult.fieldErrors
        val invalidParameters = fieldErrors.map { getCorrectFieldName(it.field) }
        val detailedMessages = fieldErrors.mapNotNull { getDetailedMessage(it) }

        return ErrorDetails(now(UTC), VALIDATION_FAILED, invalidParameters, detailedMessages)
    }

    private fun getDetailedMessage(field: FieldError): String? =
            when (field.field) {
                "type" -> WRONG_TYPE
                "period" -> WRONG_PERIOD
                else -> field.defaultMessage
            }

    private fun getCorrectFieldName(name: String): String =
            when (name) {
                "dateRequested" -> "period"
                else -> name
            }
}