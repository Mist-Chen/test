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
public class SprintParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private Long sprintId;

    @Column
    private String key;

    @Column
    private String value;
}
