package br.com.connect.model.transport.condominium;

import br.com.connect.model.Condominium;
import br.com.connect.model.transport.address.AddressDTO;

public record CondominiumDTO(String connectIdentifier, String name, String phone, String email, AddressDTO address) {

    public CondominiumDTO(Condominium condominium) {
        this(condominium.getConnectIdentifier(), condominium.getName(), condominium.getPhone(), condominium.getEmail(), new AddressDTO(condominium.getAddress()));
    }
}
