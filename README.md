![image](https://user-images.githubusercontent.com/86760552/130912831-8ec7077a-8f58-4f1c-b08e-56fc51640bac.png)

# 백신 예약 시스템

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 예제입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [예제 - 백신예약](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오 

백신 예약

기능적 요구사항
1. 정부관리자가 백신접종가능일자별로 접종병원, 백신수량을 등록한다.
2. 백신 등록 시 사용자에게 백신 등록 알림이 간다.
3. 예약자는 접종일자, 접종병원, 코로나 백신 종류를 선택하여 예약한다. 
4. 예약과 동시에 예약가능한 백신수량이 감소한다.
5. 예약이 되면 예약 확정 메시지가 예약자에게 전달된다.
6. 예약자는 예약을 취소할 수 있다.
7. 예약이 취소되면 백신수량이 증가하고, 취소내역 메시지가 예약자에게 전달된다. 
8. 예약자는 접종 예약정보(예약번호, 예약상태, 접종예정일자, 접종병원, 백신종류)를 조회할 수 있다.

비기능적 요구사항
1. 트랜잭션
   - 백신수량 감소가 되지 않은 건은 예약이 되지 않아야 한다.(Sync 호출)
2. 장애격리
   - 예약 시스템 과중되면 잠시 후에 하도록 유도한다.(Circuit breaker)
   - 백신 관리시스템이 문제가 있더라도 예약 취소는 받을 수 있어야 한다.(Async event-driven)
3. 성능
   - 예약정보를 한번에 확인할 수 있어야 한다.(CQRS)

# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계 

### 완성 모형 및 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/86760552/130928450-65ca9b8f-6c4d-4041-9638-0043c5f5aca3.png)

    - 정부관리자가 백신접종가능일자별로 접종병원, 백신수량을 등록한다 (ok)
    - 예약자는 접종일자, 접종병원, 코로나 백신 종류를 선택하여 예약한다 (ok)
    - 예약과 동시에 예약가능한 백신수량이 감소한다 (ok)
    - 백신 등록 시 사용자에게 백신 등록 알림이 간다 (ok)
    - 예약자는 예약을 취소할 수 있다 (ok)
    - 예약이 취소되면 백신수량이 증가하고, 취소내역 메시지가 예약자에게 전달된다 (ok)
    - 예약자는 접종 예약정보(예약번호, 예약상태, 접종예정일자, 접종병원, 백신종류)를 조회할 수 있다 (ok) 

   
    - 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증 

![image](https://user-images.githubusercontent.com/86760552/130930333-b0202777-9080-4cad-a52b-dc4a822d7aa5.png)

    (1) 백신수량 감소가 되지 않은 건은 예약이 되지 않아야 한다.(Sync 호출)
    (2) 예약 시스템 과중되면 잠시 후에 하도록 유도한다.(Circuit breaker)
    (3) 예약정보를 한번에 확인할 수 있어야 한다.(CQRS)
    (4) 백신 관리시스템이 문제가 있더라도 예약 취소는 받을 수 있어야 한다.(Async event-driven)

## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/86760552/130935503-58a4f6d4-7367-434f-87c6-eb7740fc07b8.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 Pub/Sub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현 

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 파이선으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
mvn spring-boot:run  

```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 VaccineMgmt 마이크로 서비스). 

```
package vaccinereservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="VaccineMgmt_table")
public class VaccineMgmt {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long reservationId;
    private Date availableDate;
    private String hospital;
    private String vaccineStatus;

    @PostPersist
    public void onPostPersist(){
        VaccineRegistered vaccineRegistered = new VaccineRegistered();
        BeanUtils.copyProperties(this, vaccineRegistered);
        vaccineRegistered.publishAfterCommit();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    public Date getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }
    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    public String getVaccineStatus() {
        return vaccineStatus;
    }

    public void setVaccineStatus(String vaccineStatus) {
        this.vaccineStatus = vaccineStatus;
    }

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

```
package vaccinereservation;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="vaccineMgmts", path="vaccineMgmts")
public interface VaccineMgmtRepository extends PagingAndSortingRepository<VaccineMgmt, Long>{

}

```
- 적용 후 REST API 의 테스트

```

백신 등록
http post localhost:8088/vaccineMgmts id=1 qty=100 availableDate=2021-08-30 hospital=seoul

백신 예약
http post localhost:8088/reservations vaccineId=1 userId=1 reservedDate=2021-08-27 reservationStatus=“reserved”

백신 취소
http patch localhost:8088/reservations/1 reservationStatus="cancelled

```

## 폴리글랏 퍼시스턴스

별다른 작업없이 기존의 Entity Pattern 과 Repository Pattern 적용과 데이터베이스 제품의 설정 (pom.xml) 만으로 hsqldb 로 부착시켰다

```
# pom.xml - in myPage 인스턴스

		<dependency>
			<groupId>org.hsqldb</groupId>
			<artifactId>hsqldb</artifactId>
			<scope>runtime</scope>
		</dependency>

```
![image](https://user-images.githubusercontent.com/86760552/131065064-33a9240f-c23e-4d18-8a4b-b893c1963c6e.png)


## 동기식 호출 과 Fallback 처리 

분석단계에서의 조건 중 하나로 백신예약->백신관리 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 백신관리 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# VaccineMgmtService.java

package vaccinereservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="vaccine", url="http://localhost:8088")
public interface VaccineMgmtService {
    @RequestMapping(method= RequestMethod.PUT, path="/vaccineMgmts/updateVaccine")
    public void updateVaccine(@RequestParam("vaccineId") long vaccineId);

}

```

- 예약을 받은 직후(@PostPersist) 백신 확보 및 예약 처리를 하도록 설계
```
# VaccineMgmt.java

    @PostPersist
    public void onPostPersist(){
        VaccineRegistered vaccineRegistered = new VaccineRegistered();
        vaccineRegistered.setId(this.getId());
        vaccineRegistered.setUserId(this.getUserId());
        vaccineRegistered.setHospital(this.getHospital());
        vaccineRegistered.setAvailableDate(this.getAvailableDate());        
        // vaccineRegistered.setQty(this.getQty());
        vaccineRegistered.setVaccineStatus("registered");

        BeanUtils.copyProperties(this, vaccineRegistered);
        vaccineRegistered.publishAfterCommit();

    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 백신관리 시스템이 장애가 나면 예약을 못받는다는 것을 확인:


- 백신 서비스 다운
![1  백신서비스다운](https://user-images.githubusercontent.com/86760552/131067753-bb9323ea-31ee-4ab7-9475-c78f994e450f.PNG)

- 백신 예약 - 에러
![2  백신예약 실패](https://user-images.githubusercontent.com/86760552/131067772-00eca8c2-1dbd-4cc6-8dd4-cf16cb60df48.PNG)

- 백신 서비스 개시
![3  백신재기동완료](https://user-images.githubusercontent.com/86760552/131067806-53bad80e-f4d9-427e-b829-7a2979f0a468.PNG)

- 백신 예약 - 성공
![4 백신예약완료](https://user-images.githubusercontent.com/86760552/131067855-6e7c34e0-e41e-4725-a9b8-6687ab33a8a4.PNG)


- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트 


백신 Notification 정보 관리는 비동기식으로 처리하여 백신관리 시스템의 처리에 영향을 주지 않도록 블로킹 처리가 되지 않도록 처리한다.
 
- 이를 위하여 예약 정보의 생성/변경의 기록을 남긴 후에 곧바로 생성/변경 되었다고 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package vaccinereservation;

@Entity
@Table(name="Reservation_table")
public class Reservation {

 ...
    @PostUpdate
    public void onPostUpdate() {
        final ReservationCancelled reservationCancelled = new ReservationCancelled();
        reservationCancelled.setId(this.getId());
        reservationCancelled.setUserId(this.getUserId());
        reservationCancelled.setHospital(this.getHospital());
        reservationCancelled.setReservedDate(this.getReservedDate());
        reservationCancelled.setReservationStatus("reserved");

        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.publishAfterCommit();
    }
```
- 백신 관리 서비스에서는 예약 취소 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
public class PolicyHandler{
    @Autowired NotificationRepository notificationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverVaccineRegistered_SendSms(@Payload VaccineRegistered vaccineRegistered){

        // if(!vaccineRegistered.validate()) return;

        System.out.println("\n\n##### listener SendSms : " + vaccineRegistered.toJson() + "\n\n");

        if(vaccineRegistered.validate()){

            /////////////////////////////////////////////
            // 취소 요청이 왔을 때 -> status -> cancelled 
            /////////////////////////////////////////////
            System.out.println("##### listener vaccineRegistered : " + vaccineRegistered.toJson());
            Notification noti = new Notification();
            // 취소시킬 Id 추출
            // long id = vaccineRegistered.getId(); // 취소시킬 Id

            // Optional<Notification> res = notificationRepository.findById(id);
            // Notification noti = res.get();

            noti.setUserId(vaccineRegistered.getUserId());
            noti.setMessage("관리자에 의해 백신이 등록되었습니다.");
            noti.setVaccineStatus("registered"); 

            // DB Update
            notificationRepository.save(noti);
        }
```
Sample Test 

백신 예약 시스템은 백신취소와 메시지 전송이 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, notification이 유지보수로 인해 잠시 내려간 상태라도 이벤트 전송받는데 문제가 없다.

- notification 서비시를 잠시 내려놓음 (ctrl+c)
![notification다운](https://user-images.githubusercontent.com/86760552/131071546-679b2e04-26cb-4fb5-b01f-b7a9a1c88c62.PNG)

- 예약 취소 처리 정상
![백신예약 취소](https://user-images.githubusercontent.com/86760552/131071595-3b4525cb-a645-47d3-97b2-bb75b2e30aad.PNG)

- notification 가동
![notification 서비스기동](https://user-images.githubusercontent.com/86760552/131071637-f5e6180a-9afc-401f-84d6-45a463fe6f9c.PNG)

- 예약 변경 확인
![정상적으로 취소알람발생 확인가능](https://user-images.githubusercontent.com/86760552/131071705-52474794-1ec6-48a1-8b47-2820135b6b12.PNG)


## CQRS

- Table 구조

![스크린샷 2021-08-27 오전 11 55 18](https://user-images.githubusercontent.com/86760552/131065313-35e846d8-e5c6-42fd-a3c0-c57660e0de88.png)

- viewpage MSA ViewHandler 를 통해 구현
```
@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenVaccineReserved_then_CREATE_1 (@Payload VaccineReserved vaccineReserved) {
        try {

            if (!vaccineReserved.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            // myPage.setId(vaccineReserved.getId());
            myPage.setUserId(vaccineReserved.getUserId());
            myPage.setHospital(vaccineReserved.getHospital());
            myPage.setReservedDate(vaccineReserved.getReservedDate());
            myPage.setReservationStatus(vaccineReserved.getReservationStatus());
            myPage.setUserId(vaccineReserved.getUserId());
            myPage.setHospital(vaccineReserved.getHospital());
            myPage.setReservedDate(vaccineReserved.getReservedDate());
            myPage.setReservationStatus(vaccineReserved.getReservationStatus());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenVaccineReserved_then_UPDATE_1(@Payload VaccineReserved vaccineReserved) {
        try {
            if (!vaccineReserved.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(vaccineReserved.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(vaccineReserved.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelled_then_UPDATE_2(@Payload ReservationCancelled reservationCancelled) {
        try {
            if (!reservationCancelled.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(reservationCancelled.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(reservationCancelled.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelled_then_UPDATE_3(@Payload ReservationCancelled reservationCancelled) {
        try {
            if (!reservationCancelled.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(reservationCancelled.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(reservationCancelled.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
```
- 실제로 view 페이지를 조회해 보면 모든 예약에 대한 전반적인 상태를 알수 있다.
![5  마이페이지 조회](https://user-images.githubusercontent.com/86760552/131079768-68df7fc5-a423-42c5-a1ac-751123c72714.PNG)

'''

## Correlation
Vanccine 관리 프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 이벤트 클래스 안의 변수로 전달받아 
서비스간 연관된 처리를 정확하게 구현하고 있습니다.

- 백신 예약
![2 백신예약](https://user-images.githubusercontent.com/86760552/131067002-f33d3330-d430-4e6d-b61d-9ead306f8ba2.PNG)

- 백신 취소
![3 백신취소](https://user-images.githubusercontent.com/86760552/131067043-e574c60c-6200-4c4a-b337-d2bdbc6b0884.PNG)

## API 게이트웨이
```
  1. gateway 스프링부트 App을 추가 후 application.yaml내에 각 마이크로 서비스의 routes 를 추가하고 gateway 서버의 포트를 8080 으로 설정함
   
      - application.yaml 예시
        spring:
          profiles: docker
          cloud:
            gateway:
              routes:
                - id: payment
                  uri: http://payment:8080
                  predicates:
                    - Path=/payments/** 
                - id: room
                  uri: http://room:8080
                  predicates:
                    - Path=/rooms/**, /reviews/**, /check/**
                - id: reservation
                  uri: http://reservation:8080
                  predicates:
                    - Path=/reservations/**
                - id: message
                  uri: http://message:8080
                  predicates:
                    - Path=/messages/** 
                - id: viewpage
                  uri: http://viewpage:8080
                  predicates:
                    - Path= /roomviews/**
              globalcors:
                corsConfigurations:
                  '[/**]':
                    allowedOrigins:
                      - "*"
                    allowedMethods:
                      - "*"
                    allowedHeaders:
                      - "*"
                    allowCredentials: true

        server:
          port: 8080            

  2. Kubernetes용 Deployment.yaml 을 작성하고 Kubernetes에 Deploy를 생성함
      - Deployment.yaml 예시
      
	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  name: gateway
	  labels:
	    app: gateway
	spec:
	  replicas: 1
	  selector:
	    matchLabels:
	      app: gateway
	  template:
	    metadata:
	      labels:
		app: gateway
	    spec:
	      containers:
		- name: gateway
		  image: user09acr.azurecr.io/gateway:latest
		  ports:
		    - containerPort: 8080

  3. Kubernetes용 Service.yaml을 작성하고 Kubernetes에 Service/LoadBalancer을 생성하여 Gateway 엔드포인트를 확인함. 
      - Service.yaml 예시
      
	apiVersion: v1
	kind: Service
	metadata:
	  name: gateway
	  labels:
	    app: gateway
	spec:
	  ports:
	    - port: 8080
	      targetPort: 8080
	  selector:
	    app: gateway
	  type:
	    LoadBalancer         
     
        Service 생성
        kubectl apply -f service.yaml      
```
Gateway Loadbal 확인
![gateway_LB](https://user-images.githubusercontent.com/86760552/131075921-affd92fb-b9e8-43ed-9530-e62c9eaba94e.jpg)



# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 Azure를 사용하였음.


- 도커 이미지


![도커 이미지](https://user-images.githubusercontent.com/86760552/131076024-b138926d-43b3-4ffe-9cf3-61b935d3bc6e.png)


- Azure Portal


![azure_potal](https://user-images.githubusercontent.com/86760552/131076080-9043917d-d1cc-4b8e-bdd0-a69157bf2e68.PNG)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 백신예약 요청--> 예약관리 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

- 피호출 서비스(백신관리)의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# Reservation.java 

    @PrePersist
    public void onPrePersist(){ 
        ...
        
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
$ siege -c100 -t60S -v --content-type "application/json" 'http://localhost:8081/reservations POST {"vaccineId": 1}'

```
![부하테스트2](https://user-images.githubusercontent.com/86760552/131077990-b536e8dd-2254-43bf-8a13-0f7f2e71f21f.png)


- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 91.55% 가 성공한 것은 고객 사용성에 있어 좋지 않기 때문에 Retry 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Retry 의 설정 (istio)
- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy reservations --min=1 --max=10 --cpu-percent=15
```
- CB 에서 했던 방식대로 워크로드를 1분 동안 걸어준다.
```
siege -c100 -t60S -v --content-type "application/json" 'http://localhost:8081/reservations POST {"vaccineId": 1}'

```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy reservation -w

```
- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다:

![autoscale 결과](https://user-images.githubusercontent.com/86760552/131078744-4ba84440-e04b-4727-87b7-d3fac60597ce.png)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t60S -v --content-type "application/json" 'http://localhost:8081/reservations POST {"vaccineId": 1}'


HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/reservations
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/reservations
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/reservations
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/reservations
:

```

- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:		        1278 hits
Availability:		       81.45 %
Elapsed time:		       60 secs
Data transferred:	        0.32 MB
Response time:		        5.60 secs
Transaction rate:	       16.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```
배포기간중 Availability 가 평소 100%에서 80% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```
# deployment.yaml 의 readiness probe 의 설정:


kubectl apply -f kubernetes/deployment.yaml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Transactions:		        1278 hits
Availability:		       100 %
Elapsed time:		        60 secs
Data transferred:	        0.34 MB
Response time:		        5.40 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

## Config Map

1: cofingmap.yml 파일 생성
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: reservation
data:
  API_URL_VACCINE: "http://vaccine:8080"

```
2: deployment.yml에 적용하기
```
spec:
containers:
- name: reservation
  image: user09acr.azurecr.io/reservation:latest
  ports:
    - containerPort: 8080
  envFrom: 
    - configMapRef:
	name: reservation
```
- 실제 모습

![configmap_kubectl](https://user-images.githubusercontent.com/86760552/131078470-747d7f86-f066-416b-ad54-59c808fb6181.jpg)

## Persistent Volume

1. persistent volume claim 생성 
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: vaccine-pvc
  labels:
    app: vaccine-pvc
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 2Ki
  storageClassName: azurefile

```
2. deployment 에적용

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: vaccine
  labels:
    app: vaccine
spec:
  replicas: 2
  selector:
    matchLabels:
      app: vaccine
  template:
    metadata:
      labels:
        app: vaccine
    spec:
      containers:
        - name: vaccine
          image: user09acr.azurecr.io/vaccine:latest
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          volumeMounts:
            - name: volume
              mountPath: "/apps/data"
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: vaccine-pvc
```
3. A pod에서 파일을 올리고 B pod 에서 확인

```
kubectl get pod
NAME                            READY   STATUS    RESTARTS   AGE
gateway-7cbb87bc9f-6zj5m        1/1     Running   0          3h13m
mypage-77dcbcb79b-q8n6b         1/1     Running   0          3h8m
notification-5c5b768898-8bg65   1/1     Running   0          3h7m
reservation-6b86b764bf-rmcwg    1/1     Running   0          18m
siege                           1/1     Running   0          124m
vaccine-5b54ccfc8-nf5lh         1/1     Running   0          56s
vaccine-fc887689b-5mjjd         1/1     Running   0          56s

kubectl exec -it vaccine-5b54ccfc8-nf5lh /bin/sh
/ # cd /apps/data
/apps/data # touch intensive_course_work

```
![pvc 파일 업로드 결과](https://user-images.githubusercontent.com/86760552/131083001-8fb955f3-f0ea-4272-a64f-0b4e3c678710.png)


