CREATE TABLE coupons
(
    id              UUID PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    code_normalized VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    max_usages      INT         NOT NULL,
    current_usages  INT DEFAULT 0,
    country_code    VARCHAR(5)  NOT NULL,
    version         BIGINT
);
ALTER TABLE coupons
    ADD CONSTRAINT uq_coupon_code UNIQUE (code);