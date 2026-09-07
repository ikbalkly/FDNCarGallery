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

    // silme tarihi
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // silen personel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private BaseEmployee deletedBy;

    public void softDelete(BaseEmployee actor) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = actor;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

}
