package matthew633jdi.dailyseed.mbookstore.category;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank(message = "변경할 카테고리 이름을 입력해주세요.")
        String name
) {
}
