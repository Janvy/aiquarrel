package com.aiquarrel.model.dto;

import com.aiquarrel.model.enums.StyleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateRequest {
    @NotBlank(message = "场景描述不能为空")
    private String scene;

    @NotBlank(message = "风格不能为空")
    private String style;

    public boolean isStyleValid() {
        return StyleEnum.isValid(this.style);
    }

    public boolean isSceneLengthValid() {
        return this.scene != null && this.scene.length() <= 200;
    }

    public boolean isSceneEmpty() {
        return this.scene == null || this.scene.isBlank();
    }
}
