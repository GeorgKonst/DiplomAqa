package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;


import static com.codeborne.selenide.Condition.exactValue;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLHelper.*;

public class CreditCardTest {
    DataHelper.CardNumber approvedCard = DataHelper.approvedCardInfo();
    DataHelper.CardNumber declinedCard = DataHelper.declinedCardInfo();
    DataHelper.CardInfo cardInfo = DataHelper.getCardInfo();

    @AfterEach
    public void cleanTables() {
        SQLHelper.cleanData();
    }

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Nested
    public class ValidTest {

        @Test
        void shouldCreditCardPurchaseWithStatusApproved() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getSuccessNotification().shouldBe(Condition.visible, ofSeconds(20));
            assertEquals(approvedCard.getStatus(), creditData().getStatus());
            assertEquals(creditData().getBank_id(), orderData().getPayment_id());
        }

        @Test
        void shouldCreditCardPurchaseWithStatusDeclined() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(declinedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getErrorMassage().shouldBe(Condition.visible, ofSeconds(20));
            assertEquals(declinedCard.getStatus(), creditData().getStatus());
            checkEmptyOrderEntity();
        }
    }

    @Nested
    public class NotValidTest {

        @Test
        void shouldCreditCardPurchaseWithStatusUnknown() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails("4444444444444444", cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getErrorMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithStatusNotValid() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails("444444444444444", cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithStatusExpiredTodayYear() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getPastMonth(), cardInfo.getTodayYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getInvalidCardMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithStatusExpiredLastYear() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getPastYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getInvalidCardMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithNameInCyrillic() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getRusOwner(), cardInfo.getCvc());
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithEmptyFields() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.clickNextButton();
            cashPage.getSuccessNotification().shouldNot(Condition.visible, ofSeconds(20));
            cashPage.getErrorMassage().shouldNot(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithFullLetters() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(cardInfo.getLetters(), cardInfo.getLetters(), cardInfo.getLetters(),
                    cardInfo.getOwner(), cardInfo.getLetters());
            cashPage.getCardNumberEl().shouldNotHave(exactValue(cardInfo.getLetters()));
            cashPage.getMonthEl().shouldNotHave(exactValue(cardInfo.getLetters()));
            cashPage.getYearEl().shouldNotHave(exactValue(cardInfo.getLetters()));
            cashPage.getCvcEl().shouldNotHave(exactValue(cardInfo.getLetters()));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldCreditCardPurchaseWithFieldOwnerInNumbers() {
            val cashPage = new StartPage().openCreditPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getNumbers(), cardInfo.getCvc());
            cashPage.getOwnerEl().shouldNotHave(exactValue(cardInfo.getNumbers()));
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyCreditEntity();
        }

    }
}