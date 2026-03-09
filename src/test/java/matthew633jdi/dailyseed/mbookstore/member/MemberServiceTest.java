package matthew633jdi.dailyseed.mbookstore.member;

import matthew633jdi.dailyseed.mbookstore.base.Address;
import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
        String street = "서울 서초구 서초대로 233";
        String detail = "서초역";
        String postalcode = "06590";

        JoinMemberRequest request = new JoinMemberRequest(email, name, phone, pwd, street, detail, postalcode);
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

        Address address = Address.builder().street(street).detail(detail).zipcode(postalcode).build();
        Member savedMockMember = Member.builder().email(email).name(name).phoneNumber(phone).password(encodedPassword).role(UserRole.USER).address(address).build();
        given(memberRepository.save(any(Member.class))).willReturn(savedMockMember);

        //when
        JoinMemberResponse response = memberService.join(request);

        //then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);    // 행위 검증(Behavior Verification)
        assertAll(
                () -> assertThat(response.email()).isEqualTo(email),
                () -> assertThat(response.name()).isEqualTo(name),
                () -> verify(memberRepository, times(1)).save(memberCaptor.capture()),
                () -> assertThat(memberCaptor.getValue().getEmail()).isEqualTo(email),
                () -> assertThat(memberCaptor.getValue().getAddress()).isEqualTo(address)
        );
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 이메일이면 예외가 발생한다")
    void join_fail_duplicateEmail() {
        // given
        String email = "dup@test.com";
        JoinMemberRequest request = new JoinMemberRequest(email, "테스터", "010-1234-5678", "Password123!", "서울 서초구 서초대로 233", "서초역", "06590");
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
        JoinMemberRequest request = new JoinMemberRequest("dup@test.com", "테스터", phone, "Password123!", "서울 서초구 서초대로 233", "서초역", "06590");
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

    @Test
    @DisplayName("회원 조회 성공: 정상적인 정보가 주어지면 조회")
    void search_success() {
        //given
        String phone = "010-1234-5678";
        String mail = "test@test.com";
        String name = "테스터";

        SearchMemberRequest request = new SearchMemberRequest(phone);

        Address address = Address.builder().street("sttt").detail("details").zipcode("123123").build();
        Member mockMember = Member.builder().email(mail).name(name).phoneNumber(phone).password("Password123!").role(UserRole.USER).address(address).build();
        Optional<Member> optionalMember = Optional.of(mockMember);
        given(memberRepository.findByPhoneNumber(phone)).willReturn(optionalMember);

        //when
        SearchMemberResponse response = memberService.findByPhone(request);

        //then
        assertAll(
                () -> assertThat(response.phone()).isEqualTo(phone),
                () -> assertThat(response.name()).isEqualTo(name),
                () -> assertThat(response.email()).isEqualTo(mail),
                () -> assertThat(response.address()).isEqualTo(address.getFullAddress())
        );
    }

    @Test
    @DisplayName("회원 조회 실패: 존재하지 않는 휴대전화 주어지면 조회 실패")
    void search_fail() {
        //given
        String phone = "010-1234-5678";

        SearchMemberRequest request = new SearchMemberRequest(phone);

        Optional<Member> optionalMember = Optional.empty();
        given(memberRepository.findByPhoneNumber(phone)).willReturn(optionalMember);

        //when & then
        assertThatThrownBy(() -> memberService.findByPhone(request))
                .isInstanceOf(DomainException.class)
                .hasMessage("등록되지 않은 휴대전화입니다.")
                .satisfies(e -> {
                    DomainException domainException = (DomainException) e;
                    assertThat(domainException.getErrorCode()).isEqualTo(MemberErrorCode.NOTFOUND_MEMBER);
                });
    }

}