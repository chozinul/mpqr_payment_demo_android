package com.mastercard.labs.mpqrpayment.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardPagerAdapter;
import com.mastercard.labs.mpqrpayment.database.model.Card;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.pager_container)
    PagerContainer pagerContainer;

    @BindView(R.id.txtAvailableBalanceAmount)
    TextView availableBalanceTextView;

    Realm realm;

    private List<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        pagerContainer.setOverlapEnabled(true);

        final ViewPager viewPager = pagerContainer.getViewPager();
        CardPagerAdapter pagerAdapter = new CardPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        new CoverFlow.Builder().with(viewPager)
                .scale(0.3f)
                .pagerMargin(getResources().getDimensionPixelSize(R.dimen.overlap_pager_margin))
                .spaceSize(0f)
                .build();

        invalidateViews();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        realm.removeAllChangeListeners();

        super.onDestroy();
    }

    // Page listener methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Card card = this.cards.get(position);

        availableBalanceTextView.setText(card.getBalanceAmount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void invalidateViews() {
        RealmResults<Card> cards = realm.where(Card.class).findAll();
        cards.addChangeListener(new RealmChangeListener<RealmResults<Card>>() {
            @Override
            public void onChange(RealmResults<Card> result) {
                refreshCards(result);
            }
        });

        refreshCards(cards);
    }

    private void refreshCards(RealmResults<Card> cards) {
        this.cards = realm.copyFromRealm(cards);

        final ViewPager viewPager = pagerContainer.getViewPager();
        CardPagerAdapter pagerAdapter = (CardPagerAdapter) viewPager.getAdapter();

        pagerAdapter.setCards(Collections.unmodifiableList(this.cards));
        pagerAdapter.notifyDataSetChanged();

        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        if (cards.size() > 0) {
            // Manually setting the first View to be elevated
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                    ViewCompat.setElevation(fragment.getView(), 8.0f);
                }
            });
        }
    }
}
