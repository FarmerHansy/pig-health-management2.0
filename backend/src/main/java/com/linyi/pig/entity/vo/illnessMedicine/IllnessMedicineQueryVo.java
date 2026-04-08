package com.linyi.pig.entity.vo.illnessMedicine;

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
@Schema(name = "疾病-药物查询实体")
public class IllnessMedicineQueryVo extends PageResponse implements Serializable {

    /**
     * 病id
     */
    @TableField("illness_id")
    @Schema(name = "illnessId",description = "病id",type = "int")
    private Integer illnessId;

    /**
     * 药品id
     */
    @TableField("medicine_id")
    @Schema(name = "medicineId",description = "药品id",type = "int")
    private Integer medicineId;

}
