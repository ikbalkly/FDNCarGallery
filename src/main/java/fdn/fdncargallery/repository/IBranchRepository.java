package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByManagerId(Long managerId);

    // kapatılmış şube adı da rezerve kalır: aynı adla ikinci kayıt açılamaz
    boolean existsByBranchName(String branchName);

    // listelemede yalnızca açık şubeler görünür
    List<Branch> findAllByDeletedAtIsNull();

    // kapatılmış şubeye personel ya da araç atanamasın diye
    Optional<Branch> findByIdAndDeletedAtIsNull(Long id);
}
