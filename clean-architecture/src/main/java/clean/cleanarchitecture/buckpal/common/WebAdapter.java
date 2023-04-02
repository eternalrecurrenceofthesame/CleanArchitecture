package clean.cleanarchitecture.buckpal.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented // 이 어노테이션을 JAVA DOC 에 포함시킨다.
@Component // 어노테이션 내에 컴포넌트를 넣어서 스캔 대상으로 만들어버림. (이게 뭔가 했네 ㅎㅎ...)
public @interface WebAdapter {

    /**
     * value String 값으로 컴포넌트 스캔 된 빈의 이름을 가진다.
     *
     * @Service,
     * @Controller
     *
     * 얘네들 만들 때랑 똑같이 만들어주려고 넣은 거 같긴 한데
     *
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     *
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}
