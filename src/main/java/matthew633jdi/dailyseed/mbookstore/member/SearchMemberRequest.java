package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SearchMemberRequest(
        @NotBlank(message = "휴대전화번호는 필수 입력값입니다.")
        @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$",
                message = "휴대전화는 010-xxxx-xxxx 형식이어야 합니다."
        )
        String phone
) {
}