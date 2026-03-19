package matthew633jdi.dailyseed.mbookstore.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CategoryService categoryService;

    @Test
    @DisplayName("정상: 모든 조건이 맞으면 201 OK를 반환한다")
    @WithMockUser
    void join_success() throws Exception {
        // given
        String name = "backend";
        CreateCategoryRequest request = new CreateCategoryRequest(name, null);

        Long mockId = 1L;
        given(categoryService.createCategory(any())).willReturn(mockId);

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/categories/" + mockId)));
    }

    @Test
    @DisplayName("실패: 이미 존재하는 카테고리 이름이면 예외를 잡아 HTTP 상태코드로 변환한다")
    @WithMockUser
    void fail_duplicated_name_http_response() throws Exception {
        // given
        String name = "backend";
        CreateCategoryRequest request = new CreateCategoryRequest(name, null);

        given(categoryService.createCategory(any()))
                .willThrow(new DomainException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME));

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("C-001"))
                .andExpect(jsonPath("$.message").value("이미 사용중인 카테고리입니다."));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 부모 카테고리면 예외를 잡아 HTTP 상태코드로 변환한다")
    @WithMockUser
    void fail_notfound_parentId_http_response() throws Exception {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("backend", 1L);

        given(categoryService.createCategory(any()))
                .willThrow(new DomainException(CategoryErrorCode.NOTFOUND_PARENT_CATEGORY));

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("C-002"))
                .andExpect(jsonPath("$.message").value("등록되지 않은 부모 카테고리 ID입니다."));
    }

    @Test
    @DisplayName("실패: 카테고리 이름이 비어있으면 400 Bad Request 반환한다")
    @WithMockUser
    void fail_empty_name() throws Exception {
        // given
        String name = "";
        CreateCategoryRequest request = new CreateCategoryRequest(name, null);

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C-400"));

        then(categoryService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("실패: 부모 카테고리 번호가 자연수가 아니면 400 Bad Request 반환한다")
    @WithMockUser
    void fail_no_positive_parentId() throws Exception {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("backend", -12L);

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C-400"));

        then(categoryService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("실패: Content-Type이 application/json이 아니면 415 반환한다")
    @WithMockUser
    void fail_invalidContentType() throws Exception {
        // given
        String invalidContent = "name=backend&parentId=2";

        // when & then
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidContent))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("C-415"));
    }

    @Test
    @DisplayName("정상: 등록된 카테고리 ID를 통한 조회")
    @WithMockUser
    void success_findById() throws Exception {
        // given
        Long id = 1L;
        String name = "backend";
        CategoryResponse mockResponse = new CategoryResponse(id, name, null);
        given(categoryService.findCategoryById(id)).willReturn(mockResponse);

        mockMvc.perform(get("/categories/{categoryId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("실패: 없는 카테고리 ID를 통한 조회")
    @WithMockUser
    void fail_findById() throws Exception {
        // given
        Long notFoundId = 999L;
        given(categoryService.findCategoryById(notFoundId))
                .willThrow(new DomainException(CategoryErrorCode.NOTFOUND_CATEGORY_ID));

        mockMvc.perform(get("/categories/{categoryId}", notFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(CategoryErrorCode.NOTFOUND_CATEGORY_ID.getCode()))
                .andExpect(jsonPath("$.message").value(CategoryErrorCode.NOTFOUND_CATEGORY_ID.getMessage()));
    }

    @Test
    @DisplayName("정상: 등록된 카테고리 이름을 통한 조회")
    @WithMockUser
    void success_findByName() throws Exception {
        // given
        Long id = 1L;
        String name = "backend";
        CategoryResponse mockResponse = new CategoryResponse(id, name, null);
        given(categoryService.findCategoryByName(name)).willReturn(mockResponse);

        mockMvc.perform(get("/categories")
                        .queryParam("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("실패: 없는 카테고리 이름을 통한 조회")
    @WithMockUser
    void fail_findByName() throws Exception {
        // given
        String notFoundName = "unknown_category";

        given(categoryService.findCategoryByName(notFoundName))
                .willThrow(new DomainException(CategoryErrorCode.NOTFOUND_CATEGORY_NAME));

        mockMvc.perform(get("/categories")
                        .queryParam("name", notFoundName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(CategoryErrorCode.NOTFOUND_CATEGORY_NAME.getCode()))
                .andExpect(jsonPath("$.message").value(CategoryErrorCode.NOTFOUND_CATEGORY_NAME.getMessage()));
    }

}