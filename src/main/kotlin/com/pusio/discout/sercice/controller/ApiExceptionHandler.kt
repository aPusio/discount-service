package com.pusio.discout.sercice.controller

import com.pusio.discout.sercice.service.CouponAlreadyExistsException
import com.pusio.discout.sercice.service.CouponAlreadyUsedException
import com.pusio.discout.sercice.service.CouponNotFoundException
import com.pusio.discout.sercice.service.CouponUsageLimitExceededException
import com.pusio.discout.sercice.service.InvalidCouponUserCountryException
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CouponAlreadyExistsException::class)
    fun handleExists(ex: CouponAlreadyExistsException) =
        buildErrorResponse(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(CouponNotFoundException::class)
    fun handleNotFound(ex: CouponNotFoundException) =
        buildErrorResponse(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(InvalidCouponUserCountryException::class)
    fun handleInvalidCountry(ex: InvalidCouponUserCountryException) =
        buildErrorResponse(HttpStatus.FORBIDDEN, ex.message)

    @ExceptionHandler(CouponAlreadyUsedException::class)
    fun handleAlreadyUsed(ex: CouponAlreadyUsedException) =
        buildErrorResponse(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(CouponUsageLimitExceededException::class)
    fun handleUsageLimit(ex: CouponUsageLimitExceededException) =
        buildErrorResponse(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(
        ObjectOptimisticLockingFailureException::class,
        OptimisticLockingFailureException::class
    )
    fun handleOptimisticLock(ex: Exception): ResponseEntity<ApiErrorResponse> {

        log.warn("Optimistic locking conflict", ex)

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            "Coupon usage conflict. Please retry."
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {

        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "invalid value")
        }

        return ResponseEntity.badRequest().body(
            ApiErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation failed",
                details = errors
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<ApiErrorResponse> {

        log.error("Unhandled exception", ex)

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error"
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String?
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                error = message ?: status.reasonPhrase
            )
        )
}

data class ApiErrorResponse(
    val status: Int,
    val error: String,
    val details: Map<String, String>? = null
)