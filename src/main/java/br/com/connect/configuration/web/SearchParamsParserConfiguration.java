package br.com.connect.configuration.web;

import br.com.connect.model.transport.search.ListSearchCriteriaDTO;
import br.com.connect.model.transport.search.SearchCriteriaDTO;
import br.com.connect.model.transport.search.SearchLogicalOperationEnum;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class SearchParamsParserConfiguration implements Converter<String, ListSearchCriteriaDTO> {

    private final Pattern searchParamsPattern = Pattern.compile("([\\w+((\\.|\\_|\\-)\\w)?]+)(\\:|\\<|\\>|\\!=|\\=)([\\w+((\\.|\\_| |\\-|\\&|\\@|\\#\\$\\%\\&|\\p{L})\\w)?]+)(\\;|\\,)?");

    @Override
    public ListSearchCriteriaDTO convert(@NonNull String searchParamsAsString) {
        List<SearchCriteriaDTO> searchParams = this.parseSearchParams(searchParamsAsString);
        return new ListSearchCriteriaDTO(searchParams);
    }

    private List<SearchCriteriaDTO> parseSearchParams(String searchParamsAsString) {
        Matcher matcher = searchParamsPattern.matcher(searchParamsAsString);

        List<SearchCriteriaDTO> searchParams = new ArrayList<>();
        while (matcher.find()) {
            this.buildSearchParamAsObject(matcher, searchParams);
        }
        return searchParams;
    }

    private void buildSearchParamAsObject(Matcher matcher, List<SearchCriteriaDTO> searchParams) {
        String key = matcher.group(1);
        String operation = matcher.group(2);
        String value = matcher.group(3);
        String logicalOperationAsString = matcher.group(4);

        if (StringUtils.hasText(logicalOperationAsString)) {
            SearchLogicalOperationEnum logicalOperation = SearchLogicalOperationEnum.findByOperation(logicalOperationAsString);
            searchParams.add(new SearchCriteriaDTO(key, operation, value, logicalOperation));
        } else {
            searchParams.add(new SearchCriteriaDTO(key, operation, value));
        }
    }
}
