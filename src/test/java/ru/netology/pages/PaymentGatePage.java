package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

public class PaymentGatePage {
    private SelenideElement paymentHead = $(byText("Оплата по карте"));

    public FormPage formPage() {
        paymentHead.should(visible);
        return new FormPage();
    }
}
