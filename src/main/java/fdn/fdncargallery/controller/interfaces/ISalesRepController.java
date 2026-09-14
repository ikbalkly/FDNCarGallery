package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.dto.salesRep.UpdateSalesRepRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISalesRepController {

    public ResponseEntity<SalesRepResponseDto> createSalesRep(CreateSalesRepRequestDto requestDto);

    ResponseEntity<SalesRepResponseDto> findSalesRepById(Long id);

    ResponseEntity<List<SalesRepResponseDto>> findAllSalesReps();

    ResponseEntity<SalesRepResponseDto> updateSalesRep(UpdateSalesRepRequestDto requestDto, Long id);

    ResponseEntity<Void> deleteSalesRep(Long id);

    ResponseEntity<SalesRepResponseDto> reactivateSalesRep(ReactivateEmployeeRequestDto request, Long id);
}
