package matthew633jdi.dailyseed.mbookstore.member;

import matthew633jdi.dailyseed.mbookstore.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_EMAIL(CONFLICT, "M-001", "이미 사용중인 이메일입니다."),
    DUPLICATE_PHONE(CONFLICT, "M-002", "이미 사용중인 휴대전화입니다."),
    NOTFOUND_MEMBER(NOT_FOUND, "M-003", "등록되지 않은 휴대전화입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
