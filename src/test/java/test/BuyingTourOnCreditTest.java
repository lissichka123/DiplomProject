package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuyingTourOnCreditTest {
    MainPage mainPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("Allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        mainPage = open("http://localhost:8080/", MainPage.class);
    }

    @AfterEach
    void tearDownAllDatabase() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Successful purchase of a tour with payment on credit with valid APPROVED card.")
    void shouldSuccessfulPurchaseOfTourInCreditWithValidApprovedDebitCard() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLineCreditRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Buying a tour on credit with valid cardholder with dot in name")
    void payTurAPPROVEDCardOwnerWithDot() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringNameWithDot();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLineCreditRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Buying a tour with valid cardholder with dash in name")
    void payTurAPPROVEDCardOwnerWithDash() {
        mainPage.chooseBy("Оплата по карте");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringNameWithDash();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Buying a tour with valid cardholder with 3 letters in name")
    void payTurAPPROVEDCardOwnerWithMinName() {
        mainPage.chooseBy("Оплата по карте");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringMinName();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour on credit when filling out the form with valid DECLINED card data.")
    void shouldUnsuccessfulPurchaseOfTourInCreditWithValidDeclancedDebitCard() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringDeclinedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour on credit when filling out the form with card data that is not registered in the system.")
    void shouldUnsuccessfulPurchaseOfTourInCreditWithDebitCardNotRegisteredInSystem() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringRandomCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
    }

    @Test
    @DisplayName("Getting an error when submitting a form with letters in the card number.")
    void shouldReturnErrorWhenLettersInCardNumber() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringInvalidCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotificationIsNotVisible();
        mainPage.verifyErrorCardNumberField("Неверный формат");
    }

    @Test
    @DisplayName("Receiving an error when filling out the form with expired card data.")
    void shouldReturnErrorWhenCardWithExpiredCardData() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringApprovedCard();
        mainPage.enteringInvalidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotificationIsNotVisible();
        mainPage.verifyPeriodErrorYearField("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Error when buying a tour on credit with invalid cardholder data on the form.")
    void shouldReturnErrorWhenCardWithInvalidCardholder() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringInValidOwner();
        mainPage.enteringValidCVC();
        mainPage.verifySuccessfulNotificationIsNotVisible();
        mainPage.verifyErrorOwnerField("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Error occurred when submitting a completed form with invalid CVC/CVV data.")
    void shouldReturnErrorWhenCardWithInvalidCVC() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.enteringApprovedCard();
        mainPage.enteringValidCardValidityPeriod();
        mainPage.enteringValidOwner();
        mainPage.enteringInValidCVC();
        mainPage.verifySuccessfulNotificationIsNotVisible();
        mainPage.verifyErrorCVCField("Неверный формат");
    }

    @Test
    @DisplayName("Receiving an error when sending an empty application form for the purchase of a tour.")
    void shouldReturnErrorWhenEmptyForm() {
        mainPage.chooseByInCredit("Кредит по данным карты");
        mainPage.verifySuccessfulNotificationIsNotVisible();
        mainPage.verifyErrorCardNumberField("Неверный формат");
        mainPage.verifyErrorMonthField("Неверный формат");
        mainPage.verifyErrorYearField("Неверный формат");
        mainPage.verifyErrorOwnerField("Поле обязательно для заполнения");
        mainPage.verifyErrorCVCField("Неверный формат");
    }
}
