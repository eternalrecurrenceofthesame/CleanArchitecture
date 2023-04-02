package clean.cleanarchitecture.buckpal;

import clean.cleanarchitecture.buckpal.archunit.HexagonalArchitecture;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class DependencyRuleTests {

    /**
     * ArchUnitAPI 를 이용한 계층간 의존성 체크
     *
     * 주의 오타를 내버리면 위반 사례를 하나도 발견하지 못함
     * 이런 상황을 방지하기 위해 위반 사례를 찾지 못할 경우
     * 실패하는 테스트를 추가해야 한다.
     */

    @Test
    void domainLayerDoesNotDependOnApplicationLayer(){

        noClasses()
                .that()
                .resideInAPackage("buckpal.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("buckpal.application..")
                .check(new ClassFileImporter()
                        .importPackages("buckpal.."));
    }


    @Test
    void validateRegistrationContextArchitecture(){

        HexagonalArchitecture.boundedContext("account")
                .withDomainLayer("domain")
                .withAdaptersLayer("adapter")
                .incoming("web")
                .outgoing("persistence")
                .and()
                .withApplicationLayer("application")
                .services("service")
                .incomingPorts("port.in")
                .outgoingPorts("port.out")
                .and()
                .withConfiguration("configuration")
                .check(new ClassFileImporter()
                        .importPackages("buckpal.."));

        // check 체크 실행하고 패키지 읜존성이 의존성 규칙을 따라 유효하게 설정 됐는지 검증





    }

}
