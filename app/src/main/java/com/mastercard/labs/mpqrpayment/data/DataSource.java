package com.mastercard.labs.mpqrpayment.data;

import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface DataSource {
    User saveUser(User user);

    Card saveCard(Card card);

    User getUser(Long userId);

    Card getCard(Long userId, Long cardId);

    List<Card> getCards(Long userId);
}
