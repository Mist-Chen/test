package com.sztus.teldrassil.sprint.repository.reader;


import com.sztus.framework.component.database.core.BaseJdbcReader;
import com.sztus.teldrassil.sprint.object.business.bo.RatioOfWorkloadBo;
import com.sztus.teldrassil.sprint.object.business.bo.SprintAndPlanIdBo;
import com.sztus.teldrassil.sprint.object.business.dto.TaskStatusCountDto;
import com.sztus.teldrassil.sprint.type.constant.SprintCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author free
 */
@Repository
public class SprintJdbcReader extends BaseJdbcReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SprintJdbcReader.class);

    /**
     * 根据lineId，分页查询sprint
     * @param lineId departmentId
     * @param page 页
     * @param size 大小
     * @return
     */
    public List<SprintAndPlanIdBo> findSprintByLineId(Long lineId, Integer page, Integer size) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> paramMap = new HashMap<>(1);
        sql.append(" SELECT t1.id,t1.`name`,t1.start_date,t1.due_date,t1.`status`,t2.id as planId ");
        sql.append(" FROM sprint t1 ");
        sql.append(" INNER JOIN sprint_plan t2 ON t1.id=t2.sprint_id ");

        if (Objects.nonNull(lineId)) {
            sql.append(" WHERE t2.line_id = :lineId ");
            paramMap.put(SprintCacheKey.LINE_ID, lineId);
        }

        //分页
        if (Objects.isNull(page) || page < 1) {
            page = 1;
        }
        if (Objects.isNull(size) || size < 1) {
            size = 10;
        }
        Integer index = (page - 1) * size;
        sql.append(" LIMIT :index, :size");
        paramMap.put(SprintCacheKey.INDEX, index);
        paramMap.put(SprintCacheKey.SIZE, size);

        return namedJdbcTemplate().query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(SprintAndPlanIdBo.class));
    }

    public List<TaskStatusCountDto> findTaskStatusCount(Set<Long> planIds) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> paramMap = new HashMap<>(1);
        sql.append(" SELECT t1.plan_id,t2.`status`,COUNT(t2.id) statusCount ");
        sql.append(" FROM relation_sprint_task_to_plan t1 ");


        sql.append(" INNER JOIN sprint_task t2 ON t1.task_id=t2.id ");
        if (Objects.nonNull(planIds)) {
            sql.append(" WHERE t1.plan_id  in ( :planIds) ");
            paramMap.put(SprintCacheKey.PLAN_IDS, planIds);
        }
        sql.append(" GROUP BY t1.plan_id,t2.`status` ");

        return namedJdbcTemplate().query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(TaskStatusCountDto.class));
    }

    public List<RatioOfWorkloadBo> findRatioOfWorkload(List<Long> employeeList, long currentTime, Long endTime) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> paramMap = new HashMap<>(1);
        sql.append(" SELECT assignee_id AS employeeId,date,`status` ");
        sql.append(" FROM sprint_task_assignee_workload ");
        sql.append(" WHERE date>= :begin AND date< :end ");
        paramMap.put(SprintCacheKey.BEGIN, currentTime);
        paramMap.put(SprintCacheKey.END, endTime);
        if (Objects.nonNull(employeeList)) {
            sql.append(" AND assignee_id in (:employeeList) ");
            paramMap.put(SprintCacheKey.EMPLOYEE_LIST, employeeList);
        }
        return namedJdbcTemplate().query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(RatioOfWorkloadBo.class));
    }
}
