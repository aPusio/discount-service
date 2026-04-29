package com.pusio.discout.sercice.controller

import com.pusio.discout.sercice.service.CouponService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.*

@RestController
@RequestMapping("/v1/api/coupons")
class CouponController(private val couponService: CouponService) {

    @PostMapping
    fun create(@Valid @RequestBody createCouponRequest: CreateCouponRequest) =
        ResponseEntity.ok(couponService.createCoupon(createCouponRequest))

    @PostMapping("/use")
    fun useCoupon(
        @Valid @RequestBody useCouponRequest: UseCouponRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        val clientIp = servletRequest.extractClientIp()
        couponService.useCoupon(useCouponRequest, clientIp)
        return ResponseEntity.ok().build()
    }

    private fun HttpServletRequest.extractClientIp() =
        this.getHeader("X-Forwarded-For")
            ?.let { forwardedFor -> forwardedFor.split(",")[0].trim() }
            ?: this.remoteAddr
}

data class CreateCouponResponse(
    val code: String,
    val maxUsages: Int,
    val countryCode: String,
    val id: UUID,
    val createdAt: OffsetDateTime,
)

data class CreateCouponRequest(
    val code: String,
    @field:Min(1)
    val maxUsages: Int,
    val countryCode: String
)

data class UseCouponRequest(
    val code: String,
    val userId: UUID
)