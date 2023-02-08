package com.sztus.teldrassil.sprint.component.converter;


import com.sztus.teldrassil.sprint.object.business.bo.SprintAndPlanIdBo;
import com.sztus.teldrassil.sprint.object.business.bo.TaskStatusCountBo;
import com.sztus.teldrassil.sprint.object.business.vo.TaskStatusCountVo;
import com.sztus.teldrassil.sprint.object.response.SprintPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

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
    SprintPlanResponse SprintAndPlanIdBoToSprintPlanResponse(SprintAndPlanIdBo sprintAndPlanIdBo);

}
