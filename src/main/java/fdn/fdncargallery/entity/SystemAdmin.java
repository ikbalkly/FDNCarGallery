package fdn.fdncargallery.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class SystemAdmin extends BaseEmployee {
}
