package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class CreditGatePage {
    private SelenideElement creditHead = $(byText("Кредит по данным карты"));

    public FormPage formPage() {
        creditHead.should(visible);
        return new FormPage();
    }
}
