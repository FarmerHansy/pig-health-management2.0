package com.linyi.pig.entity.vo.pageview;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linyi.pig.common.model.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "浏览量查询实体")
public class PageviewQueryVo extends PageResponse implements Serializable {

    /**
     * 浏览量-max
     */
    @Schema(name = "pageviewsMax",description = "浏览量",type = "int")
    private Integer pageviewsMax;

    /**
     * 浏览量-min
     */
    @Schema(name = "pageviewsMin",description = "浏览量",type = "int")
    private Integer pageviewsMin;

    /**
     * 病的id
     */
    @TableField("illness_id")
    @Schema(name = "illnessId",description = "病的id",type = "int")
    private Integer illnessId;

}
