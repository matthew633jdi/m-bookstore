package matthew633jdi.dailyseed.mbookstore.category;

import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryQueryRepository categoryQueryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    @DisplayName("최상단 카테고리와 그 자식들까지 DTO로 가져오기")
    void success_get_root_categories() {
        //given
        Category backend = Category.create("backend");
        backend.addChild(Category.create("Java"));
        backend.addChild(Category.create("Spring"));

        Category kor = Category.create("korean");
        kor.addChild(Category.create("Novel"));
        kor.addChild(Category.create("Poetry"));

        List<Category> mockCategoreis = List.of(backend, kor);

        given(categoryQueryRepository.findRootCategories()).willReturn(mockCategoreis);

        //when & then
        List<CategoryResponse> response = categoryService.findRootCategories();

        assertAll(
                () -> assertThat(response).hasSize(2),
                () -> assertThat(response).extracting(CategoryResponse::name).containsExactly("backend", "korean"),
                () -> assertThat(response).filteredOn(c -> c.name().equals("backend"))
                        .flatExtracting(CategoryResponse::children)
                        .extracting(CategoryResponse::name)
                        .containsExactly("Java", "Spring")
        );

        then(categoryQueryRepository).should(times(1)).findRootCategories();
    }

    @Test
    @DisplayName("최상단 카테고리 정상 등록")
    void success_register_root_category() {
        //given
        String name = "backend";

        Category mockCategory = Category.create(name);
        ReflectionTestUtils.setField(mockCategory, "id", 1L);

        CreateCategoryRequest request = new CreateCategoryRequest(name, null);

        given(categoryRepository.existsByName(name)).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(mockCategory);

        //when
        Long resultId = categoryService.createCategory(request);

        //then
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        then(categoryRepository).should(times(1)).save(captor.capture());

        Category savedCategory = captor.getValue();

        assertAll(
                () -> assertThat(resultId).isEqualTo(1L),
                () -> assertThat(savedCategory.getName()).isEqualTo(name),
                () -> assertThat(savedCategory.getParent()).isNull()
        );
    }

    @Test
    @DisplayName("중분류(하위) 카테고리 정상 등록")
    void success_register_middle_category() {
        //given
        String rootName = "backend";
        String middleName = "Java";

        Category rootCategory = Category.create(rootName);
        ReflectionTestUtils.setField(rootCategory, "id", 1L);

        Category mockCategory = Category.create(middleName);
        ReflectionTestUtils.setField(mockCategory, "id", 2L);

        CreateCategoryRequest request = new CreateCategoryRequest(middleName, 1L);

        given(categoryRepository.existsByName(middleName)).willReturn(false);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(mockCategory);

        //when
        Long resultId = categoryService.createCategory(request);

        //then
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        then(categoryRepository).should(times(1)).save(captor.capture());

        Category savedCategory = captor.getValue();

        assertAll(
                () -> assertThat(resultId).isEqualTo(2L),
                () -> assertThat(savedCategory.getName()).isEqualTo(middleName),

                () -> assertThat(savedCategory.getParent().getId()).isEqualTo(1L),
                () -> assertThat(savedCategory.getParent().getName()).isEqualTo(rootName)
        );
    }

    @Test
    @DisplayName("이미 존재하는 카테고리 이름 등록 시도하여 실패")
    void fail_duplicated_name() {
        //given
        String name = "backend";
        CreateCategoryRequest request = new CreateCategoryRequest(name, null);

        given(categoryRepository.existsByName(name)).willReturn(true);

        //when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(DomainException.class)
                .hasMessage("이미 사용중인 카테고리입니다.")
                .satisfies(e -> {
                    DomainException domainException = (DomainException) e;
                    assertThat(domainException.getErrorCode()).isEqualTo(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
                });

        then(categoryRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("존재 하지 않는 부모ID 하위 생성 시도하여 실패")
    void fail_notfound_parentId() {
        //given
        String name = "backend";
        Long parentId = 1L;
        CreateCategoryRequest request = new CreateCategoryRequest(name, parentId);

        given(categoryRepository.existsByName(name)).willReturn(false);
        given(categoryRepository.findById(parentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(DomainException.class)
                .hasMessage("등록되지 않은 부모 카테고리 ID입니다.")
                .satisfies(e -> {
                    DomainException domainException = (DomainException) e;
                    assertThat(domainException.getErrorCode()).isEqualTo(CategoryErrorCode.NOTFOUND_PARENT_CATEGORY);
                });

        then(categoryRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("정상: 카테고리 ID를 통한 조회")
    void success_findById() {
        // given
        Long id = 1L;
        String name = "java";

        Category rootCategory = Category.create("backend");
        Category mockCategory = Category.create(name);
        rootCategory.addChild(mockCategory);
        mockCategory.addChild(Category.create("java8"));
        mockCategory.addChild(Category.create("java9"));
        mockCategory.addChild(Category.create("java10"));

        ReflectionTestUtils.setField(mockCategory, "id", id);
        given(categoryRepository.findById(id)).willReturn(Optional.of(mockCategory));
        // when
        CategoryResponse response = categoryService.findCategoryById(id);
        // then
        assertAll(
                () -> assertThat(response.name()).isEqualTo(name),
                () -> assertThat(response.children()).extracting(CategoryResponse::name).containsAll(List.of("java8", "java9", "java10"))
        );
    }

        @Test
        @DisplayName("실패: 카테고리 ID를 통한 조회 실패")
        void fail_findById() {
            // given
            Long notFoundId = 999L;

            given(categoryRepository.findById(notFoundId)).willReturn(Optional.empty());
            // when & then
            assertThatThrownBy(
                    () -> categoryService.findCategoryById(notFoundId)
            ).isInstanceOf(DomainException.class)
                    .hasMessage("등록되지 않은 카테고리 ID입니다.")
                    .satisfies(e -> {
                        DomainException domainException = (DomainException) e;
                        assertThat(domainException.getErrorCode()).isEqualTo(CategoryErrorCode.NOTFOUND_CATEGORY_ID);
                    });
    }
}