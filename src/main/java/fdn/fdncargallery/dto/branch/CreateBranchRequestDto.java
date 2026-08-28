package fdn.fdncargallery.dto.branch;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBranchRequestDto {

    @NotBlank(message = "Şube adı boş bırakılamaz!")
    private String branchName;

    @Valid
    @NotNull(message = "Şube adresi zorunludur!")
    private AddressRequestDto address;
}
