package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.dto.salesRep.UpdateSalesRepRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.SalesRep;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IAddressMapper;
import fdn.fdncargallery.mapper.ISalesRepMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.ISalesRepRepository;
import fdn.fdncargallery.service.interfaces.IRefreshTokenService;
import fdn.fdncargallery.service.interfaces.ISalesRepService;
import fdn.fdncargallery.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesRepService implements ISalesRepService {

    private final ISalesRepRepository salesRepRepository;
    private final ISalesRepMapper salesRepMapper;
    private final SecurityService securityService;
    private final IBranchRepository branchRepository;
    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;
    private final MailService mailService;
    private final IAddressMapper addressMapper;
    private final IRefreshTokenService refreshTokenService;

    @Transactional
    @Override
    public SalesRepResponseDto createSalesRep(CreateSalesRepRequestDto requestDto) {

        // şube erişimi kontrolü
        securityService.checkBranchAccess(requestDto.getBranchId());

        // requestDto'dan gönderilen branchid var mı?
        Branch branch = branchRepository.findByIdAndDeletedAtIsNull(requestDto.getBranchId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, requestDto.getBranchId().toString())));

        // bu tc ile aktif bir employee kaydı var mı?
        employeeRepository.findByIdentityNumber(requestDto.getIdentityNumber()).ifPresent(existing -> {
            if (existing.isActive()) {
                throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_IDENTITY_ALREADY_EXISTS, requestDto.getIdentityNumber()));
            }
            // yeniden işe alım gerek
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_INACTIVE_RECORD_EXISTS, existing.getId().toString()));
        });

        // mail kontrolü yapılır?
        if (employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, requestDto.getEmail()));
        }

        SalesRep salesRep = salesRepMapper.toSalesRepEntity(requestDto);
        salesRep.setBranch(branch);

        String username = usernameGenerator.generateUnique(
                requestDto.getName(),
                requestDto.getSurname(),
                Role.SALES_REP,
                branch.getId());

        String temporaryPassword = UUID.randomUUID().toString();


        salesRep.assignTemporaryPassword(passwordEncoder.encode(temporaryPassword));
        salesRep.setUsername(username);
        salesRep.setRole(Role.SALES_REP);

        SalesRep savedSalesRep = salesRepRepository.saveAndFlush(salesRep);
        log.info("Yeni bir satış danışmanı oluşturuldu ve şubeye atandı. id: {}, şube: {}", savedSalesRep.getId(), savedSalesRep.getBranch().getBranchName());
        mailService.sendTemporaryPassword(savedSalesRep.getEmail(), username, temporaryPassword);
        return salesRepMapper.toSalesRepResponse(savedSalesRep);
    }


    @Transactional
    @Override
    public SalesRepResponseDto findSalesRepById(Long id) {

        SalesRep salesRep = getSalesRepEntityById(id);

        securityService.checkBranchAccess(salesRep.getBranch() != null ? salesRep.getBranch().getId() : null);

        BaseEmployee currentUser = securityService.getCurrentEmployee();
        if (currentUser.getRole() == Role.SALES_REP
                && !salesRep.getId().equals(currentUser.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece kendi bilgilerinizi görüntüleyebilirsiniz."));
        }

        return salesRepMapper.toSalesRepResponse(salesRep);
    }

    @Transactional
    @Override
    public List<SalesRepResponseDto> findAllSalesReps() {

        List<SalesRep> salesReps = securityService.isSuperAdmin()
                ? salesRepRepository.findAllByActiveTrue()
                : salesRepRepository.findAllByBranchIdAndActiveTrue(securityService.getCurrentBranchId());

        return salesReps.stream()
                .map(salesRepMapper::toSalesRepResponse)
                .toList();
    }

    @Override
    public SalesRep getSalesRepEntityById(Long id) {
        return salesRepRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString())));
    }

    @Transactional
    @Override
    public SalesRepResponseDto updateSalesRep(UpdateSalesRepRequestDto requestDto, Long id) {

        // gelen id ile ilgili kayıt var mı?
        SalesRep existingSalesRep = getSalesRepEntityById(id);

        // pasif personel güncellenmez
        if (!existingSalesRep.isActive()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_ACTIVE, id.toString()));
        }

        // hem mevcut şube hem hedef şube erişim alanında olmalı
        securityService.checkBranchAccess(existingSalesRep.getBranch() != null ? existingSalesRep.getBranch().getId() : null);
        securityService.checkBranchAccess(requestDto.getBranchId());

        // e-posta değiştiyse başka bir personelde kullanılıyor mu?
        if (!requestDto.getEmail().equals(existingSalesRep.getEmail())
                && employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, requestDto.getEmail()));
        }

        // şube değiştiyse yeni şubeyi DB'den çek
        if (existingSalesRep.getBranch() == null || !existingSalesRep.getBranch().getId().equals(requestDto.getBranchId())) {
            Branch newBranch = branchRepository.findByIdAndDeletedAtIsNull(requestDto.getBranchId())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, requestDto.getBranchId().toString())));
            existingSalesRep.setBranch(newBranch);
        }

        // mapper üzerine yazmadan önce alınır
        String oldEmail = existingSalesRep.getEmail();

        salesRepMapper.updateSalesRepFromDto(requestDto, existingSalesRep);

        SalesRep updatedSalesRep = salesRepRepository.saveAndFlush(existingSalesRep);
        log.info("Satış temsilcisi güncellendi. id: {}, şube: {}", updatedSalesRep.getId(), updatedSalesRep.getBranch().getBranchName());

        // adres değiştirip geçici şifre yeniden gönderilmesi hesap ele geçirme yolu: iz kalsın
        if (!oldEmail.equals(updatedSalesRep.getEmail())) {
            log.warn("Personelin e-posta adresi değiştirildi. personel id: {}, eski: {}, yeni: {}", id, oldEmail, updatedSalesRep.getEmail());
        }

        return salesRepMapper.toSalesRepResponse(updatedSalesRep);
    }

    @Transactional
    @Override
    public void deleteSalesRep(Long id) {

        SalesRep salesRep = getSalesRepEntityById(id);
        // active değilse dokunma
        if (!salesRep.isActive()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_ALREADY_INACTIVE, id.toString()));
        }

        // şube admini / müdür başka şubenin temsilcisini pasife alamasın
        securityService.checkBranchAccess(salesRep.getBranch() != null ? salesRep.getBranch().getId() : null);

        // active=false + terminationDate
        salesRep.terminate(LocalDate.now());

        // işlemi kimin ne zaman yaptığı bilgisi
        BaseEmployee actor = securityService.getCurrentEmployee();
        salesRep.softDelete(actor);

        salesRepRepository.saveAndFlush(salesRep);

        // açık oturumlar kapatılır; aksi halde hesap geri alınınca eski refresh token'lar yeniden çalışır
        refreshTokenService.revokeAllTokens(salesRep);

        // işlemi yapan kullanıcı log satırına MDC ile otomatik eklenir
        log.info("Satış temsilcisi pasife alındı. id: {}", id);
    }

    @Transactional
    @Override
    public SalesRepResponseDto reactivateSalesRep(ReactivateEmployeeRequestDto request, Long id) {

        SalesRep salesRep = getSalesRepEntityById(id);
        // e-posta aşağıda değişebilir: karşılaştırma için değişmeden önce saklanır
        String oldEmail = salesRep.getEmail();

        // zaten aktifse yeniden işe alım yapılmaz
        if (salesRep.isActive()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_ALREADY_ACTIVE, id.toString()));
        }

        // yalnızca hedef şube kontrol edilir: ayrılmış personel başka şubede işe dönebilir
        securityService.checkBranchAccess(request.getBranchId());

        Branch branch = branchRepository.findByIdAndDeletedAtIsNull(request.getBranchId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, request.getBranchId().toString())));

        // e-posta yalnızca dolu gönderildiyse ve gerçekten değiştiyse kontrol edilir; boş string mevcut e-postayı ezmesin
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(salesRep.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, request.getEmail()));
            }
            salesRep.setEmail(request.getEmail());
        }

        if (request.getAddress() != null) {
            salesRep.setAddress(addressMapper.toEntity(request.getAddress()));
        }

        if (request.getPhoneNumber() != null) {
            salesRep.setPhoneNumber(request.getPhoneNumber());
        }

        // active=true, terminationDate ve silme izi temizlenir
        salesRep.reactivate();
        salesRep.setHireDate(request.getHireDate() != null ? request.getHireDate() : LocalDate.now());
        salesRep.setBranch(branch);
        salesRep.setBaseSalary(request.getBaseSalary());

        // eski aydan kalan satış sayısı sıfırlanır
        salesRep.setMonthlySalesCount(0L);

        // yeni geçici şifre
        String temporaryPassword = UUID.randomUUID().toString();
        salesRep.assignTemporaryPassword(passwordEncoder.encode(temporaryPassword));

        SalesRep reactivatedSalesRep = salesRepRepository.saveAndFlush(salesRep);

        // şifre sıfırlandı: bu değişiklikten önce pasife alınmış hesaplarda kalmış token'lar da kapanır
        refreshTokenService.revokeAllTokens(reactivatedSalesRep);

        // işlemi yapan kullanıcı log satırına MDC ile otomatik eklenir
        log.info("Satış temsilcisi yeniden işe alındı. id: {}, şube: {}", id, branch.getBranchName());

        if (!oldEmail.equals(reactivatedSalesRep.getEmail())) {
            log.warn("Yeniden işe alımda e-posta değiştirildi. personel id: {}, eski: {}, yeni: {}",
                    id, oldEmail, reactivatedSalesRep.getEmail());
        }

        mailService.sendTemporaryPassword(reactivatedSalesRep.getEmail(), reactivatedSalesRep.getUsername(), temporaryPassword);
        return salesRepMapper.toSalesRepResponse(reactivatedSalesRep);
    }

}
