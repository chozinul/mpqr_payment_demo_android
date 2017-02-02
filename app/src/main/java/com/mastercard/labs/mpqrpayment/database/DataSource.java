package com.mastercard.labs.mpqrpayment.database;

import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.database.model.User;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface DataSource {
    User getUser(Long userId);

    Card getCard(Long cardId);
}
