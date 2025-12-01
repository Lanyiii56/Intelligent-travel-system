package com.learningassistant.backend.modules.recommend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 智能推荐请求参数
 * 模块: recommend (成员3)
 */
@Data
public class RecommendRequest {

    @NotNull(message = "地区ID不能为空")
    private Integer regionId;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 120, message = "年龄不能超过120")
    private Integer age;

    @NotNull(message = "游玩时间不能为空")
    @Min(value = 30, message = "游玩时间至少30分钟")
    private Integer playTime;

    @NotNull(message = "人数不能为空")
    @Min(value = 1, message = "人数至少1人")
    @Max(value = 50, message = "人数不能超过50人")
    private Integer peopleCount;

    private Double budget;
    private String preference;
}
