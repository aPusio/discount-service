package com.pusio.discout.sercice.service

import com.pusio.discout.sercice.controller.CreateCouponRequest
import com.pusio.discout.sercice.controller.CreateCouponResponse
import com.pusio.discout.sercice.controller.UseCouponRequest
import com.pusio.discout.sercice.repository.CouponEntity
import com.pusio.discout.sercice.repository.CouponRepository
import com.pusio.discout.sercice.repository.CouponUsageEntity
import com.pusio.discout.sercice.repository.CouponUsageRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Retryable(
    retryFor = [ObjectOptimisticLockingFailureException::class],
    maxAttempts = 3,
    backoff = Backoff(delay = 50)
)
@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponUsageRepository: CouponUsageRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun createCoupon(request: CreateCouponRequest): CreateCouponResponse {
        val coupon = request.toCouponEntity()
        try {
            val saved = couponRepository.save(coupon)
            log.info("Created coupon id=${saved.id} code=${saved.codeNormalized}")
            return saved.toCreateCouponResponse()
        } catch (_: DataIntegrityViolationException) {
            throw CouponAlreadyExistsException("Attempt to create duplicate coupon code: ${coupon.code}")
        }
    }

    @Transactional
    fun useCoupon(useCouponRequest: UseCouponRequest, clientIp: String) {
        val normalizedCode = useCouponRequest.code.trim().lowercase()
        val coupon = couponRepository.findByCodeNormalized(normalizedCode)
            ?: throw CouponNotFoundException("Coupon with code ${useCouponRequest.code} not found")

        validate(coupon, useCouponRequest)

        couponUsageRepository.save(
            CouponUsageEntity(
                coupon = coupon,
                userId = useCouponRequest.userId
            )
        )
        log.info("Coupon ${coupon.code} used successfully by user ${useCouponRequest.userId}")
    }

    private fun validate(
        coupon: CouponEntity,
        useCouponRequest: UseCouponRequest
    ) {
        //TODO use service to get ip
        val userCountry = "PL"

        if (coupon.countryCode != userCountry) {
            throw InvalidCouponUserCountryException("Coupon ${coupon.code} cannot be used by user ${useCouponRequest.userId}. Incorrect country: ${coupon.countryCode} != $userCountry")
        }

        val alreadyUsed = couponUsageRepository.existsByCouponIdAndUserId(
            coupon.id,
            useCouponRequest.userId
        )

        if (alreadyUsed) {
            throw CouponAlreadyUsedException("Coupon ${coupon.code} was already used by user ${useCouponRequest.userId}")
        }

        val usages = couponUsageRepository.countByCouponId(coupon.id)

        if (usages >= coupon.maxUsages) {
            throw CouponUsageLimitExceededException("Coupon usage limit exceeded. Coupon ${coupon.code} exceed ${coupon.maxUsages} usages")
        }
    }
}

private fun CouponEntity.toCreateCouponResponse() = CreateCouponResponse(
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

//TODO add rest handler for them
class CouponAlreadyExistsException(msg: String) : RuntimeException(msg)
class CouponNotFoundException(msg: String) : RuntimeException(msg)
class InvalidCouponUserCountryException(msg: String) : RuntimeException(msg)
class CouponAlreadyUsedException(msg: String) : RuntimeException(msg)
class CouponUsageLimitExceededException(msg: String) : RuntimeException(msg)