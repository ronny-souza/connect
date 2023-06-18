package br.com.connect.model;

import br.com.connect.model.transport.address.AddressDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String zipCode;

    private String complement;

    public Address(AddressDTO addressDTO) {
        this.street = addressDTO.street();
        this.number = addressDTO.number();
        this.district = addressDTO.district();
        this.city = addressDTO.city();
        this.country = addressDTO.country();
        this.zipCode = addressDTO.zipCode();
    }
}
