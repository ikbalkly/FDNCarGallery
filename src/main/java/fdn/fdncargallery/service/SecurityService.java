package fdn.fdncargallery.service;

import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final IEmployeeRepository employeeRepository;

    // mevcut kullanıcıyı bulma -> hesap bilgileri personel satırında olduğu için tek sorgu yeter
    public BaseEmployee getCurrentEmployee() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, username)));
    }


    public boolean isSuperAdmin() {
        return getCurrentEmployee().getRole() == Role.SUPER_ADMIN;
    }

    public boolean isBranchAdmin() {
        return getCurrentEmployee().getRole() == Role.BRANCH_ADMIN;
    }

    // kullanıcının bağlı olduğu branchId bulma
    public Long getCurrentBranchId() {
        BaseEmployee currentEmployee = getCurrentEmployee();
        if (currentEmployee.getBranch() == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Kullanıcının bağlı olduğu branch bulunamadı"));
        }
        return currentEmployee.getBranch().getId();
    }

    // şubeye erişimi kontrol etme
    public void checkBranchAccess(Long branchId) {
        if (isSuperAdmin()) {
            return;
        }
        if (branchId == null || !branchId.equals(getCurrentBranchId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece kendi şubenizin verilerine erişebilirsiniz."));
        }
    }

}
