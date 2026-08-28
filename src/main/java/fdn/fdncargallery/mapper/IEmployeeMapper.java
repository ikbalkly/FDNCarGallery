package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import fdn.fdncargallery.entity.BaseEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IAddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IEmployeeMapper {

    // Sadece veritabanından çekilen genel personelleri (BaseEmployee), ortak formata çeviriyoruz
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    @Mapping(target = "username", source = "userAccount.username")
    @Mapping(target = "email", source = "userAccount.email")
    @Mapping(target = "role", source = "userAccount.role")
    @Mapping(target = "firstLogin", source = "userAccount.firstLogin")
    EmployeeResponseDto toResponse(BaseEmployee employee);

}
