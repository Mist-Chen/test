package com.sztus.teldrassil.sprint.object.domain;


import com.sztus.framework.component.database.annotation.Column;
import com.sztus.framework.component.database.annotation.Entity;
import lombok.Data;

/**
 * @author free
 */
@Entity
@Data
public class RelationSystemToDepartment {

    @Column
    private Long departmentId;

    @Column
    private Long systemId;
}
