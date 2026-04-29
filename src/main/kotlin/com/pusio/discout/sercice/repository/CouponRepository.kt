package com.pusio.discout.sercice.repository

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface CouponRepository : JpaRepository<CouponEntity, UUID> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    fun findByCodeNormalized(codeNormalized: String): CouponEntity?
}

@Entity
@Table(name = "coupons")
class CouponEntity(

    @Column(nullable = false)
    var code: String,

    @Column(name = "code_normalized", unique = true)
    var codeNormalized: String,

    @Column(name = "max_usages")
    var maxUsages: Int,

    @Column(name = "country_code")
    var countryCode: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    lateinit var createdAt: OffsetDateTime

    @Version
    @Column(name = "version")
    var version: Long? = null

    @OneToMany(mappedBy = "coupon")
    var usages: MutableList<CouponUsageEntity> = mutableListOf()
}