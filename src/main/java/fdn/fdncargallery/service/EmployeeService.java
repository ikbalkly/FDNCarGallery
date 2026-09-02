package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IEmployeeMapper;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.service.interfaces.IEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeMapper employeeMapper;

    @Transactional
    @Override
    public EmployeeSearchResultDto findEmployeeByIdentityNumber(SearchEmployeeRequestDto searchEmployeeRequestDto) {
        BaseEmployee employee = employeeRepository.findByIdentityNumber(searchEmployeeRequestDto.getIdentityNumber())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_FOUND, "Bu TC ile kayıtlı personel yok")));

        return employeeMapper.toSearchResult(employee);
    }
}
