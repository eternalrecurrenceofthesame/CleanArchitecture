package clean.cleanarchitecture.buckpal.common;

import javax.validation.*;
import java.util.Set;

/**
 * 요 체크
 */
public abstract class SelfValidating<T>{

    private Validator validator;

    public SelfValidating(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 제약조건 검사 메서드
     *
     * 조건 검사 메서드를 추가해서 필요한 검사를 만들 수 있다.
     */
    protected void validateSelf(){
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }

    }
}
