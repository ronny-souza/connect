package br.com.connect.model.transport.address;

import br.com.connect.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDTO(@NotBlank String street, @NotNull Integer number, @NotBlank String district,
                         @NotBlank String city, @NotBlank String country, @NotBlank String zipCode, String complement) {

    public AddressDTO(Address address) {
        this(address.getStreet(), address.getNumber(), address.getDistrict(), address.getCity(), address.getCountry(), address.getZipCode(), address.getComplement());
    }
}
