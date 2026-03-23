package matthew633jdi.dailyseed.mbookstore.category;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getChildren().stream().map(CategoryResponse::from)
                        .toList()
        );
    }
}