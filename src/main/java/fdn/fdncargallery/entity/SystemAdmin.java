package fdn.fdncargallery.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("ADMINS")
public class SystemAdmin extends BaseEmployee {
}
