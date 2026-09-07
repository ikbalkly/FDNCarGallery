package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "models", uniqueConstraints = @UniqueConstraint(columnNames = {"brand_id", "model_name"}))
@SQLRestriction("deleted_at is null")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Model extends BaseEntity{

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
}
