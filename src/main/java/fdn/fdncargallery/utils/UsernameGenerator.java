package fdn.fdncargallery.utils;

import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameGenerator {

    private static final int MAX_ATTEMPTS = 99;

    private final IEmployeeRepository employeeRepository;

    /**
     * Kurumsal formatta, veritabanında benzersiz olduğu doğrulanmış bir kullanıcı adı döner.
     * Çakışma olursa sonuna sıra numarası ekler: MNG_B1_IkbalK_082026_2
     */
    public String generateUnique(String firstName, String lastName, Role role, Long branchId) {

        String base = UsernameGeneratorUtils.generateCorporateUsername(firstName, lastName, role, branchId);

        if (!employeeRepository.existsByUsername(base)) {
            return base;
        }

        for (int i = 2; i <= MAX_ATTEMPTS; i++) {
            String candidate = base + "_" + i;
            if (!employeeRepository.existsByUsername(candidate)) {
                return candidate;
            }
        }

        throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, base));
    }
}