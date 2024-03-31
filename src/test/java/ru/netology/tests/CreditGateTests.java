package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.CreditGatePage;
import ru.netology.pages.ShopPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.cleanDatabase;


public class CreditGateTests {
    CreditGatePage creditPage;
    private String successfulMessage = "Операция одобрена Банком.";
    private String errorMessage = "Ошибка! Банк отказал в проведении операции.";

    private String wrongFormat = "Неверный формат";

    private String wrongTime = "Неверно указан срок действия карты";

    private String expiredCard = "Истёк срок действия карты";

    private String obligatoryField = "Поле обязательно для заполнения";


    @BeforeEach
    void setup() {
        creditPage = open("http://localhost:8080", ShopPage.class).creditPage();
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
    @DisplayName("Покупка тура в кредит по «одобренной» карте")
    void shouldBuyOnApprovedCard() {
        var cardInfo = DataHelper.getValidCardInfoApproved();
        creditPage.fillForm(cardInfo);
        creditPage.findSuccessfulMessage(successfulMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusCreditGate());
    }

    @Test
    @DisplayName("Покупка тура в кредит по «отклоненной» карте")
    void shouldBuyOnDeclinedCard() {
        var cardInfo = DataHelper.getValidCardInfoDeclined();
        creditPage.fillForm(cardInfo);
        creditPage.findErrorMessage(errorMessage);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getVerificationStatusCreditGate());
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с буквенными значениями в поле Номер карты")
    void shouldNotBuyWithLettersInCardNumberField() {
        creditPage.setCardNumberField(getLetters());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе спецсимволов в поле Номер карты")
    void shouldNotBuyWithSymbolsInCardNumberField() {
        creditPage.setCardNumberField(getSymbols());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe17NumbersInCardNumberField() {
        var cardNumber = get17Numbers();
        creditPage.setCardNumberField(cardNumber);
        creditPage.getCardNumberField(cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16));
    }

    @Test
    @DisplayName("Отсутствие возможности ввода менее 16 цифр в поле Номер карты")
    void shouldNotBuyWithThe15NumbersInCardNumberField() {
        creditPage.setCardNumberField(get15Numbers());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при пустом поле Номер карты")
    void shouldNotBuyWithNullCardNumberField() {
        creditPage.setCardNumberField("");
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с буквенными значениями в поле Месяц")
    void shouldNotBuyWithLettersInMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getLetters());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе спецсимволов в поле Месяц")
    void shouldNotBuyWithSymbolsInMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getSymbols());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе однозначных чисел в поле Месяц")
    void shouldNotBuyWithSingleDigitNumberInMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getNumber());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе двузначных чисел, больших 12, в поле Месяц")
    void shouldNotBuyWithMoreThen12InMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getMoreThen12());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorTimeLabel(wrongTime);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе более 2 цифр в поле Месяц")
    void shouldNotBuyWithMoreThen2NumbersInMonthField() {
        var month = get3Numbers();
        creditPage.setMonthField(month);
        creditPage.getMonthField(month.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при пустом поле Месяц")
    void shouldNotBuyWithNullInMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField("");
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит по просроченной карте")
    void shouldNotBuyWithLastMonthInMonthField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getLastMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findExpiredLabel(expiredCard);
    }


    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе буквенных значениий в поле Год")
    void shouldNotBuyWithLettersInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getLetters());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе спецсимволов в поле Год")
    void shouldNotBuyWithSymbolsInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getSymbols());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе последних 2 цифр года, превышающего текущий " +
            "больше чем на 5 лет, в поле Год")
    void shouldNotBuyWithDifferenceMoreThen5YearsInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getYearPlus6Years());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorTimeLabel(wrongTime);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе последних 2 цифр года, меньших, " +
            "чем последние 2 цифры текущего, в поле Год")
    void shouldNotBuyWithLastYearsInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getLastYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findExpiredLabel(expiredCard);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе 1 цифры в поле Год")
    void shouldNotBuyWith1NumberInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getNumber());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности ввода более чем 2 цифр в поле Год")
    void shouldNotBuyWith3NumberInYearField() {
        var year = get3Numbers();
        creditPage.setYearField(year);
        creditPage.getYearField(year.substring(0, 2));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при пустом поле Год")
    void shouldNotBuyWithNullInYearField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField("");
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с цифровыми значениями в поле Владелец")
    void shouldNotBuyWithNumbersInUserNameField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(get17Numbers());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит со спецсимволами в поле Владелец")
    void shouldNotBuyWithSymbolsInUserNameField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getSymbols());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при пустом поле Владелец")
    void shouldNotBuyWithNullInUserNameField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField("");
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с именем владельца карты, записанным буквами русского " +
            "алфавита, в поле Владелец")
    void shouldNotBuyWithRussianLettersInUserNameField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getRussianLetters());
        creditPage.setCvcField(getValidCvc());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе более 50 символов в поле Владелец")
    void shouldNotBuyWithMoreThen50SymbolsInUserNameField() {
        var userName = get51Letters();
        creditPage.setUserNameField(userName);
        creditPage.getUserNameField(userName.substring(0, 50));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с буквенными значениями в поле CVC/CVV")
    void shouldNotBuyWithLettersInCvcField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getLetters());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит со спецсимволами в поле CVC/CVV")
    void shouldNotBuyWithSymbolsInCvcField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getSymbols());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит с пустым полем CVC/CVV")
    void shouldNotBuyWithNullInCvcField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField("");
        creditPage.buttonClick();
        creditPage.findObligatoryFieldLabel(obligatoryField);
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе более чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWithMoreThen3NumbersInCvcField() {
        var cvc = get15Numbers();
        creditPage.setCvcField(cvc);
        creditPage.getCvcField(cvc.substring(0, 3));
    }

    @Test
    @DisplayName("Отсутствие возможности покупки тура в кредит при вводе менее чем 3 цифр в поле CVC/CVV")
    void shouldNotBuyWith1NumbersInCvcField() {
        creditPage.setCardNumberField(DataHelper.getValidCardInfoApproved().getNumber());
        creditPage.setMonthField(getValidMonth());
        creditPage.setYearField(getValidYear());
        creditPage.setUserNameField(getValidName());
        creditPage.setCvcField(getNumber());
        creditPage.buttonClick();
        creditPage.findErrorLabel(wrongFormat);
    }
}
