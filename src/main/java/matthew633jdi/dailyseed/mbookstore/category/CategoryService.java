package matthew633jdi.dailyseed.mbookstore.category;

import lombok.RequiredArgsConstructor;
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


}
