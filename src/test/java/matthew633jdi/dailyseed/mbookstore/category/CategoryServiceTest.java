package matthew633jdi.dailyseed.mbookstore.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
}