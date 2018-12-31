package com.payment.api.repositories;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.payment.api.models.BoletoPayment;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;

public class PaymentRepository extends AbstractRepository {

	public static final String TABLE_NAME = "payment";
	public static final String COLUMN_ID = "id_payment";
	public static final String COLUMN_AMOUNT = "amount";	
	public static final String COLUMN_STATUS = "status";
	private static final String GET_BY_ID = "select p.id_payment, p.amount, p.status, p.id_form_of_payment, fp.type, fp.data from payment p"
		+ " inner join form_of_payment fp on fp.id_form_of_payment = p.id_form_of_payment where p.id_payment = ?";
	private static final String INSERT_PAYMENT = "insert into payment(id_payment, amount, status, id_form_of_payment, id_buyer, id_api_client)"
		+ "values (?, ?, ?::PAYMENT_STATUS, ?, ?, ?)";
	private static final String GET_BY_BUYER_ID = "select p.id_payment, p.amount, p.status, p.id_form_of_payment, fp.type, fp.data from payment p"
		+ " inner join form_of_payment fp on fp.id_form_of_payment = p.id_form_of_payment where p.id_buyer = ?";
	
	public PaymentRepository(ConnectionWrapper connectionWrapper) {
        super(connectionWrapper);
    }

	public Optional<Payment> getPaymentWithId(UUID id) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(GET_BY_ID)) {
			stmt.setObject(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {		
					return Optional.of(getPaymentFromResultSet(rs));
				}
			}
		}
		return Optional.empty();
	}

	public UUID insert(Long apiClientId, Payment payment, UUID formOfPaymentId, UUID buyerId) throws SQLException {
		UUID id = UUID.randomUUID();
		PreparedStatement stmt = getConnection().prepareStatement(INSERT_PAYMENT);
		stmt.setObject(1, id);
		stmt.setBigDecimal(2, payment.getAmount());
		stmt.setString(3, payment.getStatus().name());
		stmt.setObject(4, formOfPaymentId);
		stmt.setObject(5, buyerId);
		stmt.setLong(6, apiClientId);		
		stmt.addBatch();
		addBatchStatement(stmt);
		return id;
	}

	public List<Payment> getPaymentWithBuyerId(UUID buyerId) throws SQLException {
		try (PreparedStatement stmt = getConnection().prepareStatement(GET_BY_BUYER_ID)) {
			stmt.setObject(1, buyerId);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Payment> pays = new ArrayList<>();
				while(rs.next())
					pays.add(getPaymentFromResultSet(rs));
				return pays;
			}
		}
	}
	
	private Payment getPaymentFromResultSet(ResultSet rs) throws SQLException {
		Payment.Type type = Payment.Type.get(rs.getString(FormOfPaymentRepository.COLUMN_TYPE));
		if (Payment.Type.CREDIT_CARD.equals(type))
			return new CreditCardPayment(rs.getObject(COLUMN_ID, UUID.class),
				BigDecimal.valueOf(rs.getDouble(COLUMN_AMOUNT)),
				Payment.Status.get(rs.getString(COLUMN_STATUS)),
				FormOfPaymentRepository.getCardFromResultSet(rs));
		else			
			return new BoletoPayment(rs.getObject(COLUMN_ID, UUID.class),
				BigDecimal.valueOf(rs.getDouble(COLUMN_AMOUNT)),
				Payment.Status.get(rs.getString(COLUMN_STATUS)),
				FormOfPaymentRepository.getBoletoFromResultSet(rs));
	}
}