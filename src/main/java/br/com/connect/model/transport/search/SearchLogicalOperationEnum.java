package br.com.connect.model.transport.search;

public enum SearchLogicalOperationEnum {
    AND(","), OR(";");

    private final String operation;

    SearchLogicalOperationEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public static SearchLogicalOperationEnum findByOperation(String operation) {
        for (SearchLogicalOperationEnum operationAsEnum : SearchLogicalOperationEnum.values()) {
            if (operationAsEnum.getOperation().equals(operation)) {
                return operationAsEnum;
            }
        }
        return SearchLogicalOperationEnum.AND;
    }

}
