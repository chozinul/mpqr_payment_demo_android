package com.mastercard.labs.mpqrpayment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kaile on 17/7/17.
 */

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());

    private List<Transaction> transactions;
    private TransactionItemListener mListener;

    public TransactionsAdapter(List<Transaction> transactions, TransactionItemListener listener) {
        this.transactions = transactions;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onClick(int position) {
                if (mListener != null) {
                    mListener.onTransactionItemClicked(transactions.get(position));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        String currency = "";
        if (transaction.getCurrencyCode() != null) {
            currency = transaction.getCurrencyCode().toString() + " ";
        }

        holder.mAmountTextView.setText(String.format(Locale.getDefault(), "%s%,.2f", currency, transaction.getTotal()));
        holder.mMerchantNameView.setText(transaction.getMerchantName());
        holder.mDateTextView.setText(dateFormat.format(transaction.getTransactionDate()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_transaction_amount)
        TextView mAmountTextView;

        @BindView(R.id.txt_merchant_name)
        TextView mMerchantNameView;

        @BindView(R.id.txt_transaction_date)
        TextView mDateTextView;

        private ViewHolderClickListener mListener;

        ViewHolder(View itemView, ViewHolderClickListener listener) {
            super(itemView);

            mListener = listener;

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(getAdapterPosition());
            }
        }

        interface ViewHolderClickListener {
            void onClick(int position);
        }
    }

    public interface TransactionItemListener {
        void onTransactionItemClicked(Transaction transaction);
    }
}