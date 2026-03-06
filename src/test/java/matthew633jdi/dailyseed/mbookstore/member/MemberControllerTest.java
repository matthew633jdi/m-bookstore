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
    @DisplayName("정상: 모든 조건이 맞으면 200 OK를 반환한다")
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(mail))
                .andExpect(jsonPath("$.name").value(name));
    }

}