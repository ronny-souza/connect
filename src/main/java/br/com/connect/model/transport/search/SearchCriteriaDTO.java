package br.com.connect.model.transport.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDTO {

    private String key;
    private String operation;
    private String value;
    private SearchLogicalOperationEnum logicalOperation;

    public SearchCriteriaDTO(String key, String operation, String value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.logicalOperation = SearchLogicalOperationEnum.AND;
    }
}
