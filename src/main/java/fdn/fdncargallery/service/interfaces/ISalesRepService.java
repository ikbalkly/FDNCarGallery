package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.dto.salesRep.UpdateSalesRepRequestDto;
import fdn.fdncargallery.entity.SalesRep;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ISalesRepService {
    SalesRepResponseDto createSalesRep(CreateSalesRepRequestDto requestDto);
    SalesRepResponseDto findSalesRepById(Long id);
    List<SalesRepResponseDto> findAllSalesReps();
    SalesRep getSalesRepEntityById(Long id);
    SalesRepResponseDto updateSalesRep(UpdateSalesRepRequestDto requestDto,Long id);
    void deleteSalesRep(Long id);
    SalesRepResponseDto reactivateSalesRep(ReactivateEmployeeRequestDto request, Long id);
}
