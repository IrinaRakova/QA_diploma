package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;


public class DataHelper {

    private DataHelper() {
    }

    private static final Faker FAKER = new Faker(new Locale("en"));

    @Value
    public static class CardInfo {
        String number;
        String month;
        String year;
        String user;
        String cvc;
    }

    public static String getValidMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM", new Locale("ru")));
    }

    public static String getLastMonth() {
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM", new Locale("ru")));
    }

    public static String getValidYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getYearPlus6Years() {
        return LocalDate.now().plusYears(6).format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getLastYear() {
        return LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getValidName() {
        String userName = FAKER.name().firstName() + " " + FAKER.name().lastName();
        return userName;
    }

    public static String getValidCvc() {
        String cvc = FAKER.regexify("[0-9]{3}");
        return cvc;
    }

    public static CardInfo getValidCardInfoApproved() {
        String number = getValidCardApproved().getNumber();
        String month = getValidMonth();
        String year = getValidYear();
        String user = getValidName();
        String cvc = getValidCvc();
        return new CardInfo(number, month, year, user, cvc);
    }

    public static CardInfo getValidCardInfoDeclined() {
        String number = getValidCardDeclined().getNumber();
        String month = getValidMonth();
        String year = getValidYear();
        String user = getValidName();
        String cvc = getValidCvc();
        return new CardInfo(number, month, year, user, cvc);
    }

    @Value
    public static class CardStatus {
        String number;
        String status;
    }

    public static CardStatus getValidCardApproved() {
        return new CardStatus("4444 4444 4444 4441", "APPROVED");
    }

    public static CardStatus getValidCardDeclined() {
        return new CardStatus("4444 4444 4444 4442", "DECLINED");
    }

    public static String getVerifyStatus(String number) {
        if (number == getValidCardApproved().getNumber()) {
            return getValidCardApproved().getStatus();
        }
        if (number == getValidCardDeclined().getNumber()) {
            return getValidCardDeclined().getStatus();
        } else {
            return null;
        }
    }

    public static String getLetters() {
        String nameStr = FAKER.regexify("[A-Z]{20}");
        return nameStr;
    }

    public static String getRussianLetters() {
        String nameStr = FAKER.regexify("[А-Я]{20}");
        return nameStr;
    }

    public static String get51Letters() {
        String nameStr = FAKER.regexify("[A-Z]{51}");
        return nameStr;
    }

    public static String getSymbols() {
        String[] specialSymbols = {"!", "@", "#", "$", "%", "^", "&", "(", ")", "'", "_", "+", "?", "<"};
        int numberSpecialSymbols = 14;
        int randomIndexSpecialSymbols = (int) (Math.random() * numberSpecialSymbols);
        int i = 0;
        String result = "";
        while (i < 10) {
            result = result + specialSymbols[randomIndexSpecialSymbols];
            randomIndexSpecialSymbols = (int) (Math.random() * numberSpecialSymbols);
            i++;
        }
        return result;
    }

    public static String get17Numbers() {
        return FAKER.regexify("[0-9]{17}");
    }

    public static String get15Numbers() {
        return FAKER.regexify("[0-9]{15}");
    }

    public static String getNumber() {
        return FAKER.regexify("[0-9]{1}");
    }

    public static String get3Numbers() {
        return FAKER.regexify("[0-9]{3}");
    }

    public static String getMoreThen12() {
        int randomNum = new Random().nextInt(99 - 12 + 1) + 12;
        return Integer.toString(randomNum);
    }
}