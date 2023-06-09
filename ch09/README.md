## 애플리케이션 조립하기

설정 컴포넌트를 따로 만들어서 애플리케이션을 조립하는 이유는 의존성이 올바른 방향을 가리키게 하기 위함이다.

의존성은 안쪽 도메인 코드 방향으로 향해야 도메인 코드가 바깥 계층의 변경으로부터 안전하다.

> 설정 컴포넌트의 역할
> * 웹 어댑터 인스턴스 생성
> * HTTP 요청이 웹 어댑터로 전달되도록 보장
> * 유스케이스 인스턴스 생성
> * 웹 어댑터에 유스케이스 인스턴스 제공
> * 영속성 어댑터 인스턴스 생성
> * 유스케이스에 영속성 어댑터 인스턴스 제공
> * 영속성 어댑터가 실제 데이터베이스에 접근할 수 있도록 보장 

설정 컴포넌트는 설정 파일이나 커맨드라인 파라미터 등과 같은 설정 파라미터의 소스에도 접근할 수 있어야 한다.

설정 컴포넌트는 단일 책임 원칙을 위반한다 하지만 애플리케이션을 깔끔하게 유지하려면 컴포넌트를 알고 컴포넌트들을 

연결해주는 설정 컴포넌트가 필요하다.

#### + 평범한 코드로 조립하기

```
class Applicatin{
public static void main(String[] args) { // 자바 애플리케이션은 메인부터 시작된다.

AccountRepository accountRepository = new AccountRepository();
ActivityRepository activityRepository = new ActivityRepository();

AccountPersistenceAdapter accountPersistenceAdapter 
    = new AccountPersistenceAdapter(accountRepository, activityRepository);

SendMoneyUseCase sendMoneyUseCase 
    = new SendMoneyUseService(
         accountPersistenceAdapter,  // 엔티티 조회 출력 포트와
         accountPersistenceAdapter); // 엔티티 업데이트 출력 포트 두 개를 사용하는 유스케이스  
       
SendMoneyController sendMoneyController 
     = new SendMoneyController(sendMoneyUseCase);

startProcessingWebRequests(sendMoneyController); // 웹 컨트롤러를 HTTP 로 노출하는 신비한 메서드
                                                    (이런건 한번도 안 만들어봄.. 신비하다..)

}
}
```

가장 원시적인 방법으로 조립해본 결과물이다. 

```
문제점

구현체가 많아지면 수동으로 일일이 작업해야 한다.

패키지 외부에서 인스턴스를 생성하기 때문에 클래스를 public 으로 만들어야 한다.

(public 으로 만들어진 클래스를 어디서나 접근할 수 있게 된다.)
```

#### + 스프링의 클래스패스 스캐닝으로 조립하기

보편적으로 사용하는 컴포넌트 애노테이션 기반의 스캔 방법

```
@Target({ElementType.Tye})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PersistenceAdapter{

@AliasFor(annotation = Component.class)
String value() default "";
}
```
이처럼 직접 애노테이션을 만들어서 어댑터에서 사용하면 아키텍처를 파악하는 데 도움을 준다.

클래스 패스 스캐닝방식 또한 단점이 없는 것은 아니다.

프레임 워크에 특화된 애노테이션을 사용해야 한다.

@Component 가 붙은 모든 클래스가 빈 객체로 컨테이너에 올라가기 때문에

컨테이너에 올리고싶지 않은 객체까지 올라갈 수 있다.

#### + 스프링의 자바 컨피그로 조립하기

```
@Configuration // 컴포넌트 스캔에서 사용할 설정이라는 표시
@EnableJpaRepositories // 스프링 데이터 JPA 로 만든 리포지토리를 사용하는 애노테이션
class PersistenceAdapterConfiguration{

@Bean
AccountPersistenceAdapter accountPersistenceAdapter{
    AccountRepository accountRepository,
    ActivityRepository activityRepository,
    AccountMapper accountMapper){
    
    return new AccountPersistenceAdapter(
    accountRepository,
    activityRepository,
    accountMappger);
    }
}

@Bean
AccountMapper accountMapper(){
    return new AccountMapper();
}

}
```

@Configuration 설정 클래스에 설정된 빈을 사용해서 스프링 컨테이너에 빈을 등록한다.

모든 빈을 가져오는 대신 설정을 따로 분리해서 관리할 수 있다.

PersistenceAdapterconfiguration 클래스를 만듦으로써 아웃 고잉 포트의 영속성 어댑터에서 사용하는(한정된) 객체를

빈으로 만들고 스프링 컨테이너에 올릴수 있게 되었다.

비슷한 방법으로 인커밍 포트의 웹 어댑터, 애플리케이션 계츠으이 특정 모듈들을 위한 설정 클래스도 만들 수 있다.

애플리케이션 계층에 @Component 애노테이션을 붙이도록 강제하지 않아서 프레임워크에 대한 의존성 없이 유지할 수 있다.

이 방법의 문제점은 설정 클래스가 생성하는 빈이 설정 클래스와 같은 패키지에 존재하지 않는다면 빈으로 만들 클래스들을 

public 으로 만들어야 한다. 

가시성을 제한하기 위해 패키지를 모듈 경계로 사용하고 각 패키지 안에 전용 설정 클래스를 만들 수 있지만 하위 패키지를 

사용할 수 없게 된다 ?? 

## 결론

프레임워크가 제공하는 컴포넌트 스캐닝을 이용하면 편리하게 빈 객체를 사용할 수 있지만 애플리케이션의 규모가 커지게 되면

스캐닝 되는 빈 객체의 가시성이 떨어지게 된다. 테스트에서 컨텍스트의 일부만 독립적으로 올리기도 힘들다.

@Configuration 설정 컴포넌트를 만들면 변경의 책임에서 자유로워지고 다른 모듈로부터 독립되어 코드 상에서 손쉽게 옮겨 다닐 

수 있는 응집도 높은 모듈을 만들 수 있다.




