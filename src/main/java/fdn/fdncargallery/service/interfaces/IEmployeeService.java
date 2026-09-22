package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.ResendPasswordResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;

public interface IEmployeeService {

    // employee tablosunda tc no ile arama yapar
    EmployeeSearchResultDto findEmployeeByIdentityNumber(SearchEmployeeRequestDto searchEmployeeRequestDto);

    // geçici şifre e-postası ulaşmadıysa yenisini üretip gönderir
    ResendPasswordResultDto resendTemporaryPassword(SearchEmployeeRequestDto request);
}