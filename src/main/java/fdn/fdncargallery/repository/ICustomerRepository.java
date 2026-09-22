package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    // silinmişler dahil: TC sütunu tekil, silinmiş müşterinin satırı da aynı TC'yi tutuyor
    Optional<Customer> findByIdentityNumber(String identityNumber);

    List<Customer> findAllByDeletedAtIsNull();
}
