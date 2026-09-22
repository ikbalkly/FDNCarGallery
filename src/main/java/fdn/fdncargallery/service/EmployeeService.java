package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.ResendPasswordResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IEmployeeMapper;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.service.interfaces.IEmployeeService;
import fdn.fdncargallery.service.interfaces.IRefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final MailService mailService;
    private final IRefreshTokenService refreshTokenService;

    @Transactional
    @Override
    public EmployeeSearchResultDto findEmployeeByIdentityNumber(SearchEmployeeRequestDto searchEmployeeRequestDto) {
        BaseEmployee employee = employeeRepository.findByIdentityNumber(searchEmployeeRequestDto.getIdentityNumber())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_FOUND, "Bu TC ile kayıtlı personel yok")));

        log.info("TC ile personel araması yapıldı. bulunan personel id: {}", employee.getId());
        return employeeMapper.toSearchResult(employee);
    }

    /*
     * Kayıt açıldı ama geçici şifre e-postası ulaşmadıysa kullanılır.
     * Kayıtlı şifre bcrypt hash'i olduğu için eskisi okunup gönderilemez: yenisi üretilir.
     */
    @Transactional
    @Override
    public ResendPasswordResultDto resendTemporaryPassword(SearchEmployeeRequestDto request) {

        BaseEmployee employee = employeeRepository.findByIdentityNumber(request.getIdentityNumber())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_FOUND, "Bu TC ile kayıtlı personel yok")));

        // Ayrılmış personele şifre gönderilmez; geri dönüyorsa yeniden işe alım ucu zaten yeni şifre üretiyor.
        if (!employee.isActive()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_ACTIVE, employee.getId().toString()));
        }

        BaseEmployee caller = securityService.getCurrentEmployee();

        // mevcut kullanıcı, tc ile kontrol edilen kişiye şifre sıfırlaması yapılabilir mi?
        securityService.checkBranchAccess(employee.getBranch().getId());
        checkResetAllowed(caller, employee);

        String temporaryPassword = UUID.randomUUID().toString();
        employee.setPassword(passwordEncoder.encode(temporaryPassword));
        employee.setFirstLogin(true);

        // Eski şifreyle açılmış oturumlar kapansın.
        refreshTokenService.revokeAllTokens(employee);
        employeeRepository.saveAndFlush(employee);

        mailService.resendTemporaryPassword(employee.getEmail(), employee.getUsername(), temporaryPassword);

        log.warn("Geçici şifre yeniden üretilip gönderildi. personel id: {}, username: {}, isteyen: {}",
                employee.getId(), employee.getUsername(), caller.getUsername());

        return new ResendPasswordResultDto(employee.getId(), employee.getEmail());
    }

    // Kimi açabiliyorsan onun şifresini sıfırlayabilirsin: müdür süper adminin hesabını kilitleyemesin.
    private void checkResetAllowed(BaseEmployee caller, BaseEmployee target) {

        boolean allowed = switch (caller.getRole()) {
            case SUPER_ADMIN -> true;
            case BRANCH_ADMIN -> target.getRole() == Role.MANAGER || target.getRole() == Role.SALES_REP;
            case MANAGER -> target.getRole() == Role.SALES_REP;
            default -> false;
        };

        if (!allowed) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Bu personelin şifresini sıfırlama yetkiniz yok."));
        }
    }
}