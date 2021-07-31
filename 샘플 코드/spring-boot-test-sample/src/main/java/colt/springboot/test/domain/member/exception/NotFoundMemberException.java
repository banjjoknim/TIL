package colt.springboot.test.domain.member.exception;

public class NotFoundMemberException extends RuntimeException {
    public NotFoundMemberException(String message) {
        super(message);
    }
}
