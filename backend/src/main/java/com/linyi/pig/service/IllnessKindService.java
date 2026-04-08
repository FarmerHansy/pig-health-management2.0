package com.linyi.pig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.pig.common.model.PageResult;
import com.linyi.pig.entity.IllnessKind;
import com.linyi.pig.entity.vo.illnessKind.IllnessKindAddVo;
import com.linyi.pig.entity.vo.illnessKind.IllnessKindQueryVo;
import com.linyi.pig.entity.vo.illnessKind.IllnessKindUpdateVo;


public interface IllnessKindService extends IService<IllnessKind> {
    /**
     * 分页查询
     *
     * @param illnessKindQueryVo 分页查询实体
     * @return PageResult<IllnessKind>
     */
    PageResult<IllnessKind> illnessKindPage(IllnessKindQueryVo illnessKindQueryVo);

    /**
     * 新增
     *
     * @param illnessKindAddVo 新增实体
     * @return Boolean
     */
    Boolean illnessKindAdd(IllnessKindAddVo illnessKindAddVo);

    /**
     * 修改
     *
     * @param illnessKindUpdateVo 修改实体
     * @return Boolean
     */
    Boolean illnessKindUpdate(IllnessKindUpdateVo illnessKindUpdateVo);
}
