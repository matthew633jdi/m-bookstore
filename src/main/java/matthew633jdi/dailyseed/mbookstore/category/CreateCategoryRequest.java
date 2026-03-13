package matthew633jdi.dailyseed.mbookstore.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateCategoryRequest(
        @NotBlank(message = "카테고리 이름은 필수값입니다.")
        String name,

        @Positive(message = "값은 자연수여야합니다.")
        Long parentId
) {
}
