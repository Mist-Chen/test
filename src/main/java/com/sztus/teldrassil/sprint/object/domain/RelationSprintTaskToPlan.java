package com.sztus.teldrassil.sprint.object.domain;


import com.sztus.framework.component.database.annotation.Column;
import com.sztus.framework.component.database.annotation.Entity;
import lombok.Data;

/**
 * @author free
 */
@Entity
@Data
public class RelationSprintTaskToPlan {
    @Column
    private Long planId;

    @Column
    private Long taskId;
}
