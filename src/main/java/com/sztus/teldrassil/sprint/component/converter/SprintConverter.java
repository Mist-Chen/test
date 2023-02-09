package com.sztus.teldrassil.sprint.component.converter;


import com.sztus.framework.component.core.util.DateUtil;
import com.sztus.teldrassil.sprint.object.business.bo.RatioOfWorkloadBo;
import com.sztus.teldrassil.sprint.object.business.bo.SprintAndPlanIdBo;
import com.sztus.teldrassil.sprint.object.business.bo.TaskStatusCountBo;
import com.sztus.teldrassil.sprint.object.business.vo.RatioOfWorkloadVo;
import com.sztus.teldrassil.sprint.object.business.vo.TaskStatusCountVo;
import com.sztus.teldrassil.sprint.object.response.SprintPlanResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author free
 **/
@Mapper(imports = DateUtil.class)
public interface SprintConverter {
    SprintConverter INSTANCE = Mappers.getMapper(SprintConverter.class);

    TaskStatusCountVo taskStatusCountBoToTaskStatusCountVo(TaskStatusCountBo taskStatusCountBo);

    @Mappings({
            @Mapping(target = "startDate", expression = "java(DateUtil.timeStampToStr(sprintAndPlanIdBo.getStartDate()))"),
            @Mapping(target = "dueDate", expression = "java(DateUtil.timeStampToStr(sprintAndPlanIdBo.getDueDate()))")
    })
    SprintPlanResponse sprintAndPlanIdBoToSprintPlanResponse(SprintAndPlanIdBo sprintAndPlanIdBo, TaskStatusCountVo taskStatusCountVo);


    @IterableMapping(qualifiedByName = "ratioOfWorkloadBoToRatioOfWorkloadVo")
    List<RatioOfWorkloadVo> ratioOfWorkloadBosToRatioOfWorkloadVos(List<RatioOfWorkloadBo> ratioOfWorkloadBos);

    @Named("ratioOfWorkloadBoToRatioOfWorkloadVo")
    @Mapping(target = "date", expression = "java(DateUtil.timeStampToStr(ratioOfWorkloadBo.getDate()))")
    RatioOfWorkloadVo ratioOfWorkloadBoToRatioOfWorkloadVo(RatioOfWorkloadBo ratioOfWorkloadBo);

}
