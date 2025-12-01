package com.learningassistant.backend.modules.spot.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 地区实体
 * 模块: spot (成员2)
 */
@Entity
@Table(name = "regions")
@Data
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer parentId;
    private String type; // province/city/area
}
