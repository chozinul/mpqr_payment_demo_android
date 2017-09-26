package com.mastercard.labs.mpqrpayment.data;

import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.List;

/**
 * Simulates a database using Realm
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface DataSource {

    User saveUser(User user);

    PaymentInstrument saveCard(PaymentInstrument paymentInstrument);

    User getUser(Long userId);

    PaymentInstrument getCard(Long cardId);

    List<PaymentInstrument> getCards(Long userId);

    boolean deleteUser(long userId);

}
