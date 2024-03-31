package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.FormPage;
import ru.netology.pages.PaymentGatePage;
import ru.netology.pages.ShopPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class PaymentGateTests {
    PaymentGatePage paymentPage;
    private String successfulMessage = "Операция одобрена Банком.";
    private String errorMessage = "Ошибка! Банк отказал в проведении операции.";

    private String wrongFormat = "Неверный формат";

    private String wrongTime = "Неверно указан срок действия карты";

    private String expiredCard = "Истёк срок действия карты";

    private String obligatoryField = "Поле обязательно для заполнения";

    @BeforeEach
    void setup() {
        paymentPage = open("http://localhost:8080", ShopPage.class).paymentPage();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Покупка тура по «одобренной» карте")
    void shouldBuyOnApprovedCard() {
        var cardInfo = DataHelper.getValidCardInfoApproved();
        paymentPage.fillForm(cardInfo);
        paymentPage.findSuccessfulMessage(successfulMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusPaymentGate());
    }

    @Test
    @DisplayName("Покупка тура по «отклоненной» карте")
    void shouldBuyOnDeclinedCard() {
        var cardInfo = DataHelper.getValidCardInfoDeclined();
        paymentPage.fillForm(cardInfo);
        paymentPage.findErrorMessage(errorMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusPaymentGate());
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле Номер карты")
    void shouldNotBuyWithLettersInCardNumberField() {
        paymentPage.setCardNumberField(getLetters());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Номер карты")
    void shouldNotBuyWithSymbolsInCardNumberField() {
        paymentPage.setCardNumberField(getSymbols());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe17NumbersInCardNumberField() {
        var cardNumber = get17Numbers();
        paymentPage.setCardNumberField(cardNumber);
        paymentPage.getCardNumberField(cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16));
    }

    @Test
    @DisplayName("Отсутствие возможности ввода менее 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe15NumbersInCardNumberField() {
        paymentPage.setCardNumberField(get15Numbers());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Номер карты")
    void shouldNotBuyWithNullCardNumberField() {
        paymentPage.setCardNumberField("");
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле Месяц")
    void shouldNotBuyWithLettersInMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getLetters());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Месяц")
    void shouldNotBuyWithSymbolsInMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getSymbols());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе однозначных чисел в поле Месяц")
    void shouldNotBuyWithSingleDigitNumberInMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getNumber());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе двузначных чисел, больших 12, в поле Месяц")
    void shouldNotBuyWithMoreThen12InMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getMoreThen12());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorTimeLabel(wrongTime);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более 2 цифр в поле Месяц")
    void shouldNotBuyWithMoreThen2NumbersInMonthField() {
        var month = get3Numbers();
        paymentPage.setMonthField(month);
        paymentPage.getMonthField(month.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Месяц")
    void shouldNotBuyWithNullInMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField("");
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по просроченной карте")
    void shouldNotBuyWithLastMonthInMonthField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getLastMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findExpiredLabel(expiredCard);
    }


    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе буквенных значениий в поле Год")
    void shouldNotBuyWithLettersInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getLetters());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Год")
    void shouldNotBuyWithSymbolsInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getSymbols());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе последних 2 цифр года, превышающего текущий " +
            "больше чем на 5 лет, в поле Год")
    void shouldNotBuyWithDifferenceMoreThen5YearsInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getYearPlus6Years());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorTimeLabel(wrongTime);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе последних 2 цифр года, меньших, " +
            "чем последние 2 цифры текущего, в поле Год")
    void shouldNotBuyWithLastYearsInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getLastYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findExpiredLabel(expiredCard);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе 1 цифры в поле Год")
    void shouldNotBuyWith1NumberInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getNumber());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более чем 2 цифр в поле Год")
    void shouldNotBuyWith3NumberInYearField() {
        var year = get3Numbers();
        paymentPage.setYearField(year);
        paymentPage.getYearField(year.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Год")
    void shouldNotBuyWithNullInYearField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField("");
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с цифровыми значениями в поле Владелец")
    void shouldNotBuyWithNumbersInUserNameField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(get17Numbers());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте со спецсимволами в поле Владелец")
    void shouldNotBuyWithSymbolsInUserNameField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getSymbols());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Владелец")
    void shouldNotBuyWithNullInUserNameField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField("");
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с именем владельца карты, записанным буквами русского " +
            "алфавита, в поле Владелец")
    void shouldNotBuyWithRussianLettersInUserNameField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getRussianLetters());
        paymentPage.setCvcField(getValidCvc());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более 50 символов в поле Владелец")
    void shouldNotBuyWithMoreThen50SymbolsInUserNameField() {
        var userName = get51Letters();
        paymentPage.setUserNameField(userName);
        paymentPage.getUserNameField(userName.substring(0, 50));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле CVC/CVV")
    void shouldNotBuyWithLettersInCvcField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getLetters());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте со спецсимволами в поле CVC/CVV")
    void shouldNotBuyWithSymbolsInCvcField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getSymbols());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с пустым полем CVC/CVV")
    void shouldNotBuyWithNullInCvcField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField("");
        paymentPage.buttonClick();
        paymentPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWithMoreThen3NumbersInCvcField() {
        var cvc = get15Numbers();
        paymentPage.setCvcField(cvc);
        paymentPage.getCvcField(cvc.substring(0, 3));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе менее чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWith1NumbersInCvcField() {
        paymentPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        paymentPage.setMonthField(getValidMonth());
        paymentPage.setYearField(getValidYear());
        paymentPage.setUserNameField(getValidName());
        paymentPage.setCvcField(getNumber());
        paymentPage.buttonClick();
        paymentPage.findErrorLabel(wrongFormat);
    }
}

