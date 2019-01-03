package com.payment.checkout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.payment.checkout.database.DatabaseConfig;
import com.payment.checkout.resources.BoletoTestJSON;
import com.payment.checkout.resources.CardTestJSON;
import com.payment.checkout.resources.PaymentTestJSON;
import com.payment.checkout.resources.models.BuyerJSON;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class TestUtils {

    private static final Gson gson = new Gson();
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String HOST = "localhost";
    private static final List<String> CARD_NUMBERS = Arrays.asList("4556871504426069", "4485852708185657",
        "4556467913497344", "4929688328133899", "4556277357283823", "4539584687209168", "4211092039420223",
        "4411477338138766", "4916194519060236", "4556327462407179", "4485264899141244", "4916115640204179",
        "4721388719722745", "4716300107757933", "4556177250512767", "4929659693545686", "4354431299600347",
        "4716745355647071", "4716071706745252", "4485355445728144", "4916267195723416", "4716755950468666",
        "4539692582476160", "4716581422105300", "4447263794924552", "4929703894140770", "4539406846854793",
        "4916009705084560", "4716758425234440", "4929672190091740", "4539844225408585", "4485077001824998",
        "4485275867755138", "4024007157039883", "4485327938311506", "4804518156980048", "4929502647107965",
        "4705151549567650", "4929010912803607", "4916946870124285", "4929422069635478", "4024007145353230",
        "4532609061449622", "4929688909765705", "4556075685790125", "4556444353105562", "4916293045099153",
        "4716660215816511", "4556775826892728", "4916109740583764", "4916158128748291", "4024007134649788",
        "4539286348367662", "4716644339153647", "4556752584452474", "4556013007796744", "4716097622491846",
        "4591951895633825", "4556838658760171", "4556498440546659", "4024007107231895", "4532029832963712",
        "4485872140196260", "4916914965147678", "4556266408077516", "4532079888296871", "4556027143807181",
        "4485310143208782", "4539523299150113", "4556955262472009", "4485483412823563", "4485138897721407",
        "4485467940911396", "4652767715736978", "4485913749488631", "4539196229715170", "4532198543383633",
        "4532231346813058", "4716599614188942", "4539637444568548", "4024007156589631", "4485104257015533",
        "4716335538021715", "4916883316725468", "4556060921434506", "4716140428880968", "4024007180797564",
        "4916902954542637", "4968188844558606", "4485399468335072", "4916203345689892", "4932920812029878",
        "4916062355870283", "4024007184857679", "4916786728444828", "4556396973169776", "4916684982025773",
        "4532489582747154", "4532234555397573", "4716280421484515");

    public static String getBuyersResourcePath() {
        return "http://" + HOST + ":" + PaymentCheckoutConfig.getApplicationPort() + "/payment-checkout/api/v1/buyers";
    }

    public static String getPaymentsResourcePath() {
        return "http://" + HOST + ":" + PaymentCheckoutConfig.getApplicationPort() + "/payment-checkout/api/v1/payments";
    }

    public static String getCardsResourcePath() {
        return "http://" + HOST + ":" + PaymentCheckoutConfig.getApplicationPort() + "/payment-checkout/api/v1/cards";
    }

    public static <T> T execRequestGetAndParseAsJson(String url, Type type)
            throws JsonSyntaxException, ClientProtocolException, IOException {
        return gson.fromJson(Request.Get(url).execute().returnContent().asString(), type);
    }

    public static Pair<Integer, String> execRequestPostWithJsonBody(String url, Object json)
            throws ClientProtocolException, IOException {
        HttpResponse res = Request.Post(url).bodyString(gson.toJson(json), ContentType.APPLICATION_JSON).execute()
                .returnResponse();
        return Pair.of(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity()));

    }

    public static final Collection<Pair<String, String>> PROPERTIES = Arrays.asList(
            Pair.of(PaymentCheckoutConfig.CONFIG_PATH_PROPERTY, "application.properties"),
            Pair.of(DatabaseConfig.CONFIG_PATH_PROPERTY, "database.properties"),
            Pair.of("logback.configurationFile", "logback.xml"));

    public static Path copyResourceFileToPath(String file, Path target) throws IOException {
        InputStream fileInputStream = TestUtils.class.getClassLoader().getResourceAsStream(file);
        Path filePath = Paths.get(target.toString(), file);
        Files.copy(fileInputStream, filePath);
        return filePath;
    }

    public static void loadFileFromResourceAndSetProperty(Path target) throws IOException {
        for (Pair<String, String> pair : PROPERTIES) {
            Path path = copyResourceFileToPath(pair.getRight(), target);
            System.setProperty(pair.getLeft(), path.toString());
        }
    }

    public static BuyerJSON generateRandomBuyer() {
        return new BuyerJSON(RandomStringUtils.random(60, CHARACTERS), RandomStringUtils.randomNumeric(11),
                RandomStringUtils.random(50, CHARACTERS));
    }

    public static PaymentTestJSON generateRandomBoletoPayment() {
        return PaymentTestJSON.newAsBoleto(
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                new BoletoTestJSON(RandomStringUtils.randomNumeric(15)));
    }

    public static PaymentTestJSON generateRandomCardPayment() {
        return PaymentTestJSON.newAsCard(
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                new CardTestJSON(RandomStringUtils.random(60, CHARACTERS), getRandomCardNumber(),
                        LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()),
                        RandomStringUtils.randomNumeric(3, 3)));
    }

    public static String getRandomCardNumber() {
        Collections.shuffle(CARD_NUMBERS);
        return CARD_NUMBERS.get(0);
    }

}