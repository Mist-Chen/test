package com.sztus.teldrassil.sprint.object.domain;


import com.sztus.framework.component.database.annotation.Column;
import com.sztus.framework.component.database.annotation.Entity;
import com.sztus.framework.component.database.annotation.GeneratedValue;
import com.sztus.framework.component.database.annotation.Id;
import com.sztus.framework.component.database.enumerate.GenerationType;
import lombok.Data;

/**
 * @author free
 */
@Entity
@Data
public class SprintTaskAssigneeWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private Long assigneeId;

    @Column
    private Long date;

    @Column
    private Integer status;
}
