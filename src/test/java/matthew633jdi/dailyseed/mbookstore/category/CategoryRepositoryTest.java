package matthew633jdi.dailyseed.mbookstore.category;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("부모 카테고리 저장하면 자식 카테고리도 함께 연쇄 저장(Cascade)")
    void save_cascade() {
        // given
        String novelStr = "소설";

        Category kor = Category.create("국내도서");

        kor.addChild(Category.create(novelStr));
        kor.addChild(Category.create("시"));

        // when
        categoryRepository.save(kor);

        em.flush();
        em.clear();

        Category foundParent = categoryRepository.findById(kor.getId()).orElseThrow();

        assertThat(foundParent.getChildren()).hasSize(2);
        assertThat(foundParent.getChildren().get(0).getName()).isEqualTo(novelStr);
    }

    @Test
    @DisplayName("부모 카테고리 삭제하면 자식 카테고리도 함께 연쇄 삭제(Cascade)")
    void delete_cascade() {
        // given
        String novelStr = "소설";

        Category kor = Category.create("국내도서");

        kor.addChild(Category.create(novelStr));
        Category novel = Category.create("시");
        kor.addChild(novel);

        categoryRepository.save(kor);

        em.flush();
        em.clear();


        // when
        Category targetParent = categoryRepository.findById(kor.getId()).orElseThrow();
        categoryRepository.delete(targetParent);

        em.flush();
        em.clear();

        assertThat(categoryRepository.findById(kor.getId())).isEmpty();
        assertThatThrownBy(() -> categoryRepository.findById(novel.getId()).orElseThrow(IllegalArgumentException::new)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("부모 객체에서 제거한 고아 객체 삭제(orphanRemoval)")
    void orphan_removal_test() {
        // given
        Category kor = Category.create("국내도서");
        kor.addChild(Category.create("소설"));

        categoryRepository.save(kor);
        em.flush();
        em.clear();

        // when
        Category foundParent = categoryRepository.findById(kor.getId()).orElseThrow();
        Category targetChild = foundParent.getChildren().get(0);

        foundParent.getChildren().remove(targetChild);

        em.flush();
        em.clear();

        // then
        assertThatThrownBy(() -> categoryRepository.findById(targetChild.getId()).orElseThrow(IllegalArgumentException::new))
                .isInstanceOf(IllegalArgumentException.class);
    }
}