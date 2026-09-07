package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.enums.CarStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = IBaseMapperConfig.class, uses = {IAddressMapper.class}, imports = CarStatus.class)
public interface IBranchMapper {

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Branch toEntity(CreateBranchRequestDto request);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerFullName", expression = "java(branch.getManager() != null ? branch.getManager().getName() + ' ' + branch.getManager().getSurname() : null)")
    // Satılan araç stokta değildir; bakım, ekspertiz, rezerve ve transfer stokta sayılır.
    @Mapping(target = "totalCars", expression = "java(branch.getStockItems() != null ? (int) branch.getStockItems().stream().filter(item -> item.getStatus() != CarStatus.SOLD).count() : 0)")
    // Ayrılmış personel sayılmaz: listeleme uçları da yalnızca aktifleri döndürüyor.
    @Mapping(target = "totalEmployees", expression = "java(branch.getEmployees() != null ? (int) branch.getEmployees().stream().filter(emp -> emp.isActive()).count() : 0)")
    BranchResponseDto toResponse(Branch branch);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateBranchFromDto(UpdateBranchRequestDto request, @MappingTarget Branch branch);
}
