package com.payment.api.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.payment.api.repositories.search.SearchConditionPreparer;
import com.payment.api.repositories.search.SearchConditions;

abstract class AbstractRepository {

    private ConnectionWrapper connectionWrapper;

    public AbstractRepository(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }


    protected Connection getConnection() throws SQLException {
        return connectionWrapper.getConnection();
    }

    protected void addBatchStatement(Statement stmt) {
        connectionWrapper.addBatchStatement(stmt);
    }

    protected ResultSet executeQuerySearch(Connection conn, String query, int limit, SearchConditions searchArgs, Set<SearchConditionPreparer> searchArgsHandlers) throws SQLException {
        StringBuilder searchQueryBuilder = new StringBuilder(query);
        String whereOrAnd = query.toUpperCase().contains(" WHERE ") ? " AND " : " WHERE ";
        boolean firstCondition = true;
        ArrayList<Object> values = new ArrayList<>();
        for (SearchConditionPreparer s : searchArgsHandlers) {
            String columnName = s.getColumnName();
            Object val = searchArgs.getSearchArgs().get(columnName);
            if (val != null) {
                searchQueryBuilder.append(whereOrAnd).append(s.prepareColumn(columnName))
                    .append(s.prepareComparatorCondition()).append(" ? ");
                values.add(s.prepareValue(val));
                if (firstCondition) {                    
                    firstCondition = false;
                    whereOrAnd = " AND ";
                }
            }
        }
        PreparedStatement stmt = conn.prepareStatement(searchQueryBuilder.append(" LIMIT ").append(limit).toString());
        for (int i = 0; i < values.size(); i++)
            stmt.setObject(i + 1, values.get(i));
        return stmt.executeQuery();
    }
}