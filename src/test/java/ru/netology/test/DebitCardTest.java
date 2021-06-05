package ru.netology.test;

import com.codeborne.selenide.Condition;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.StartPage;


import static com.codeborne.selenide.Condition.exactValue;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLHelper.*;

public class DebitCardTest {
    DataHelper.CardNumber approvedCard = DataHelper.approvedCardInfo();
    DataHelper.CardNumber declinedCard = DataHelper.declinedCardInfo();
    DataHelper.CardInfo cardInfo = DataHelper.getCardInfo();

    @AfterEach
    public void cleanTables() {
        SQLHelper.cleanData();
    }

    @Nested
    public class ValidTest {

        @Test
        void shouldDebitCardPurchaseWithStatusApproved() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getSuccessNotification().shouldBe(Condition.visible, ofSeconds(20));
            assertEquals(approvedCard.getStatus(), payData().getStatus());
            assertEquals(payData().getTransaction_id(), orderData().getPayment_id());
        }

        @Test
        void shouldDebitCardPurchaseWithStatusDeclined() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(declinedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getErrorMassage().shouldBe(Condition.visible, ofSeconds(20));
            assertEquals(declinedCard.getStatus(), payData().getStatus());
            checkEmptyOrderEntity();
        }
    }

    @Nested
    public class NotValidTest {

        @Test
        void shouldDebitCardPurchaseWithStatusUnknown() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails("4444444444444444", cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getErrorMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithStatusNotValid() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails("444444444444444", cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithStatusExpiredTodayYear() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getPastMonth(), cardInfo.getTodayYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getInvalidCardMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithStatusExpiredLastYear() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getPastYear(),
                    cardInfo.getOwner(), cardInfo.getCvc());
            cashPage.getInvalidCardMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithNameInCyrillic() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getRusOwner(), cardInfo.getCvc());
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithEmptyFields() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.clickNextButton();
            cashPage.getSuccessNotification().shouldNot(Condition.visible, ofSeconds(20));
            cashPage.getErrorMassage().shouldNot(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }

        @Test
        void shouldDebitCardPurchaseWithFullLetters() {
            val cashPage = new StartPage().openCashPayPage();
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
        void shouldDebitCardPurchaseWithFieldOwnerInNumbers() {
            val cashPage = new StartPage().openCashPayPage();
            cashPage.enterCardDetails(approvedCard.getCardNumber(), cardInfo.getMonth(), cardInfo.getYear(),
                    cardInfo.getNumbers(), cardInfo.getCvc());
            cashPage.getOwnerEl().shouldNotHave(exactValue(cardInfo.getNumbers()));
            cashPage.getWrongFormatMassage().shouldBe(Condition.visible, ofSeconds(20));
            checkEmptyPaymentEntity();
            checkEmptyOrderEntity();
        }
    }
}
