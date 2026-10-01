package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.ResendPasswordResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;

public interface IEmployeeService {

    // employee tablosunda tc no ile arama yapar
    EmployeeSearchResultDto findEmployeeByIdentityNumber(SearchEmployeeRequestDto searchEmployeeRequestDto);

    // geçici şifre e-postası ulaşmadıysa yenisini üretip gönderir
    ResendPasswordResultDto resendTemporaryPassword(SearchEmployeeRequestDto request);

    // şifreye dokunmadan personelin tüm oturumlarını kapatır
    void revokeSessions(SearchEmployeeRequestDto request);

    // diğer servisler personeli rolden bağımsız olarak buradan alır
    BaseEmployee getEmployeeEntityById(Long id);
}