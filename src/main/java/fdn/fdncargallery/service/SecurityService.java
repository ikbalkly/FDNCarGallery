package fdn.fdncargallery.service;

import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.UserAccount;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.repository.IUserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final IUserAccountRepository userAccountRepository;

    // mevcut kullanıcıyı bulma
    public UserAccount getUserAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, username)));
    }


    public boolean isSuperAdmin() {
        return getUserAccount().getRole() == Role.SUPER_ADMIN;
    }

    public boolean isBranchAdmin() {
        return getUserAccount().getRole() == Role.BRANCH_ADMIN;
    }

    // kullanıcının bağlı olduğu branchId bulma
    public Long getCurrentBranchId() {
        UserAccount currentUser = getUserAccount();
        if (currentUser.getEmployee() == null || currentUser.getEmployee().getBranch() == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Kullanıcının bağlı olduğu branch bulunamadı"));
        }
        return currentUser.getEmployee().getBranch().getId();

    }

    // mevcut employee bulma
    public BaseEmployee getCurrentEmployee() {
        UserAccount currentUser = getUserAccount();
        if (currentUser.getEmployee() == null) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_FOUND, currentUser.getUsername()));
        }
        return currentUser.getEmployee();
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
