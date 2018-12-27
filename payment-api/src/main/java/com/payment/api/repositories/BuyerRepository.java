package com.payment.api.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.models.Buyer;

public class BuyerRepository extends AbstractRepository {
    
    public static final String TABLE_NAME = "buyer";
    protected static final String COLUMN_ID = "id_buyer";
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_CPF = "cpf";
    protected static final String COLUMN_EMAIL = "email";
    protected static final String GET_BY_CPF = "select id_buyer, name, cpf, email from buyer where cpf = ?";
    protected static final String INSERT_BUYER = "insert into buyer (name, email, cpf) values (?, ?, ?)";

    public BuyerRepository(ConnectionWrapper conn) {
        super(conn);
    }

    public Optional<Buyer> getByCPF(String cpf) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(GET_BY_CPF)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Buyer(rs.getLong(COLUMN_ID), rs.getString(COLUMN_NAME),
                        rs.getString(COLUMN_CPF), rs.getString(COLUMN_EMAIL)));
                }                
            }            
        }
        return Optional.empty();
    }

	public void insert(Buyer buyer) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(INSERT_BUYER);
        stmt.setString(1, buyer.getName());
        stmt.setString(2, buyer.getEmail());
        stmt.setString(3, buyer.getCpf());
        stmt.addBatch();
        addBatchStatement(stmt);
	}
}