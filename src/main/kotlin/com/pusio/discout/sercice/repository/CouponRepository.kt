package com.pusio.discout.sercice.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface CouponRepository : JpaRepository<CouponEntity, Long> {
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

    @Column(name = "current_usages")
    var currentUsages: Int = 0

    @Version
    @Column(name = "version")
    var version: Long? = null
}