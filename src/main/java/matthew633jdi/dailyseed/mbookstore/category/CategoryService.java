package matthew633jdi.dailyseed.mbookstore.category;

import lombok.RequiredArgsConstructor;
import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findRootCategories() {
        List<Category> rootCategories = categoryQueryRepository.findRootCategories();
        return rootCategories.stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public Long createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DomainException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        Category newCategory = Category.create(request.name());

        if (request.parentId() != null) {
            Category parentCategory = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new DomainException(CategoryErrorCode.NOTFOUND_PARENT_CATEGORY));

            parentCategory.addChild(newCategory);
        }

        return categoryRepository.save(newCategory).getId();
    }
}
