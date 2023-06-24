package br.com.connect.model.transport.condominium.tenant;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateTenantDTO {

    @CsvBindByPosition(position = 0)
    private String name;
    @CsvBindByPosition(position = 1)
    private String email;
    @CsvBindByPosition(position = 2)
    private Integer apartment;
    @CsvBindByPosition(position = 3)
    private String complement;
    @CsvBindByPosition(position = 4)
    private String contact;
}
