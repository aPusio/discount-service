package com.pusio.discout.sercice.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface CouponUsageRepository : JpaRepository<CouponUsageEntity, UUID> {
    fun existsByCouponIdAndUserId(
        couponId: UUID,
        userId: UUID
    ): Boolean
}

@Entity
@Table(
    name = "coupon_usages",
//    uniqueConstraints = [
//        UniqueConstraint(
//            name = "uq_coupon_usage_coupon_user",
//            columnNames = ["coupon_id", "user_id"]
//        )
//    ]
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
