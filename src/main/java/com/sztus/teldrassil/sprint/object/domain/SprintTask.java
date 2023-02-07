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
public class SprintTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private Long systemId;

    @Column
    private String taskNo;

    @Column
    private Integer priority;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Long startDate;

    @Column
    private Long dueDate;

    @Column
    private Integer status;
}
