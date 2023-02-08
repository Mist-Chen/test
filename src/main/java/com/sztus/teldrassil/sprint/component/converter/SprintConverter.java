package com.sztus.teldrassil.sprint.component.converter;


import com.sztus.teldrassil.sprint.object.domain.SprintTask;
import com.sztus.teldrassil.sprint.object.domain.SprintTaskContent;
import com.sztus.teldrassil.sprint.object.view.TaskView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @Author free
 **/
@Mapper
public interface SprintConverter {
    SprintConverter INSTANCE = Mappers.getMapper(SprintConverter.class);

    @Mapping(source = "sprintTask.id",target = "id")
    TaskView sprintTaskAndContentToTaskView(SprintTask sprintTask, SprintTaskContent sprintTaskContent);
}
