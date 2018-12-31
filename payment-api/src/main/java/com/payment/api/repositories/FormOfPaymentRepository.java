package com.payment.api.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.payment.api.models.Boleto;
import com.payment.api.models.Card;
import com.payment.api.models.Payment;
import java.time.LocalDate;

public class FormOfPaymentRepository extends AbstractRepository {

	public static final String TABLE_NAME = "form_of_payment";
	public static final String COLUMN_ID = "id_form_of_payment";
	public static final String COLUMN_DATA = "data";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_HOLDER_NAME = "holderName";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_EXPIRATION_DATE = "expirationDate";
	public static final String COLUMN_CVV = "cvv";
	private static final String GET_BY_NUMBER = "select id_form_of_payment, type, data from form_of_payment where type = ?::PAYMENT_TYPE AND data ->> 'number' = ?";
	private static final String INSERT_FORM_OF_PAYMENT = "insert into form_of_payment (id_form_of_payment, type, data) values (?, ?::PAYMENT_TYPE, ?::JSONB)";
	private static final Gson gson;

	static {
		gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy(){
			@Override
			public boolean shouldSkipField(FieldAttributes f) {				
				return f.getName().equals("id");
			}
			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		}).create();		
	}

	public FormOfPaymentRepository(ConnectionWrapper connectionWrapper) {
		super(connectionWrapper);
	}

	public Optional<Card> getCardWithNumber(String number) throws SQLException {
		try (PreparedStatement stmt = getConnection().prepareStatement(GET_BY_NUMBER)) {
			stmt.setString(1, Payment.Type.CREDIT_CARD.name());
			stmt.setString(2, number);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(getCardFromResultSet(rs));
				}
			}
		}
		return Optional.empty();
	}

	public Optional<Boleto> getBoletoWithNumber(String number) throws SQLException {
		try (PreparedStatement stmt = getConnection().prepareStatement(GET_BY_NUMBER)) {
			stmt.setString(1, Payment.Type.BOLETO.name());
			stmt.setString(2, number);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {					
					return Optional.of(getBoletoFromResultSet(rs));
				}
			}
		}
		return Optional.empty();
	}

	public UUID insert(Card card) throws SQLException {		
		PreparedStatement stmt = getConnection().prepareStatement(INSERT_FORM_OF_PAYMENT);
		UUID id = UUID.randomUUID();
		stmt.setObject(1, id);
		stmt.setString(2, Payment.Type.CREDIT_CARD.name());
		stmt.setObject(3, gson.toJson(card));
		stmt.addBatch();
		addBatchStatement(stmt);
		return id;
	}

	public UUID insert(Boleto boleto) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement(INSERT_FORM_OF_PAYMENT);
		UUID id = UUID.randomUUID();
		stmt.setObject(1, id);
		stmt.setString(2, Payment.Type.BOLETO.name());
		stmt.setObject(3, gson.toJson(boleto));
		stmt.addBatch();
		addBatchStatement(stmt);
		return id;
	}

	public static Card getCardFromResultSet(ResultSet rs) throws JsonSyntaxException, SQLException {
		JsonObject formOfPayData = gson.fromJson(rs.getString(COLUMN_DATA), JsonObject.class);
		return new Card(rs.getObject(COLUMN_ID, UUID.class),
			formOfPayData.getAsJsonPrimitive(COLUMN_HOLDER_NAME).getAsString(),
			formOfPayData.getAsJsonPrimitive(COLUMN_NUMBER).getAsString(),
			gson.fromJson(formOfPayData.getAsJsonObject(COLUMN_EXPIRATION_DATE), LocalDate.class),
			formOfPayData.getAsJsonPrimitive(COLUMN_CVV).getAsString());
	}

	public static Boleto getBoletoFromResultSet(ResultSet rs) throws JsonSyntaxException, SQLException {
		JsonObject formOfPayData = gson.fromJson(rs.getString(COLUMN_DATA), JsonObject.class);
		return new Boleto(rs.getObject(COLUMN_ID, UUID.class), formOfPayData.getAsJsonPrimitive(COLUMN_NUMBER).getAsString());
	}
}