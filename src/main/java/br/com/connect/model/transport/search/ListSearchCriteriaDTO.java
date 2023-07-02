package br.com.connect.model.transport.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListSearchCriteriaDTO {

    private List<SearchCriteriaDTO> searchItems = new ArrayList<>();
}
