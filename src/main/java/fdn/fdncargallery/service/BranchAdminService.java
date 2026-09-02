package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.SystemAdmin;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IAddressMapper;
import fdn.fdncargallery.mapper.IBranchAdminMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.ISystemAdminRepository;
import fdn.fdncargallery.service.interfaces.IBranchAdminService;
import fdn.fdncargallery.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchAdminService implements IBranchAdminService {

    private final ISystemAdminRepository systemAdminRepository;
    private final IBranchAdminMapper branchAdminMapper;
    private final IBranchRepository branchRepository;
    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;
    private final SecurityService securityService;
    private final MailService mailService;
    private final IAddressMapper addressMapper;

    /*
     * Oluşturma akışı:
     * 1) requestle gelen şube id var mı
     * 2) şubenin zaten aktif bir yöneticisi var mı -> şube başına tek yönetici
     * 3) e-posta başkasında mı?
     * 4) tc hiçbir personelde yok mu?
     * 5) entity'yi kur, kimlik alanlarını set et, rolü BRANCH_ADMIN yap
     * 6) geçici şifre üret, ilk girişte değiştirmeyi zorunlu kıl
     */
    @Transactional
    @Override
    public BranchAdminResponseDto createBranchAdmin(CreateBranchAdminRequestDto request) {

        // requesden gelen branchID, şube repoda var mı?
        // varsa o datayı getir
        // yoksa hata fırlat
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, request.getBranchId().toString())));

        // admin repoda gönderilen şube id'sinde kayıtlı admin var mı?
        // true ise zaten admin var hatası fırlatılır
        if (systemAdminRepository.existsActiveByRoleAndBranch(Role.BRANCH_ADMIN, branch.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_ALREADY_ASSIGNED, branch.getBranchName()));
        }

        // TC kontrolü e-postadan ÖNCE: ayrılmış personel eski e-postasıyla geri
        // dönerse önce "pasif kaydın var" mesajını görmeli, e-posta çakışmasını değil.
        employeeRepository.findByIdentityNumber(request.getIdentityNumber()).ifPresent(existing -> {
            // Bir kişi tek rolde görev yapabilir: aktif kayıt varsa ikinci kayıt açılmaz.
            if (existing.isActive()) {
                throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_IDENTITY_ALREADY_EXISTS, request.getIdentityNumber()));
            }
            // Pasif kayıt: yeni satır değil, yeniden işe alım gerekiyor. id'yi mesaja koy.
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_INACTIVE_RECORD_EXISTS, existing.getId().toString()));
        });

        // user repoda gönderilen email kaydı var mı ?
        // varsa error fırlatı
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, request.getEmail()));
        }

        // gelen request Branch Admin entitysine maplernir
        SystemAdmin branchAdmin = branchAdminMapper.toEntity(request);

        // adminin şube bilgisi yukarıdaki şubeye setlenir
        branchAdmin.setBranch(branch);

        String username = usernameGenerator.generateUnique(
                request.getName(), request.getSurname(), Role.BRANCH_ADMIN, branch.getId());

        String temporaryPassword = UUID.randomUUID().toString();

        // e-posta mapper tarafından DTO'dan geliyor; geri kalan kimlik alanları sunucuda üretilir
        branchAdmin.setUsername(username);
        branchAdmin.setPassword(passwordEncoder.encode(temporaryPassword));
        branchAdmin.setRole(Role.BRANCH_ADMIN);
        branchAdmin.setFirstLogin(true);

        SystemAdmin savedBranchAdmin = systemAdminRepository.saveAndFlush(branchAdmin);

        log.info("Yeni şube yöneticisi oluşturuldu. id: {}, şube: {}", savedBranchAdmin.getId(), branch.getBranchName());

        mailService.sendTemporaryPassword(savedBranchAdmin.getEmail(), username, temporaryPassword);
        return branchAdminMapper.toResponse(savedBranchAdmin);
    }

    @Transactional
    @Override
    public BranchAdminResponseDto updateBranchAdmin(UpdateBranchAdminRequestDto request, Long id) {

        SystemAdmin existing = getBranchAdminEntityById(id);

        // Şube değişiyorsa hedef şube gerçekten var mı ve boşta mı?
        if (existing.getBranch() == null || !existing.getBranch().getId().equals(request.getBranchId())) {

            Branch targetBranch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, request.getBranchId().toString())));

            if (systemAdminRepository.existsActiveByRoleAndBranch(Role.BRANCH_ADMIN, targetBranch.getId())) {
                throw new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_ALREADY_ASSIGNED, targetBranch.getBranchName()));
            }
            existing.setBranch(targetBranch);
        }

        // Adres dahil tüm alanlar yerinde güncellenir; yeni Address satırı açılmaz.
        branchAdminMapper.updateBranchAdminFromDto(request, existing);

        SystemAdmin updated = systemAdminRepository.saveAndFlush(existing);
        return branchAdminMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public BranchAdminResponseDto findBranchAdminById(Long id) {

        SystemAdmin branchAdmin = getBranchAdminEntityById(id);

        BaseEmployee currentUser = securityService.getCurrentEmployee();
        if (currentUser.getRole() == Role.BRANCH_ADMIN
                && !branchAdmin.getId().equals(currentUser.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece kendi bilgilerinizi görüntüleyebilirsiniz."));
        }

        return branchAdminMapper.toResponse(branchAdmin);
    }

    @Transactional
    @Override
    public List<BranchAdminResponseDto> findAllBranchAdmins() {

        // Şube yöneticisi listede tek eleman görür: kendisi.
        BaseEmployee currentUser = securityService.getCurrentEmployee();
        if (currentUser.getRole() == Role.BRANCH_ADMIN) {
            SystemAdmin self = getBranchAdminEntityById(currentUser.getId());
            return List.of(branchAdminMapper.toResponse(self));
        }

        // Rol filtresi şart: filtresiz sorgu sistem yöneticisini de listeye katardı.
        return systemAdminRepository.findAllActiveByRole(Role.BRANCH_ADMIN)
                .stream()
                .map(branchAdminMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public void deleteBranchAdmin(Long id) {

        SystemAdmin branchAdmin = getBranchAdminEntityById(id);

        branchAdmin.setActive(false);
        branchAdmin.setTerminationDate(LocalDate.now());
        systemAdminRepository.saveAndFlush(branchAdmin);

        log.info("Şube yöneticisi pasife alındı, sistem erişimi kapandı. id: {}", id);
    }

    /*
     * Yeniden işe alım: ayrılmış şube yöneticisi geri döndüğünde yeni satır
     * açılmıyor, mevcut kayıt güncelleniyor. TC ve kullanıcı adı değişmez
     * (ikisi de entity'de updatable = false).
     */
    @Transactional
    @Override
    public BranchAdminResponseDto reactivateBranchAdmin(ReactivateEmployeeRequestDto request, Long id) {

        SystemAdmin branchAdmin = getBranchAdminEntityById(id);

        if (branchAdmin.isActive()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_ALREADY_ACTIVE, id.toString()));
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, request.getBranchId().toString())));

        // Şube başına tek yönetici: dönülecek şube boşta olmalı.
        if (systemAdminRepository.existsActiveByRoleAndBranch(Role.BRANCH_ADMIN, branch.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_ALREADY_ASSIGNED, branch.getBranchName()));
        }

        // E-posta yalnızca gönderildiyse ve gerçekten değiştiyse kontrol edilir.
        if (request.getEmail() != null && !request.getEmail().equals(branchAdmin.getEmail())){
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, request.getEmail()));
            }
            branchAdmin.setEmail(request.getEmail());
        }

        // Aylar sonra dönen personel taşınmış olabilir: adres gönderildiyse yenilenir.
        if (request.getAddress() != null) {
            branchAdmin.setAddress(addressMapper.toEntity(request.getAddress()));
        }

        if (request.getPhoneNumber() != null) {
            branchAdmin.setPhoneNumber(request.getPhoneNumber());
        }

        branchAdmin.setActive(true);
        branchAdmin.setTerminationDate(null);
        branchAdmin.setHireDate(request.getHireDate() != null ? request.getHireDate() : LocalDate.now());
        branchAdmin.setBranch(branch);
        branchAdmin.setBaseSalary(request.getBaseSalary());

        // yeni şifre üret
        String temporaryPassword = UUID.randomUUID().toString();
        branchAdmin.setPassword(passwordEncoder.encode(temporaryPassword));
        branchAdmin.setFirstLogin(true);

        SystemAdmin reactivated = systemAdminRepository.saveAndFlush(branchAdmin);

        log.info("Şube yöneticisi yeniden işe alındı. id: {}, şube: {}", reactivated.getId(), branch.getBranchName());
        mailService.sendTemporaryPassword(reactivated.getEmail(), reactivated.getUsername(), temporaryPassword);
        return branchAdminMapper.toResponse(reactivated);
    }

    @Override
    public SystemAdmin getBranchAdminEntityById(Long id) {

        SystemAdmin candidate = systemAdminRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_NOT_FOUND, id.toString())));

        if (candidate.getRole() != Role.BRANCH_ADMIN) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_NOT_FOUND, id.toString()));
        }
        return candidate;
    }
}
