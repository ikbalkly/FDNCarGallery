package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    @Query("""
            SELECT ua FROM UserAccount ua
            JOIN FETCH ua.employee e
            LEFT JOIN FETCH e.branch
            WHERE ua.username = :username
            """)
    Optional<UserAccount> findByUsernameForAuthentication(@Param("username") String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
