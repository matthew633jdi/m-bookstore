package matthew633jdi.dailyseed.mbookstore.category;

import matthew633jdi.dailyseed.mbookstore.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum CategoryErrorCode implements ErrorCode {
    DUPLICATE_CATEGORY_NAME(CONFLICT, "C-001", "이미 사용중인 카테고리입니다."),
    NOTFOUND_PARENT_CATEGORY(NOT_FOUND, "C-002", "등록되지 않은 부모 카테고리 ID입니다."),
    NOTFOUND_CATEGORY_ID(NOT_FOUND, "C-003", "등록되지 않은 카테고리 ID입니다."),
    NOTFOUND_CATEGORY_NAME(NOT_FOUND, "C-004", "등록되지 않은 카테고리입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CategoryErrorCode(HttpStatus httpStatus, String code, String message) {
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
