package com.pusio.discout.sercice.service

import com.pusio.discout.sercice.controller.CreateCouponRequest
import com.pusio.discout.sercice.controller.CreateCouponResponse
import com.pusio.discout.sercice.controller.UseCouponRequest
import com.pusio.discout.sercice.repository.CouponEntity
import com.pusio.discout.sercice.repository.CouponRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class CouponService (
    private val couponRepository: CouponRepository,
){
    private val log = LoggerFactory.getLogger(javaClass)

    fun createCoupon(request: CreateCouponRequest): CreateCouponResponse {
        val coupon = request.toCouponEntity()
        try {
            val saved = couponRepository.save(coupon)
            log.info("Created coupon id=${saved.id} code=${saved.codeNormalized}")
            return saved.toCreateCouponResponse()
        } catch (_: DataIntegrityViolationException) {
            log.warn("Attempt to create duplicate coupon code: ${coupon.code}")
            throw CouponAlreadyExistsException("Coupon with code ${coupon.code} already exists")
        }
    }

    fun useCoupon(useCouponRequest: UseCouponRequest, clientIp: String) {
        TODO("Not yet implemented")
    }
}

private fun CouponEntity.toCreateCouponResponse() =  CreateCouponResponse(
    code = this.code,
    maxUsages = this.maxUsages,
    countryCode = this.countryCode,
    id = this.id,
    createdAt = this.createdAt,
)

private fun CreateCouponRequest.toCouponEntity() = CouponEntity(
    code = this.code,
    codeNormalized = this.code.trim().lowercase(),
    maxUsages = this.maxUsages,
    //TODO enum with country codes?
    countryCode = this.countryCode,
)

class CouponAlreadyExistsException(msg: String): RuntimeException(msg)