package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.manager.CreateManagerRequestDto;
import fdn.fdncargallery.dto.manager.ManagerResponseDto;
import fdn.fdncargallery.dto.manager.UpdateManagerRequestDto;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.entity.UserAccount;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IManagerMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.IManagerRepository;
import fdn.fdncargallery.repository.IUserAccountRepository;
import fdn.fdncargallery.service.interfaces.IManagerService;
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
public class ManagerService implements IManagerService {

    private final IManagerRepository managerRepository;
    private final IManagerMapper managerMapper;
    private final IBranchRepository branchRepository;
    private final IEmployeeRepository employeeRepository;
    private final IUserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;
    private final SecurityService securityService;

    /*
     * Müdür oluşturma akışı:
     * 1) şube gerçekten var mı doğrula (istemciden sadece id geliyor)
     * 2) şube boşta mı kontrol et -> bir şubede yalnızca bir müdür olabilir
     * 3) kullanıcı adı / e-posta başkası tarafından alınmış mı kontrol et
     * 4) DTO'yu Manager entity'sine çevir, şubeyi DB'den gelen nesneyle set et
     * 5) UserAccount'u kur, şifreyi encode et, rolü SUNUCU tarafında MANAGER yap
     * 6) tek save ile kaydet -> cascade zinciri Manager ve Address'i de kaydeder
     * 7) müdürü şubeye ATA -> işlem tek çağrıda tamamlansın
     */
    @Transactional
    @Override
    public ManagerResponseDto createManager(CreateManagerRequestDto createManagerRequestDto) {

        // Şube admini yalnızca kendi şubesine müdür açabilir.
        // erişim kontrolü yapılır
        securityService.checkBranchAccess(createManagerRequestDto.getBranchId());

        // requestden gelen dto içerindeki branchId alanı db'de var mı? yoksa hata fırlatır
        Branch branch = branchRepository.findById(createManagerRequestDto.getBranchId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, createManagerRequestDto.getBranchId().toString())));
        // eğer şubedeki manager alanı doluysa "zaten müdür atanmış" uyarısı döner
        if (branch.getManager() != null) {
            throw new BaseException(new ErrorMessage(MessageType.MANAGER_ALREADY_ASSIGNED, branch.getBranchName()));
        }

        // aynı maille ikinci bir kayıt açılmasın diye
        if (userAccountRepository.existsByEmail(createManagerRequestDto.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, createManagerRequestDto.getEmail()));
        }

        // Bir kişi tek rolde görev yapabilir: bu TC herhangi bir personel
        // tablosunda varsa (müdür / danışman / yönetici) ikinci kayıt açılmaz.
        if (employeeRepository.existsByIdentityNumber(createManagerRequestDto.getIdentityNumber())) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_IDENTITY_ALREADY_EXISTS, createManagerRequestDto.getIdentityNumber()));
        }

        Manager manager = managerMapper.toEntity(createManagerRequestDto);
        manager.setBranch(branch);

        // kullanıcı adı kurumsal formatta sunucuda üretilir
        String username = usernameGenerator.generateUnique(
                createManagerRequestDto.getName(),
                createManagerRequestDto.getSurname(),
                Role.MANAGER,
                branch.getId());


        String temporaryPassword = UUID.randomUUID().toString();

        // yeni bir kullanıcı hesabı oluşturup, dataları setleniyor
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(createManagerRequestDto.getEmail());
        userAccount.setPassword(passwordEncoder.encode(temporaryPassword));
        userAccount.setFirstLogin(true);
        userAccount.setUsername(username);
        userAccount.setRole(Role.MANAGER);
        userAccount.setEmployee(manager);

        UserAccount savedUserAccount = userAccountRepository.saveAndFlush(userAccount);
        Manager savedManager = (Manager) savedUserAccount.getEmployee();

        savedManager.setUserAccount(savedUserAccount);

        branch.setManager(savedManager);
        branchRepository.save(branch);

        log.info("Yeni müdür oluşturuldu ve şubeye atandı. id: {}, şube: {}", savedManager.getId(), branch.getBranchName());
        ManagerResponseDto response = managerMapper.toResponse(savedManager);
        response.setTemporaryPassword(temporaryPassword);
        return response;
    }

    @Transactional
    @Override
    public ManagerResponseDto updateManager(UpdateManagerRequestDto updateManagerRequestDto, Long id) {

        // var olan müdürün id'si
        Manager existingManager = getManagerEntityById(id);

        // Hem müdürün MEVCUT şubesi hem ATANACAĞI şube erişim alanında olmalı;
        // aksi halde şube admini kendi müdürünü başka şubeye kaydeder.
        // Şimdilik sadece SUPER_ADMIN
        securityService.checkBranchAccess(existingManager.getBranch() != null ? existingManager.getBranch().getId() : null);
        securityService.checkBranchAccess(updateManagerRequestDto.getBranchId());

        if (existingManager.getBranch() == null || !existingManager.getBranch().getId().equals(updateManagerRequestDto.getBranchId())) {
            Branch newBranch = branchRepository.findById(updateManagerRequestDto.getBranchId())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, updateManagerRequestDto.getBranchId().toString())));

            // Hedef şubenin müdürü doluysa taşımaya izin verme: bir şubede tek müdür.
            if (newBranch.getManager() != null && !newBranch.getManager().getId().equals(id)) {
                throw new BaseException(new ErrorMessage(MessageType.MANAGER_ALREADY_ASSIGNED, newBranch.getBranchName()));
            }

            branchRepository.findByManagerId(id).ifPresent(oldBranch -> {
                oldBranch.setManager(null);
                branchRepository.saveAndFlush(oldBranch);
            });

            existingManager.setBranch(newBranch);
            newBranch.setManager(existingManager);
            branchRepository.save(newBranch);
        }

        // Adres dahil tüm alanlar yerinde güncellenir; yeni Address satırı açılmaz.
        managerMapper.updateManagerFromDto(updateManagerRequestDto, existingManager);

        Manager updatedManager = managerRepository.save(existingManager);
        return managerMapper.toResponse(updatedManager);
    }

    @Transactional
    @Override
    public ManagerResponseDto findManagerById(Long id) {

        Manager manager = getManagerEntityById(id);
        securityService.checkBranchAccess(manager.getBranch() != null ? manager.getBranch().getId() : null);

        UserAccount currentUser = securityService.getUserAccount();
        if (currentUser.getRole() == Role.MANAGER
                && !manager.getId().equals(currentUser.getEmployee().getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece kendi bilgilerinizi görüntüleyebilirsiniz."));
        }

        return managerMapper.toResponse(manager);
    }

    @Transactional
    @Override
    public List<ManagerResponseDto> findAllManagers() {
        // SUPER_ADMIN tüm aktif müdürleri; ADMIN ve MANAGER yalnızca kendi
        // şubesindekileri görür.
        List<Manager> managers = securityService.isSuperAdmin()
                ? managerRepository.findAllByActiveTrue()
                : managerRepository.findAllByBranchIdAndActiveTrue(securityService.getCurrentBranchId());

        return managers.stream()
                .map(managerMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public void deleteManager(Long id) {

        // istekte gönderilen id ile ilgili entity var mı kontrolü?
        Manager manager = getManagerEntityById(id);

        // Şube admini başka şubenin müdürünü pasife alamasın.
        securityService.checkBranchAccess(manager.getBranch() != null ? manager.getBranch().getId() : null);

        // silmek istenilen manager bir şubeye atanmış ise o şubedeki manager_id alanı null olarak setlenir
        branchRepository.findByManagerId(id).ifPresent(branch -> {
            branch.setManager(null);
            branchRepository.save(branch);
        });

        // managerin aktifliğini false yap
        manager.setActive(false);
        managerRepository.save(manager);

        log.info("Müdür pasife alındı. id: {}", id);
    }

    @Override
    public Manager getManagerEntityById(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.MANAGER_NOT_FOUND, id.toString())));
    }
}
