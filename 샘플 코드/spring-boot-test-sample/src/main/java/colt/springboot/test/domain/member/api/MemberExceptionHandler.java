package colt.springboot.test.domain.member.api;

import colt.springboot.test.domain.member.exception.NotFoundMemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = MemberController.class)
public class MemberExceptionHandler {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<HttpStatus> handleNotFoundMemberException(NotFoundMemberException e) {
        log.error("존재하지 않는 멤버입니다.", e);
        return ResponseEntity.notFound().build();
    }
}
