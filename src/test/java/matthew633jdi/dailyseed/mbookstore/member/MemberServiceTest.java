package matthew633jdi.dailyseed.mbookstore.member;

import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원 가입 성공: 정상적인 정보가 주어지면 저장")
    void join_success() {
        //given
        String email = "test@test.com";
        String name = "테스터";
        String phone = "010-1234-5678";
        String pwd = "Password123!";
        String encodedPassword = "encodedPassword";

        JoinMemberRequest request = new JoinMemberRequest(email, name, phone, pwd);
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

        Member savedMockMember = Member.builder().email(email).name(name).phoneNumber(phone).password(encodedPassword).role(UserRole.USER).build();
        given(memberRepository.save(any(Member.class))).willReturn(savedMockMember);

        //when
        JoinMemberResponse response = memberService.join(request);

        //then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);    // 행위 검증(Behavior Verification)
        assertAll(
                () -> assertThat(response.email()).isEqualTo(email),
                () -> assertThat(response.name()).isEqualTo(name),
                () -> verify(memberRepository, times(1)).save(memberCaptor.capture()),
                () -> assertThat(memberCaptor.getValue().getEmail()).isEqualTo(email)

        );
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 이메일이면 예외가 발생한다")
    void join_fail_duplicateEmail() {
        // given
        String email = "dup@test.com";
        JoinMemberRequest request = new JoinMemberRequest(email, "테스터", "010-1234-5678", "Password123!");
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(DomainException.class)
                .hasMessage("이미 사용중인 이메일입니다.")
                .satisfies(e -> {
                    DomainException domainException = (DomainException) e;
                    assertThat(domainException.getErrorCode()).isEqualTo(MemberErrorCode.DUPLICATE_EMAIL);
                });
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 휴대전화면 예외가 발생한다")
    void join_fail_duplicatePhone() {
        // given
        String phone = "010-1234-5678";
        JoinMemberRequest request = new JoinMemberRequest("dup@test.com", "테스터", phone, "Password123!");
        given(memberRepository.existsByPhoneNumber(phone)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(DomainException.class)
                .hasMessage("이미 사용중인 휴대전화입니다.")
                .satisfies(e -> {
                    DomainException domainException = (DomainException) e;
                    assertThat(domainException.getErrorCode()).isEqualTo(MemberErrorCode.DUPLICATE_PHONE);
                });
    }
}