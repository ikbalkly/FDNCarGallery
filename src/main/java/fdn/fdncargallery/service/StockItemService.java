package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.stockItem.CreateStockItemRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.dto.stockItem.UpdateStockItemRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.entity.Vehicle;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IStockItemMapper;
import fdn.fdncargallery.mapper.IVehicleMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IStockItemRepository;
import fdn.fdncargallery.repository.IVehicleRepository;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockItemService implements IStockItemService {

    private final IStockItemRepository stockItemRepository;
    private final IVehicleRepository vehicleRepository;
    private final IBranchRepository branchRepository;
    private final IStockItemMapper stockItemMapper;
    private final IVehicleMapper vehicleMapper;
    private final SecurityService securityService;

    /*
     * Stok girişi akışı:
     * 1) şubeye erişim yetkisi var mı
     * 2) şube gerçekten var mı (requesten sadece id geliyor)
     * 3) VIN'i normalize et -> arama büyük/küçük harfe takılmasın
     * 4) plaka ile AÇIK bir kayıt var mı
     * 5) araç daha önce galeriden geçmiş mi -> Vehicle'ı yeniden kullan,
     *    yoksa yeni Vehicle aç; geçmişse hâlâ stokta mı diye kontrol et
     * 6) StockItem'ı kur, sunucu tarafı alanları (status / acquiredAt) burada yaz
     * 7) saveAndFlush -> timestamp'ler response'a dolu dönsün
     */

    @Transactional
    @Override
    public StockItemResponseDto createStockItem(CreateStockItemRequestDto createStockItemRequestDto) {

        // branchId opsiyonel: şube personeli göndermezse kendi şubesi kullanılır.
        Long targetBranchId = resolveTargetBranchId(createStockItemRequestDto.getBranchId());

        // Şube admini ve müdür yalnızca kendi şubesine araç girişi yapabilir.
        securityService.checkBranchAccess(targetBranchId);

        Branch branch = branchRepository.findById(targetBranchId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND,
                        targetBranchId.toString())));

        // Locale.ROOT şart: Türkçe locale'de "i".toUpperCase() "İ" üretir ve aynı
        // VIN iki farklı metne dönüşürdü. UsernameGeneratorUtils ile aynı gerekçe.
        String vin = createStockItemRequestDto.getVehicle().getVin().trim().toUpperCase(Locale.ROOT);

        // yeni Vehicle kaydetmeden
        // plakayı kontrol edersek, hata durumunda boşuna insert yapılmamış olur.
        // Plaka entity'de bilerek unique DEĞİL (aynı plaka farklı dönemlerde
        // farklı kalemlerde görünebilir), bu yüzden tekilliği burada koruyoruz.
        // SOLD dışındaki her durum "hâlâ bizde" demek.


        //Veritabanında bu plakaya (örn: 34ABC123) sahip ve durumu SATILDI (SOLD) OLMAYAN herhangi bir kayıt var mı?
        if (stockItemRepository.existsByPlateNumberAndStatusNot(
                createStockItemRequestDto.getPlateNumber(), CarStatus.SOLD)) {
            throw new BaseException(new ErrorMessage(MessageType.PLATE_ALREADY_IN_STOCK,
                    createStockItemRequestDto.getPlateNumber()));
        }

        Vehicle vehicle = resolveVehicle(vin, createStockItemRequestDto);

        StockItem stockItem = stockItemMapper.toEntity(createStockItemRequestDto);
        stockItem.setVehicle(vehicle);
        stockItem.setBranch(branch);

        stockItem.setStatus(CarStatus.AVAILABLE);
        stockItem.setAcquiredAt(LocalDate.now());
        stockItem.setSoldAt(null);

        // Girişi yapan personel de token'dan gelir: istemci başkasının adına
        // kayıt açamasın diye DTO'da böyle bir alan yok.
        BaseEmployee currentEmployee = securityService.getCurrentEmployee();
        stockItem.setEmployee(currentEmployee);

        StockItem savedStockItem = stockItemRepository.saveAndFlush(stockItem);

        log.info("Stoğa araç girişi yapıldı. stockItemId: {}, vin: {}, plaka: {}, şube: {}, personelId: {}",
                savedStockItem.getId(), vin, savedStockItem.getPlateNumber(), branch.getBranchName(), currentEmployee.getId());

        return stockItemMapper.toResponse(savedStockItem);
    }

    @Transactional
    @Override
    public StockItemResponseDto updateStockItem(UpdateStockItemRequestDto updateStockItemRequestDto, Long id) {

        StockItem existingStockItem = getStockItemEntityById(id);

        // branchId opsiyonel. Create'ten FARKLI olarak varsayılan "kendi şubem"
        // değil, "aracın bulunduğu şube": burada boş bırakmak "şubeyi değiştirme"
        // demektir. SUPER_ADMIN'de "kendi şubem" varsayımı aracı sessizce
        // IT Merkez'e taşırdı; bu varsayılan herkes için doğru çalışıyor.
        Long targetBranchId = updateStockItemRequestDto.getBranchId() != null
                ? updateStockItemRequestDto.getBranchId()
                : existingStockItem.getBranch().getId();

        // Hem aracın MEVCUT şubesi hem TAŞINACAĞI şube erişim alanında olmalı;
        // aksi halde şube admini kendi aracını başka şubeye kaçırabilirdi.
        // updateManager ile aynı çift kapı.
        securityService.checkBranchAccess(existingStockItem.getBranch().getId());
        securityService.checkBranchAccess(targetBranchId);

        // Satılmış kalem tarihsel bir kayıttır: fiyatı ya da kilometresi sonradan
        // değiştirilirse satış raporları ve müşteriye verilen belge tutarsızlaşır.
        if (existingStockItem.getStatus() == CarStatus.SOLD) {
            throw new BaseException(new ErrorMessage(MessageType.SOLD_STOCK_ITEM_CANNOT_BE_MODIFIED, id.toString()));
        }

        // Plaka DEĞİŞTİYSE aynı plakayla açık başka bir kayıt olmamalı. Değişmediyse
        // sorgu hiç çalışmaz; yoksa kaydın kendi plakası kendine takılırdı.
        if (!existingStockItem.getPlateNumber().equals(updateStockItemRequestDto.getPlateNumber())
                && stockItemRepository.existsByPlateNumberAndStatusNot(
                        updateStockItemRequestDto.getPlateNumber(), CarStatus.SOLD)) {
            throw new BaseException(new ErrorMessage(MessageType.PLATE_ALREADY_IN_STOCK,
                    updateStockItemRequestDto.getPlateNumber()));
        }

        // Şube değişiyorsa hedef şube gerçekten var mı? Mapper 'branch' alanını
        // ignore ettiği için ilişkiyi burada elle kuruyoruz. branchId boş
        // geldiyse targetBranchId mevcut şubeye eşittir ve bu blok hiç çalışmaz.
        if (!existingStockItem.getBranch().getId().equals(targetBranchId)) {
            Branch newBranch = branchRepository.findById(targetBranchId)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND,
                            targetBranchId.toString())));

            log.info("Araç şube değiştiriyor. stockItemId: {}, eski şube: {}, yeni şube: {}",
                    id, existingStockItem.getBranch().getId(), newBranch.getId());

            existingStockItem.setBranch(newBranch);
        }

        // Araç kimliği (vehicle), durum, tarihler ve girişi yapan personel
        // DTO'da yok: hiçbiri bu uçtan değiştirilemez.
        stockItemMapper.updateStockItemFromDto(updateStockItemRequestDto, existingStockItem);

        StockItem updatedStockItem = stockItemRepository.save(existingStockItem);
        return stockItemMapper.toResponse(updatedStockItem);
    }

    @Transactional
    @Override
    public StockItemResponseDto findStockItemById(Long id) {

        StockItem stockItem = getStockItemEntityById(id);

        // Şube admini ve müdür başka şubenin aracını göremez.
        securityService.checkBranchAccess(stockItem.getBranch().getId());

        return stockItemMapper.toResponse(stockItem);
    }

    @Transactional
    @Override
    public List<StockItemResponseDto> findAllStockItems() {

        // SUPER_ADMIN tüm stoğu; BRANCH_ADMIN ve MANAGER yalnızca kendi şubesininkini görür.
        List<StockItem> stockItems = securityService.isSuperAdmin()
                ? stockItemRepository.findAll()
                : stockItemRepository.findAllByBranchId(securityService.getCurrentBranchId());

        return stockItems.stream()
                .map(stockItemMapper::toResponse)
                .toList();
    }
    @Transactional
    @Override
    public void deleteStockItem(Long id) {

        StockItem stockItem = getStockItemEntityById(id);

        // Şube admini ve müdür başka şubenin aracını silemesin.
        securityService.checkBranchAccess(stockItem.getBranch().getId());

        if (stockItem.getStatus() == CarStatus.SOLD) {
            throw new BaseException(new ErrorMessage(MessageType.SOLD_STOCK_ITEM_CANNOT_BE_MODIFIED, id.toString()));
        }

        if (stockItem.getStatus() != CarStatus.AVAILABLE) {
            throw new BaseException(new ErrorMessage(MessageType.DATA_INTEGRITY_VIOLATION,
                    "Yalnızca satışta (AVAILABLE) olan bir stok kalemi silinebilir. Mevcut durum: " + stockItem.getStatus()));
        }

        stockItemRepository.delete(stockItem);

        log.info("Stok kalemi silindi. stockItemId: {}, plaka: {}", id, stockItem.getPlateNumber());
    }

    @Override
    public StockItem getStockItemEntityById(Long id) {
        return stockItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_NOT_FOUND, id.toString())));
    }

    private Long resolveTargetBranchId(Long requestedBranchId) {

        if (requestedBranchId != null) {
            return requestedBranchId;
        }

        if (securityService.isSuperAdmin()) {
            throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR,
                    "Sistem yöneticisi aracın ekleneceği şubeyi (branchId) belirtmelidir."));
        }

        return securityService.getCurrentBranchId();
    }

    private Vehicle resolveVehicle(String vin, CreateStockItemRequestDto request) {

        return vehicleRepository.findByVin(vin)
                .map(existingVehicle -> {
                    if (stockItemRepository.existsByVehicleIdAndStatusNot(existingVehicle.getId(), CarStatus.SOLD)) {
                        throw new BaseException(new ErrorMessage(MessageType.VEHICLE_ALREADY_IN_STOCK, vin));
                    }
                    log.info("Mevcut araç yeniden stoğa alınıyor. vin: {}, vehicleId: {}", vin, existingVehicle.getId());
                    return existingVehicle;
                })
                .orElseGet(() -> {
                    Vehicle newVehicle = vehicleMapper.toEntity(request.getVehicle());
                    newVehicle.setVin(vin);
                    return vehicleRepository.save(newVehicle);
                });
    }
}
