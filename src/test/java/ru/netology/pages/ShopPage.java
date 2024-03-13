package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ShopPage {
    private SelenideElement head = $("h2");
    public ShopPage() {
        head.shouldBe(visible);
    }
    public CreditGatePage creditPage(){
        $$("button").find(exactText("Купить в кредит")).click();
        return new CreditGatePage();
    }

    public PaymentGatePage paymentPage(){
        $$("button").find(exactText("Купить")).click();
        return new PaymentGatePage();
    }
}