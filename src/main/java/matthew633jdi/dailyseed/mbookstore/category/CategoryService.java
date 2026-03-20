package matthew633jdi.dailyseed.mbookstore.category;

import lombok.RequiredArgsConstructor;
import matthew633jdi.dailyseed.mbookstore.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

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

    public CategoryResponse findCategoryById(Long requestedId) {
        Category response = categoryRepository.findById(requestedId).orElseThrow(() -> new DomainException(CategoryErrorCode.NOTFOUND_CATEGORY_ID));
        return CategoryResponse.from(response);
    }


    public CategoryResponse findCategoryByName(String requestedName) {
        Category response = categoryRepository.findByName(requestedName).orElseThrow(() -> new DomainException(CategoryErrorCode.NOTFOUND_CATEGORY_NAME));
        return CategoryResponse.from(response);
    }

    @Transactional
    public void updateCategory(Long id, UpdateCategoryRequest request) {
        Category originCategory = categoryRepository.findById(id).orElseThrow(() -> new DomainException(CategoryErrorCode.NOTFOUND_CATEGORY_ID));

        String newName = request.name();

        if (originCategory.getName().equals(newName)) {
            return;
        }

        if (categoryRepository.existsByName(newName)) {
            throw new DomainException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        originCategory.changeName(request.name());
    }
}
