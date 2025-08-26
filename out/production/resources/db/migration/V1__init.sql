-- 제품 스키마 (운영 습관: 금액은 정수, 상태는 문자열, 시간은 DB 기본값 + 애플리케이션 감시 이중화)
CREATE TABLE products (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(200)  NOT NULL,
    description   TEXT          NULL,
    category      VARCHAR(100)  NOT NULL,
    brand         VARCHAR(100)  NULL,
    price         BIGINT        NOT NULL CHECK (price >= 0),
    stock         INT           NOT NULL DEFAULT 0 CHECK (stock >= 0),
    status        VARCHAR(32)   NOT NULL, -- ACTIVE, HIDDEN, SOLD_OUT
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status   ON products(status);
CREATE INDEX idx_products_name     ON products(name);
