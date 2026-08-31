package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.SystemAdmin;
import fdn.fdncargallery.entity.UserAccount;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IBranchAdminMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.ISystemAdminRepository;
import fdn.fdncargallery.repository.IUserAccountRepository;
import fdn.fdncargallery.service.interfaces.IBranchAdminService;
import fdn.fdncargallery.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final IUserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;
    private final SecurityService securityService;
    private final MailService mailService;

    /*
     * Oluşturma akışı:
     * 1) requestle gelen şube id var mı
     * 2) şubenin zaten aktif bir yöneticisi var mı -> şube başına tek yönetici
     * 3) e-posta başkasında mı?
     * 4) tc hiçbir personelde yok mu?
     * 5) entity + UserAccount kur, rolü  BRANCH_ADMIN yap
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

        // user repoda gönderilen email kaydı var mı ?
        // varsa error fırlatı
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, request.getEmail()));
        }

        // employee repoda böyle bir tc kaydı var mı?
        // Bir kişi tek rolde görev yapabilir: gönderilen  TC herhangi bir personel
        // tablosunda varsa (müdür / danışman / yönetici) ikinci kayıt açılmaz.
        if (employeeRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_IDENTITY_ALREADY_EXISTS, request.getIdentityNumber()));
        }

        // gelen request Branch Admin entitysine maplernir
        SystemAdmin branchAdmin = branchAdminMapper.toEntity(request);

        // adminin şube bilgisi yukarıdaki şubeye setlenir
        branchAdmin.setBranch(branch);

        String username = usernameGenerator.generateUnique(
                request.getName(), request.getSurname(), Role.BRANCH_ADMIN, branch.getId());

        String temporaryPassword = UUID.randomUUID().toString();

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(username);
        userAccount.setEmail(request.getEmail());
        userAccount.setPassword(passwordEncoder.encode(temporaryPassword));
        userAccount.setRole(Role.BRANCH_ADMIN);
        userAccount.setFirstLogin(true);
        SystemAdmin savedBranchAdmin = systemAdminRepository.save(branchAdmin);
        userAccount.setEmployee(savedBranchAdmin);

        UserAccount savedUserAccount = userAccountRepository.saveAndFlush(userAccount);
        savedBranchAdmin.setUserAccount(savedUserAccount);

        log.info("Yeni şube yöneticisi oluşturuldu. id: {}, şube: {}", savedBranchAdmin.getId(), branch.getBranchName());

        mailService.sendTemporaryPassword(savedUserAccount.getEmail(), username, temporaryPassword);
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

        SystemAdmin updated = systemAdminRepository.save(existing);
        return branchAdminMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public BranchAdminResponseDto findBranchAdminById(Long id) {

        SystemAdmin branchAdmin = getBranchAdminEntityById(id);

        UserAccount currentUser = securityService.getUserAccount();
        if (currentUser.getRole() == Role.BRANCH_ADMIN
                && !branchAdmin.getId().equals(currentUser.getEmployee().getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece kendi bilgilerinizi görüntüleyebilirsiniz."));
        }

        return branchAdminMapper.toResponse(branchAdmin);
    }

    @Transactional
    @Override
    public List<BranchAdminResponseDto> findAllBranchAdmins() {

        // Şube yöneticisi listede tek eleman görür: kendisi.
        UserAccount currentUser = securityService.getUserAccount();
        if (currentUser.getRole() == Role.BRANCH_ADMIN) {
            SystemAdmin self = getBranchAdminEntityById(currentUser.getEmployee().getId());
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
        systemAdminRepository.save(branchAdmin);

        log.info("Şube yöneticisi pasife alındı, sistem erişimi kapandı. id: {}", id);
    }

    @Override
    public SystemAdmin getBranchAdminEntityById(Long id) {

        SystemAdmin candidate = systemAdminRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_NOT_FOUND, id.toString())));

        if (candidate.getUserAccount() == null || candidate.getUserAccount().getRole() != Role.BRANCH_ADMIN) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_ADMIN_NOT_FOUND, id.toString()));
        }
        return candidate;
    }
}
