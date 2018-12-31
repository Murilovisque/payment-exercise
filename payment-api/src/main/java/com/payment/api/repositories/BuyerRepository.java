package com.payment.api.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.payment.api.models.Buyer;
import com.payment.api.repositories.search.SearchConditionPreparer;
import com.payment.api.repositories.search.SearchConditions;

public class BuyerRepository extends AbstractRepository {
    
    public static final String TABLE_NAME = "buyer";
    public static final String COLUMN_ID = "id_buyer";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CPF = "cpf";
    public static final String COLUMN_EMAIL = "email";
    private static final String GET_BUYERS = "select id_buyer, name, cpf, email from buyer";
    private static final String GET_WITH_CPF = GET_BUYERS + " where cpf = ?";
    private static final String GET_WITH_ID = GET_BUYERS + " where id = ?";
    private static final String INSERT_BUYER = "insert into buyer (id_buyer, name, email, cpf) values (?, ?, ?, ?)";
    private static final Set<SearchConditionPreparer> SEARCH_ARGS_HANDLER;

    static {
        SEARCH_ARGS_HANDLER = new HashSet<>();
        SEARCH_ARGS_HANDLER.add(SearchConditionPreparer.likeIgnoreCase(() -> COLUMN_NAME));
        SEARCH_ARGS_HANDLER.add(SearchConditionPreparer.likeIgnoreCase(() -> COLUMN_EMAIL));
        SEARCH_ARGS_HANDLER.add(SearchConditionPreparer.likeIgnoreCase(() -> COLUMN_CPF));
    }

    public BuyerRepository(ConnectionWrapper conn) {
        super(conn);
    }

    public Optional<Buyer> getWithCPF(String cpf) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(GET_WITH_CPF)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Buyer(rs.getObject(COLUMN_ID, UUID.class), rs.getString(COLUMN_NAME),
                        rs.getString(COLUMN_CPF), rs.getString(COLUMN_EMAIL)));
                }                
            }            
        }
        return Optional.empty();
    }

    public Optional<Buyer> getWithId(UUID id) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(GET_WITH_ID)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getBuyerFromResultSet(rs));
                }                
            }            
        }
        return Optional.empty();
    }

    public List<Buyer> search(SearchConditions searchConditions, int limit) throws SQLException {
        try (ResultSet rs = executeQuerySearch(getConnection(), GET_BUYERS, limit, searchConditions, SEARCH_ARGS_HANDLER)) {
            List<Buyer> buyers = new ArrayList<>();
            while(rs.next())
                buyers.add(getBuyerFromResultSet(rs));
            return buyers;
        }
    }

    public List<Buyer> getBuyers(int limit) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(String.format("%s LIMIT %d", GET_BUYERS, limit));
                ResultSet rs = stmt.executeQuery()) {
            List<Buyer> buyers = new ArrayList<>();
            while(rs.next())
                buyers.add(getBuyerFromResultSet(rs));
            return buyers;
        }
    }

	public UUID insert(Buyer buyer) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(INSERT_BUYER);
        UUID id = UUID.randomUUID();
        stmt.setObject(1, id);
        stmt.setString(2, buyer.getName());
        stmt.setString(3, buyer.getEmail());
        stmt.setString(4, buyer.getCpf());
        stmt.addBatch();
        addBatchStatement(stmt);
        return id;
    }
    
    public static Buyer getBuyerFromResultSet(ResultSet rs) throws SQLException {
        return new Buyer(rs.getObject(COLUMN_ID, UUID.class), rs.getString(COLUMN_NAME),
            rs.getString(COLUMN_CPF), rs.getString(COLUMN_EMAIL));
    }
}