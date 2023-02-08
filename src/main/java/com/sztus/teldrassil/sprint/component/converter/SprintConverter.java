package com.sztus.teldrassil.sprint.component.converter;


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
@Mapper
public interface SprintConverter {
    SprintConverter INSTANCE = Mappers.getMapper(SprintConverter.class);

    TaskStatusCountVo taskStatusCountBoToTaskStatusCountVo(TaskStatusCountBo taskStatusCountBo);

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    })
    SprintPlanResponse sprintAndPlanIdBoToSprintPlanResponse(SprintAndPlanIdBo sprintAndPlanIdBo);


    @IterableMapping(qualifiedByName = "ratioOfWorkloadBoToRatioOfWorkloadVo")
    List<RatioOfWorkloadVo> ratioOfWorkloadBosToRatioOfWorkloadVos(List<RatioOfWorkloadBo> ratioOfWorkloadBos);

    @Named("ratioOfWorkloadBoToRatioOfWorkloadVo")
    @Mapping(source = "date", target = "date", dateFormat = "yyyy-MM-dd")
    RatioOfWorkloadVo ratioOfWorkloadBoToRatioOfWorkloadVo(RatioOfWorkloadBo ratioOfWorkloadBo);

}
