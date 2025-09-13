# 이커머스 API (Backend Portfolio)

## 1. 프로젝트 소개

실제 운영 및 확장성을 고려하여 설계한 이커머스 API입니다.

이 프로젝트는 단순히 상품을 판매하는 API를 구현하는 것을 넘어 신입 개발자로서 마주할 수 있는 과제들을 해결하기 위해 집중했습니다. **안전한 사용자 인증 시스템 구축, 데이터 무결성을 보장하는 트랜잭션 관리, 클라우드 인프라 설계, 그리고 수많은 트러블슈팅을 거친 CI/CD 파이프라인 구축** 등 백엔드 엔지니어의 핵심 역량을 고민했습니다.

---

### ✨ **API Docs**

- **[API Documentation (Swagger)](http://3.34.46.39:8081/swagger-ui/index.html)**

---

## 2. 주요 기능

- **🔐 사용자 인증**: JWT(JSON Web Token)와 Spring Security를 이용한 안전한 API 기반 회원가입 및 로그인 기능을 제공합니다.
- **📦 상품 관리**: 관리자와 일반 사용자의 권한을 분리하여 관리자만 상품을 등록, 수정, 삭제할 수 있는 기능을 구현했습니다.
- **🛒 장바구니**: 로그인한 사용자가 원하는 상품을 담고, 수량을 변경하고, 삭제하는 등의 장바구니 기능을 제공합니다.
- **💳 주문 및 결제**: 장바구니의 상품들을 기반으로 주문을 생성하고, 사용자의 내부 포인트를 이용한 결제 시뮬레이션 기능을 구현했습니다.

## 3. 적용 기술 및 개발 환경

- **Backend**: `Java 21`, `Spring Boot 3.x`, `Spring Security`, `Spring Data JPA`
- **Database**: `MySQL`
- **Build Tool**: `Gradle`
- **Infrastructure & DevOps**: `GitHub Actions`, `AWS EC2`, `AWS RDS`

## 4. 전체 시스템 아키텍처

<details>
<summary><b>클릭하여 아키텍처 확인하기</b></summary>

개발부터 배포, 서비스 운영까지의 전체 흐름을 나타내는 아키텍처 다이어그램입니다.

```mermaid
graph TD

    subgraph Development_&_CI/CD
        A[👨‍💻 Developer] -->|1. Git Push| B(GitHub Repository)
        B -->|2. Trigger| C{GitHub Actions}

        subgraph Pipeline
            C -->|3. Build| D[✅ .jar 생성]
            D -->|4. Deploy| E[🚀 EC2로 전송 및 재시작]
        end
    end

    subgraph AWS_Cloud_Infrastructure
        F[🌐 EC2 Instance<br>(ecommerce.jar)] -->|DB Connection| G[💾 RDS (MySQL)]
    end

    E -->|SSH| F

    subgraph User_Service_Flow
        I[👤 User] -->|API Request| F
    end

    style F fill:#FF9900,stroke:#333,stroke-width:2px
    style G fill:#0073BB,stroke:#333,stroke-width:2px
```

</details>

## 5. 문제 해결 및 개선 경험

이 프로젝트를 진행하며 마주했던 주요 기술적 문제들과, 이를 해결하며 **'왜'** 라는 질문을 던지고 더 나은 방향을 고민했던 경험입니다.

#### 1. 객체지향 설계
- **초기 설계**: 처음에는 `OrderService`에서 주문을 생성할 때 상품의 재고를 직접 변경(`product.setStockQuantity()`)하는 코드를 작성했습니다.
- **문제점**: 이 방식은 재고 변경과 관련된 모든 책임이 `Service` 계층에 집중되어 만약 다른 서비스에서도 재고를 변경해야 할 경우 로직이 중복되고 일관성이 깨질 위험이 있었습니다. 이는 객체지향의 **캡슐화(Encapsulation)** 원칙에 위배된다고 판단했습니다.
- **설계 개선**: `Product` 엔티티 내부에 `removeStock(int quantity)`이라는 **재고를 감소시키는 책임**을 가진 메소드를 만들었습니다. 이 메소드는 재고가 충분한지 스스로 검증하고 부족할 경우 예외를 발생시키는 로직까지 포함합니다. `OrderService`는 이제 단순히 `product.removeStock(int quantity)`을 호출하여 도메인에 작업을 위임하기만 하면 됩니다.
- **결과**: `Product`와 관련된 핵심 비즈니스 로직(재고 관리)을 **해당 도메인 객체가 직접 책임**지도록 하여 코드의 응집도를 높이고 유지보수성을 향상시키는 객체지향적인 설계를 경험했습니다.

#### 2. 데이터베이스
- **DTO를 통한 계층 분리**:
    - **문제점**: 엔티티 클래스를 API의 요청 및 응답 객체로 직접 사용하는 것의 위험성을 인지했습니다. 이 방식은 엔티티의 모든 필드가 외부에 그대로 노출될 수 있고, 엔티티의 내부 로직 변경이 API 스펙에 직접적인 영향을 주어 매우 불안정한 구조를 만들게 됩니다.
    - **설계**: 이러한 문제를 해결하기 위해 각 API의 목적에 맞는 별도의 DTO를 설계했습니다. 예를 들어, 회원가입 시에는 `UserSignupRequestDto`를, 상품 조회 시에는 `ProductResponseDto`를 사용하여 클라이언트와 시스템 내부의 데이터 표현을 분리했습니다.
    - **결과**: 엔티티를 외부로부터 완벽하게 보호하고 API의 요청/응답 스펙을 안정적으로 유지할 수 있게 되었습니다. 또한, `@Valid`를 이용한 유효성 검증 로직을 DTO 계층에서 처리하게 되어 각 클래스가 자신의 책임에만 집중하는 코드를 작성했습니다.
- **트랜잭션을 통한 데이터 일관성 확보**:
    - **문제점**: 여러 데이터를 동시에 변경해야 하는 작업 중 하나라도 실패하면, 데이터의 무결성이 훼손되는 문제가 발생할 수 있습니다.
    - **해결 과정**: **`@Transactional`** 어노테이션을 서비스 계층에 적용하여 모든 DB 작업을 하나의 단위로 묶었습니다. 이를 통해 중간에 예외가 발생하면 이전에 처리된 모든 작업이 자동으로 롤백(Rollback)되어 **데이터의 일관성과 무결성을 보장**하도록 설계했습니다.

#### 3. 인프라 및 배포 자동화
- **CI/CD**: 개발 생산성을 극대화하고 'push-to-deploy' 환경을 구축하기 위해 **GitHub Actions**를 이용한 배포 자동화 파이프라인을 설계하고 구현했습니다.
- **민감 정보의 안전한 관리**: 하드코딩되어 있던 모든 민감 정보를 **GitHub Secrets** 로 완벽하게 분리하여 코드 저장소의 보안을 확보했습니다.