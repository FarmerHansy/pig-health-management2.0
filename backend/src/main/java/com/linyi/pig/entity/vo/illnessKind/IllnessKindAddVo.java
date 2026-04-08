package com.linyi.pig.entity.vo.illnessKind;

import com.baomidou.mybatisplus.annotation.TableField;
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
@Schema(name = "疾病种类新增实体")
public class IllnessKindAddVo implements Serializable {

    /**
     * 分类名称
     */
    @TableField("name")
    @Schema(name = "name",description = "分类名称",type = "varchar")
    private String name;

    /**
     * 描述
     */
    @TableField("info")
    @Schema(name = "info",description = "描述",type = "varchar")
    private String info;

}
