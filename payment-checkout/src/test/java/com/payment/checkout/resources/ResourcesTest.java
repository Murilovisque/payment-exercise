package com.payment.checkout.resources;

import static com.payment.checkout.TestUtils.execRequestGetAndParseAsJson;
import static com.payment.checkout.TestUtils.execRequestPostWithJsonBody;
import static com.payment.checkout.TestUtils.generateRandomBoletoPayment;
import static com.payment.checkout.TestUtils.generateRandomBuyer;
import static com.payment.checkout.TestUtils.generateRandomCardPayment;
import static com.payment.checkout.TestUtils.getCardsResourcePath;
import static com.payment.checkout.TestUtils.getBuyersResourcePath;
import static com.payment.checkout.TestUtils.getPaymentsResourcePath;
import static com.payment.checkout.TestUtils.loadFileFromResourceAndSetProperty;
import static com.payment.checkout.TestUtils.getRandomCardNumber;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.repositories.BuyerRepository;
import com.payment.checkout.HTTPUtils;
import com.payment.checkout.PaymentCheckout;
import com.payment.checkout.resources.models.BoletoJSON;
import com.payment.checkout.resources.models.BuyerJSON;
import com.payment.checkout.resources.models.CardJSON;
import com.payment.checkout.resources.models.CreditCardPaymentJSON;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResourcesTest {

    private static Path tempDir;
    private BuyerJSON buyer;
    private PaymentTestJSON boletoPayment;
    private PaymentTestJSON cardPayment;

    @BeforeClass
    public static void setupServer() throws Exception {
        tempDir = Files.createTempDirectory("payment-checkout-tests");
        loadFileFromResourceAndSetProperty(tempDir);
        PaymentCheckout.getInstance().start();
    }

    @Test()
    public void testBoletoResources() throws ClientProtocolException, IOException, URISyntaxException {
        buyer = generateRandomBuyer();
        boletoPayment = generateRandomBoletoPayment();
        testBuyerExists(false, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testBoletoPaymentExists(getPaymentsResourcePath(), false, boletoPayment);
        Pair<Integer, String> statusAndBody = execRequestPostWithJsonBody(getPaymentsResourcePath() + "/boletos",
                new BoletoJSON(boletoPayment.getAmount(),
                        new BuyerJSON(buyer.getName(), buyer.getCpf(), buyer.getEmail())));
        assertEquals(statusAndBody.getRight(), Integer.valueOf(HTTPUtils.HTTP_STATUS_CREATED), statusAndBody.getLeft());
        testBuyerExists(true, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testBoletoPaymentExists(getPaymentsResourcePath(), true, boletoPayment);
    }

    @Test
    public void testCardResources() throws ClientProtocolException, IOException, URISyntaxException {
        buyer = generateRandomBuyer();
        cardPayment = generateRandomCardPayment();
        testBuyerExists(false, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testCardPaymentExists(getPaymentsResourcePath(), false, cardPayment);
        Pair<Integer, String> statusAndBody = execRequestPostWithJsonBody(getPaymentsResourcePath() + "/credit-cards",
                new CreditCardPaymentJSON(cardPayment.getAmount(),
                        new BuyerJSON(buyer.getName(), buyer.getCpf(), buyer.getEmail()),
                        new CardJSON(cardPayment.getCard().getHolderName(), cardPayment.getCard().getNumber(),
                                cardPayment.getCard().getExpirationDate(), cardPayment.getCard().getCvv())));
        assertEquals(statusAndBody.getRight(), Integer.valueOf(HTTPUtils.HTTP_STATUS_CREATED), statusAndBody.getLeft());
        testBuyerExists(true, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testCardPaymentExists(getPaymentsResourcePath(), true, cardPayment);
    }

    @Test
    public void testBuyerResources()  throws ClientProtocolException, IOException, URISyntaxException {
        buyer = generateRandomBuyer();
        boletoPayment = generateRandomBoletoPayment();
        testBuyerExists(false, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testBuyerExists(false, BuyerRepository.COLUMN_NAME, buyer.getName(), buyer);
        testBuyerExists(false, BuyerRepository.COLUMN_EMAIL, buyer.getEmail(), buyer);
        Pair<Integer, String> statusAndBody = execRequestPostWithJsonBody(getPaymentsResourcePath() + "/boletos",
                new BoletoJSON(boletoPayment.getAmount(),
                        new BuyerJSON(buyer.getName(), buyer.getCpf(), buyer.getEmail())));
        assertEquals(statusAndBody.getRight(), Integer.valueOf(HTTPUtils.HTTP_STATUS_CREATED), statusAndBody.getLeft());
        testBuyerExists(true, BuyerRepository.COLUMN_CPF, buyer.getCpf(), buyer);
        testBuyerExists(true, BuyerRepository.COLUMN_NAME, buyer.getName(), buyer);
        Optional<Buyer> foundBuyer = testBuyerExists(true, BuyerRepository.COLUMN_EMAIL, buyer.getEmail(), buyer);
        testBoletoPaymentExists(String.format("%s/%s/payments", getBuyersResourcePath(), foundBuyer.get().getId().toString()), true, boletoPayment);
    }

    @Test
    public void testCardsResources() throws JsonSyntaxException, ClientProtocolException, IOException {
        String uri = getCardsResourcePath() + "/brands/" + getRandomCardNumber();
        BrandJSON brand = execRequestGetAndParseAsJson(uri, BrandJSON.class);
        assertEquals(Card.Brand.VISA, brand.getBrand());
    }

    private Optional<Buyer> testBuyerExists(boolean expected, String field, String value, BuyerJSON buyer) throws ClientProtocolException, IOException, URISyntaxException {
        URI uri = new URIBuilder(getBuyersResourcePath()).addParameter(field, value).build();
        List<Buyer> buyers = execRequestGetAndParseAsJson(uri.toString(), new TypeToken<List<Buyer>>() {}.getType());
        Optional<Buyer> foundBuyer = buyers.stream().filter(b -> b.getName().equals(buyer.getName())).findAny();
        assertEquals(expected, foundBuyer.isPresent());
        return foundBuyer;
    }

    private void testBoletoPaymentExists(String url, boolean expected, PaymentTestJSON boletoPayment) throws ClientProtocolException, IOException, URISyntaxException {
        List<PaymentTestJSON> payments = execRequestGetAndParseAsJson(url, new TypeToken<List<PaymentTestJSON>>() {}.getType());
        boolean findBoleto = payments.stream().filter(p -> p.isBoleto()).anyMatch(b -> b.getAmount().equals(boletoPayment.getAmount()));
        assertEquals(expected, findBoleto);
    }

    private void testCardPaymentExists(String url, boolean expected, PaymentTestJSON cardPayment) throws ClientProtocolException, IOException, URISyntaxException {
        List<PaymentTestJSON> payments = execRequestGetAndParseAsJson(url, new TypeToken<List<PaymentTestJSON>>() {}.getType());
        boolean findCard = payments.stream().filter(p -> p.isCard()).anyMatch(b -> b.getAmount().equals(cardPayment.getAmount())
            && b.getCard().getHolderName().equals(cardPayment.getCard().getHolderName())
            && b.getCard().getNumber().equals(cardPayment.getCard().getNumber())
            && b.getCard().getExpirationDate().equals(cardPayment.getCard().getExpirationDate())
            && b.getCard().getCvv().equals(cardPayment.getCard().getCvv()));
        assertEquals(expected, findCard);
    }

	@AfterClass
    public static void clear() throws IOException {
        Files.list(tempDir).forEach(t -> {
            try {
                Files.delete(t);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Files.deleteIfExists(tempDir);
    }
}