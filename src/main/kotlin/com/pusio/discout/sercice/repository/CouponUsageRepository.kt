package com.pusio.discout.sercice.repository

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface CouponUsageRepository : JpaRepository<CouponUsageEntity, UUID> {
    fun existsByCouponIdAndUserId(
        couponId: UUID,
        userId: UUID
    ): Boolean

    fun countByCouponId(couponId: UUID): Long
}

@Entity
@Table(
    name = "coupon_usages",
)
class CouponUsageEntity(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    var coupon: CouponEntity,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: OffsetDateTime
}
