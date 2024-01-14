package com.isp.backend.domain.image.entity;

import com.isp.backend.domain.schedules.entity.MySchedule;
import com.isp.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="image")
public class Image extends BaseEntity {

    @Id
    @Column(name="id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_schedule_id", nullable = false)
    private MySchedule mySchedule;

    @Column(name = "image_name")
    private String imageName ;

    @Column(name = "image_url")
    private String imageUrl ;

}