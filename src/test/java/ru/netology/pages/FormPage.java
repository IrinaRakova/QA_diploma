package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FormPage {
    DataHelper.CardInfo card;
    private SelenideElement cardNumberField = $$("[class='form-field form-field_size_m form-field_theme_alfa-on-white']").find(exactText("Номер карты"));
    private SelenideElement monthField = $$("[class='input-group__input-case']").find(exactText("Месяц"));
    private SelenideElement yearField = $$("[class='input-group__input-case']").find(exactText("Год"));
    private SelenideElement userNameField = $$("[class='input-group__input-case']").find(exactText("Владелец"));
    private SelenideElement cvcField = $$("[class='input-group__input-case']").find(exactText("CVC/CVV"));
    private SelenideElement button = $$("button").find(exactText("Продолжить"));
    private SelenideElement errorFormatLabel =$$("[class=input__sub]").find(exactText("Неверный формат"));
    private SelenideElement errorTimeLabel =$$("[class=input__sub]").find(exactText("Неверно указан срок " +
            "действия карты"));
    private SelenideElement expiredLabel = $$("[class=input__sub]").find(exactText("Истёк срок " +
            "действия карты"));
    private SelenideElement obligatoryFieldLabel = $$("[class=input__sub]").find(exactText("Поле обязательно " +
            "для заполнения"));

    private SelenideElement maxSymbolsLabel = $$("[class=input__sub]").find(exactText("Достигнуто " +
            "максимальное количество символов"));
    private final SelenideElement successfulMessage = $(".notification_status_ok");
    private final SelenideElement errorMessage = $(".notification_status_error");


    public void fillForm(DataHelper.CardInfo info) {
        cardNumberField.$("input").setValue(info.getNumber());
        monthField.$("input").setValue(info.getMonth());
        yearField.$("input").setValue(info.getYear());
        userNameField.$("input").setValue(info.getUser());
        cvcField.$("input").setValue(info.getCvc());
        button.click();
    }

    public void setCardNumberField(String number) {
        cardNumberField.$("input").setValue(number);
    }

    public void setMonthField(String month) {
        monthField.$("input").setValue(month);
    }

    public void setYearField(String year) {
        yearField.$("input").setValue(year);
    }

    public void setUserNameField(String userName) {
        userNameField.$("input").setValue(userName);
    }

    public void setCvcField(String cvc) {cvcField.$("input").setValue(cvc);}

    public void buttonClick(){
        button.click();
    }

    public void findSuccessfulMessage(String expectedText) {
        successfulMessage.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findErrorMessage(String expectedText) {
        errorMessage.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findErrorLabel(String expectedText) {
        errorFormatLabel.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findErrorTimeLabel(String expectedText) {
        errorTimeLabel.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findExpiredLabel(String expectedText) {
        expiredLabel.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findObligatoryFieldLabel(String expectedText) {
        obligatoryFieldLabel.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void findMaxSymbolsLabel(String expectedText) {
        maxSymbolsLabel.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void getCardNumberField(String cardNumber) {
        cardNumberField.$("input").shouldHave(value(cardNumber));
    }

    public void getMonthField(String month) {
        monthField.$("input").shouldHave(value(month));
    }

    public void getYearField(String year) {
        yearField.$("input").shouldHave(value(year));
    }

    public void getUserNameField(String userName) {
        userNameField.$("input").shouldHave(value(userName));
    }

    public void getCvcField(String cvc) {
        cvcField.$("input").shouldHave(value(cvc));
    }
}
