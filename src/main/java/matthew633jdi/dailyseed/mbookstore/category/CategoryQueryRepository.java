package matthew633jdi.dailyseed.mbookstore.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static matthew633jdi.dailyseed.mbookstore.category.QCategory.*;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Category> findRootCategoriesWithChildren() {
        return jpaQueryFactory
                .selectFrom(category)
                .where(category.parent.isNull())
                .fetch();
    }
}