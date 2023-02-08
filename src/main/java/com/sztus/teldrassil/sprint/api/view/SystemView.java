package com.sztus.teldrassil.sprint.api.view;

import lombok.Data;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 14:07
 * @DES:
 */
@Data
public class SystemView {
    private Long id;

    private String systemName;

    private String systemCode;

    private String version;

    private String updatedAt;

    private String createdAt;

    private Integer status;

    private String systemBrand;
}
