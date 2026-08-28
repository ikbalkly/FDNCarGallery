package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = "spring", uses = {IAddressMapper.class})
public interface IBranchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Branch toEntity(CreateBranchRequestDto request);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerFullName", expression = "java(branch.getManager() != null ? branch.getManager().getName() + ' ' + branch.getManager().getSurname() : null)")
    @Mapping(target = "totalCars", expression = "java(branch.getStockItems() != null ? branch.getStockItems().size() : 0)")
    @Mapping(target = "totalEmployees", expression = "java(branch.getEmployees() != null ? branch.getEmployees().size() : 0)")
    BranchResponseDto toResponse(Branch branch);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateBranchFromDto(UpdateBranchRequestDto request, @MappingTarget Branch branch);
}
