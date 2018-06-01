package com.brocoding.stocks.api.error

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime.now
import java.time.ZoneOffset.UTC


/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RestControllerAdvice
class ValidationHandlerController {

    companion object {
        private const val MESSAGE = "Bad Request, Validation Failed"
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
        val detailMessages = fieldErrors.map { it.defaultMessage }

        return ErrorDetails(now(UTC), MESSAGE, invalidParameters, detailMessages)
    }

    private fun getCorrectFieldName(name: String): String =
            when (name) {
                "dateRequested" -> "period"
                else -> name
            }
}