CREATE TABLE coupon_usages
(
    id         UUID PRIMARY KEY,
    coupon_id  UUID        NOT NULL REFERENCES coupons (id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE coupon_usages
    ADD CONSTRAINT uq_coupon_usage_coupon_user
        UNIQUE (coupon_id, user_id);

CREATE INDEX idx_coupon_usage_coupon_id
    ON coupon_usages(coupon_id);

CREATE INDEX idx_coupon_usage_user_id
    ON coupon_usages(user_id);
