package com.payment.api.repositories.search;

@FunctionalInterface
public interface SearchConditionPreparer {

    String getColumnName();
    default Object prepareValue(Object val) {
        return val;
    } 

    default String prepareColumn(String column) {
        return column;
    }

    default String prepareComparatorCondition() {
        return " = ";
    }

    public static LikeIgnoreCaseCondition likeIgnoreCase(LikeIgnoreCaseCondition stringCondition) {
        return stringCondition;
    }

    public static interface LikeIgnoreCaseCondition extends SearchConditionPreparer {
        @Override
        default Object prepareValue(Object val) {
            return ((String) val ).toUpperCase() + "%";
        }

        @Override
        default String prepareColumn(String column) {
            return " UPPER( " + column + ") ";
        }

        default String prepareComparatorCondition() {
            return " LIKE ";
        }
    }
    
}