package br.com.connect.specification;

import br.com.connect.model.Condominium;
import br.com.connect.model.transport.search.ListSearchCriteriaDTO;
import br.com.connect.model.transport.search.SearchCriteriaDTO;
import br.com.connect.model.transport.search.SearchLogicalOperationEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CondominiumSpecification {

    private final Map<String, Function<SearchCriteriaDTO, Specification<Condominium>>> specifications = new HashMap<>();
    private List<SearchCriteriaDTO> params = new ArrayList<>();
    private Specification<Condominium> specification;

    public CondominiumSpecification(Specification<Condominium> specification) {
        this.specification = specification;
        this.addSpecification("name", param -> CondominiumSpecification.name(param.getValue()));
        this.addSpecification("connectIdentifier", param -> CondominiumSpecification.connectIdentifier(param.getValue()));
        this.addSpecification("street", param -> CondominiumSpecification.street(param.getValue()));
    }

    public CondominiumSpecification withSearchParams(ListSearchCriteriaDTO params) {
        if (params != null && !params.getSearchItems().isEmpty()) {
            this.params = params.getSearchItems();
        }
        return this;
    }

    public Specification<Condominium> build() {
        SearchLogicalOperationEnum logicalOperation = SearchLogicalOperationEnum.AND;
        Specification<Condominium> searchParamsSpecification = null;
        for (SearchCriteriaDTO param : this.params) {
            Specification<Condominium> specificationSearch = this.getSpecification(param);
            if (specificationSearch != null) {
                if (searchParamsSpecification == null) {
                    searchParamsSpecification = specificationSearch;
                    logicalOperation = param.getLogicalOperation();
                    continue;
                }

                if (logicalOperation == SearchLogicalOperationEnum.AND) {
                    searchParamsSpecification = searchParamsSpecification.and(specificationSearch);
                } else if (logicalOperation == SearchLogicalOperationEnum.OR) {
                    searchParamsSpecification = searchParamsSpecification.or(specificationSearch);
                }

                logicalOperation = param.getLogicalOperation();
            }
        }

        if (searchParamsSpecification != null) {
            this.specification = this.specification.and(searchParamsSpecification);
        }

        return Specification.where(this.specification);
    }

    public static Specification<Condominium> name(String name) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Condominium> connectIdentifier(String connectIdentifier) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("connectIdentifier"), connectIdentifier);
    }

    public static Specification<Condominium> street(String street) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Object, Object> address = root.join("address", JoinType.LEFT);
            return criteriaBuilder.like(address.get("street"), "%" + street + "%");
        };
    }

    public static Specification<Condominium> availableForUser(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Object, Object> tenants = root.join("tenants", JoinType.LEFT);
            Predicate userIsNull = criteriaBuilder.isNull(tenants.get("user"));
            Predicate tenantEmailIsEquals = criteriaBuilder.equal(tenants.get("email"), email);
            return criteriaBuilder.and(userIsNull, tenantEmailIsEquals);
        };
    }

    private void addSpecification(String key, Function<SearchCriteriaDTO, Specification<Condominium>> value) {
        this.specifications.put(key, value);
    }

    private Specification<Condominium> getSpecification(SearchCriteriaDTO param) {
        return this.specifications.getOrDefault(param.getKey(), defaultParameter -> null).apply(param);
    }
}
