package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    // id
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fdn_seq")
    @SequenceGenerator(name = "fdn_seq", sequenceName = "fdn_id_seq", allocationSize = 5)
    private Long id;

    // oluşturulma tarihi
    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    //güncelleme tarihi
    @Column(name = "update_time")
    @UpdateTimestamp
    private LocalDateTime updateTime;

}
