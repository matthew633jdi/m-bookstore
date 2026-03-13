package matthew633jdi.dailyseed.mbookstore.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService.findRootCategories();
    }

    @PostMapping
    public ResponseEntity<Void> registerCategory(@RequestBody @Valid CreateCategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryId)
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
