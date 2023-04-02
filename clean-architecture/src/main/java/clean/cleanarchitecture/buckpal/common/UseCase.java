package clean.cleanarchitecture.buckpal.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface UseCase {

    /**
     * Component 가 있는 클래스의 빈 네임을 반환하는 로직인거 같은데??
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
