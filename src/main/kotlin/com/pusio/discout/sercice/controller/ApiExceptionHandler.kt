package com.pusio.discout.sercice.controller

import com.pusio.discout.sercice.service.CouponAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(CouponAlreadyExistsException::class)
    fun handleExists(ex: CouponAlreadyExistsException) = ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to ex.message))

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception) = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to ex.message))
}