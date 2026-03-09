package matthew633jdi.dailyseed.mbookstore.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberService memberService;

    @Test
    @DisplayName("정상: 모든 조건이 맞으면 201 OK를 반환한다")
    @WithMockUser   // 1. 가짜 인증 사용자 권한 부여 (401 방어)
    void join_success() throws Exception {
        // given
        String mail = "test@test.com";
        String name = "테스터";
        JoinMemberRequest request = new JoinMemberRequest(mail, name, "010-1234-5678", "Password123!", "서울스트리스", "123-1", "12345");

        // 서비스 로직은 가짜로 통과하게 설정 (관심사 분리)
        JoinMemberResponse mockResponse = new JoinMemberResponse(mail, name);
        given(memberService.join(any())).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/members/signup")
                        .with(csrf())   // 2. 가짜 CSRF 토큰을 요청에 묻혀서 보냄 (403 방어)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // DTO -> JSON 변환
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(mail))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("실패: 이메일 형식이 틀리면 400 반환한다")
    @WithMockUser
    void join_fail_invalidEmail() throws Exception {
        // given
        String mail = "test";
        JoinMemberRequest request = new JoinMemberRequest(mail, "테스터", "010-1234-5678", "Password123!", "서울스트리스", "123-1", "12345");

        // when & then
        mockMvc.perform(post("/members/signup")
                        .with(csrf())   // 2. 가짜 CSRF 토큰을 요청에 묻혀서 보냄 (403 방어)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패: 비밀번호 형식이 틀리면 400 반환한다")
    @WithMockUser
    void join_fail_invalidPWD() throws Exception {
        // given
        String password = "password123!";
        JoinMemberRequest request = new JoinMemberRequest("test@test.com", "테스터", "010-1234-5678", password, "서울스트리스", "123-1", "12345");

        // when & then
        mockMvc.perform(post("/members/signup")
                        .with(csrf())   // 2. 가짜 CSRF 토큰을 요청에 묻혀서 보냄 (403 방어)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패: Content-Type이 application/json이 아니면 415 반환한다")
    @WithMockUser
    void join_fail_invalidContentType() throws Exception {
        // given
        JoinMemberRequest request = new JoinMemberRequest("test", "테스터", "010-1234-5678", "Password123!", "서울스트리스", "123-1", "12345");
        String invalidContent = "email=test@test.com&name=테스터&phone=010-1234-5678&password=Password123!&street_address=서울스트리스&detail_address=123-1&postal_code=12345";

        // when & then
        mockMvc.perform(post("/members/signup")
                        .with(csrf())   // 2. 가짜 CSRF 토큰을 요청에 묻혀서 보냄 (403 방어)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidContent))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("정상: 휴대전화 조건이 맞으면 200 OK를 반환한다")
    @WithMockUser   // 1. 가짜 인증 사용자 권한 부여 (401 방어)
    void search_success() throws Exception {
        // given
        String phone = "010-1234-5678";
        String mail = "test@test.com";
        String name = "테스터";
        String address = "서울특별시 서초구 서초동 123-1 111";


        SearchMemberRequest request = new SearchMemberRequest(phone);

        // 서비스 로직은 가짜로 통과하게 설정 (관심사 분리)
        SearchMemberResponse response = new SearchMemberResponse(mail, name, phone, address);
        given(memberService.findByPhone(request)).willReturn(response);

        // when & then
        mockMvc.perform(get("/members/find")
                        .with(csrf())
                        .queryParam("phone", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(mail))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    @DisplayName("실패: 휴대전화 형식이 맞지 않으면 400을 반환한다")
    @WithMockUser
    void search_fail() throws Exception {
        // given
        String phone = "010-12345678";
        String mail = "test@test.com";
        String name = "테스터";
        String address = "서울특별시 서초구 서초동 123-1 111";

        SearchMemberRequest request = new SearchMemberRequest(phone);

        // when & then
        mockMvc.perform(get("/members/find")
                        .with(csrf())
                        .queryParam("phone", phone))
                .andExpect(status().isBadRequest());
    }
}