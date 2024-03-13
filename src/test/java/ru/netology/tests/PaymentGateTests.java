package ru.netology.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.FormPage;
import ru.netology.pages.ShopPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class PaymentGateTests {
    private String successfulMessage = "Операция одобрена Банком.";
    private String errorMessage = "Ошибка! Банк отказал в проведении операции.";

    private String wrongFormat = "Неверный формат";

    private String wrongTime = "Неверно указан срок действия карты";

    private String expiredCard = "Истёк срок действия карты";

    private String obligatoryField = "Поле обязательно для заполнения";

    @BeforeEach
    void setup() {
        var shopPage = open("http://localhost:8080", ShopPage.class);
        var paymentPage = shopPage.paymentPage();
        var formPage = paymentPage.formPage();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Покупка тура по «одобренной» карте")
    void shouldBuyOnApprovedCard() {
        var formPage = new FormPage();
        var cardInfo = DataHelper.getValidCardInfoApproved();
        formPage.fillForm(cardInfo);
        formPage.findSuccessfulMessage(successfulMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusPaymentGate());
    }

    @Test
    @DisplayName("Покупка тура по «отклоненной» карте")
    void shouldBuyOnDeclinedCard() {
        var formPage = new FormPage();
        var cardInfo = DataHelper.getValidCardInfoDeclined();
        formPage.fillForm(cardInfo);
        formPage.findErrorMessage(errorMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusPaymentGate());
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле Номер карты")
    void shouldNotBuyWithLettersInCardNumberField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(getLetters());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Номер карты")
    void shouldNotBuyWithSymbolsInCardNumberField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(getSymbols());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe17NumbersInCardNumberField() {
        var formPage = new FormPage();
        var cardNumber = get17Numbers();
        formPage.setCardNumberField(cardNumber);
        formPage.getCardNumberField(cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16));
    }

    @Test
    @DisplayName("Отсутствие возможности ввода менее 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe15NumbersInCardNumberField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(get15Numbers());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Номер карты")
    void shouldNotBuyWithNullCardNumberField() {
        var formPage = new FormPage();
        formPage.setCardNumberField("");
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле Месяц")
    void shouldNotBuyWithLettersInMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getLetters());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Месяц")
    void shouldNotBuyWithSymbolsInMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getSymbols());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе однозначных чисел в поле Месяц")
    void shouldNotBuyWithSingleDigitNumberInMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getNumber());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе двузначных чисел, больших 12, в поле Месяц")
    void shouldNotBuyWithMoreThen12InMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField("13");
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более 2 цифр в поле Месяц")
    void shouldNotBuyWithMoreThen2NumbersInMonthField() {
        var formPage = new FormPage();
        var month = get3Numbers();
        formPage.setMonthField(month);
        formPage.getMonthField(month.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Месяц")
    void shouldNotBuyWithNullInMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField("");
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по просроченной карте")
    void shouldNotBuyWithLastMonthInMonthField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getLastMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findExpiredLabel(expiredCard);
    }


    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе буквенных значениий в поле Год")
    void shouldNotBuyWithLettersInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getLetters());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе спецсимволов в поле Год")
    void shouldNotBuyWithSymbolsInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getSymbols());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе последних 2 цифр года, превышающего текущий " +
            "больше чем на 5 лет, в поле Год")
    void shouldNotBuyWithDifferenceMoreThen5YearsInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getYearPlus6Years());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorTimeLabel(wrongTime);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе последних 2 цифр года, меньших, " +
            "чем последние 2 цифры текущего, в поле Год")
    void shouldNotBuyWithLastYearsInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getLastYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findExpiredLabel(expiredCard);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе 1 цифры в поле Год")
    void shouldNotBuyWith1NumberInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getNumber());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более чем 2 цифр в поле Год")
    void shouldNotBuyWith3NumberInYearField() {
        var formPage = new FormPage();
        var year = get3Numbers();
        formPage.setYearField(year);
        formPage.getYearField(year.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Год")
    void shouldNotBuyWithNullInYearField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField("");
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с цифровыми значениями в поле Владелец")
    void shouldNotBuyWithNumbersInUserNameField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(get17Numbers());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте со спецсимволами в поле Владелец")
    void shouldNotBuyWithSymbolsInUserNameField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getSymbols());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при пустом поле Владелец")
    void shouldNotBuyWithNullInUserNameField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField("");
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с именем владельца карты, записанным буквами русского " +
            "алфавита, в поле Владелец")
    void shouldNotBuyWithRussianLettersInUserNameField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getRussianLetters());
        formPage.setCvcField(getValidCvc());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более 50 символов в поле Владелец")
    void shouldNotBuyWithMoreThen50SymbolsInUserNameField() {
        var formPage = new FormPage();
        var userName = get51Letters();
        formPage.setUserNameField(userName);
        formPage.getUserNameField(userName.substring(0, 50));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с буквенными значениями в поле CVC/CVV")
    void shouldNotBuyWithLettersInCvcField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getLetters());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте со спецсимволами в поле CVC/CVV")
    void shouldNotBuyWithSymbolsInCvcField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getSymbols());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте с пустым полем CVC/CVV")
    void shouldNotBuyWithNullInCvcField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField("");
        formPage.buttonClick();
        formPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе более чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWithMoreThen3NumbersInCvcField() {
        var formPage = new FormPage();
        var cvc = get15Numbers();
        formPage.setCvcField(cvc);
        formPage.getCvcField(cvc.substring(0, 3));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура по карте при вводе менее чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWith1NumbersInCvcField() {
        var formPage = new FormPage();
        formPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        formPage.setMonthField(getValidMonth());
        formPage.setYearField(getValidYear());
        formPage.setUserNameField(getValidName());
        formPage.setCvcField(getNumber());
        formPage.buttonClick();
        formPage.findErrorLabel(wrongFormat);
    }
}

