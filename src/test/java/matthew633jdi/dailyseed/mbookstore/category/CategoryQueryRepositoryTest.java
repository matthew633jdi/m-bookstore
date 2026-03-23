package matthew633jdi.dailyseed.mbookstore.category;

import jakarta.persistence.EntityManager;
import matthew633jdi.dailyseed.mbookstore.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({CategoryQueryRepository.class, JpaConfig.class})
class CategoryQueryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    CategoryQueryRepository categoryQueryRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        Category backend = Category.create("backend");
        backend.addChild(Category.create("Java"));
        backend.addChild(Category.create("Spring"));

        Category kor = Category.create("KOR");
        kor.addChild(Category.create("novel"));
        kor.addChild(Category.create("poetry"));

        categoryRepository.save(backend);
        categoryRepository.save(kor);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("심플 조회 + Batch size: 대분류 조회 후 자식은 IN 쿼리로 지연 로딩")
    void test_simple_query_with_batch_size() {
        // when
        List<Category> roots = categoryQueryRepository.findRootCategories();

        assertThat(roots.size()).isEqualTo(2);

        // then
        for (Category root : roots) {
            int childCount = root.getChildren().size();
            assertThat(childCount).isEqualTo(2);
        }
    }
}