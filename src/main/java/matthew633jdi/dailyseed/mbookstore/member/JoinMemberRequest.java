package matthew633jdi.dailyseed.mbookstore.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinMemberRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "이름은 필수 입력값입니다.")
         @Size(min = 2, max = 20)
        String name,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                message = "비밀번호는 대소문자, 숫자, 특수문자(@$!%*#?&)를 최소 1개 이상 포함해야 합니다."
        )
        String password
) {
    public Member toEntity(String encodedPassword) {
        return new Member(this.email, encodedPassword, this.name, UserRole.USER);
    }
}
