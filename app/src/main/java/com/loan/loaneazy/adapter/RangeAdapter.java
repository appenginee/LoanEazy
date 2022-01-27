package com.loan.loaneazy.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loan.loaneazy.R;
import com.loan.loaneazy.model.general.AmountRange;
import com.loan.loaneazy.my_interface.RowItemClickListener;
import com.loan.loaneazy.views.MyTextView;

import java.util.ArrayList;

public class RangeAdapter extends RecyclerView.Adapter<RangeAdapter.RangeHolder> {
    private final RowItemClickListener mClickListener;
    Context mCtx;
    ArrayList<AmountRange> mList;
    private static SparseBooleanArray sSelectedItems;
    private static final int MULTIPLE = 0;
    private static final int SINGLE = 1;
    private static int sModo = 0;
    private static int sPosition;

    public RangeAdapter(FragmentActivity activity, ArrayList<AmountRange> mAmountList, int modo) {
        this.mCtx = activity;
        this.mList = mAmountList;
        this.mClickListener= (RowItemClickListener) activity;
        sSelectedItems = new SparseBooleanArray();
        sModo = modo;

    }

    @NonNull
    @Override
    public RangeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mCtx).inflate(R.layout.row_range_amount, parent, false);
        return new RangeHolder(inflate);


    }

    @Override
    public void onBindViewHolder(@NonNull RangeHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void selected(int position) {
        switch (sModo) {
            case SINGLE:
                sPosition = position;
                notifyDataSetChanged();
                break;
            case MULTIPLE:
            default:
                break;
        }
    }

    public class RangeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyTextView tvRangeAmount;
        LinearLayout mRootLinear;

        public RangeHolder(@NonNull View itemView) {
            super(itemView);
            tvRangeAmount = itemView.findViewById(R.id.tvRangeAmount);
            mRootLinear = itemView.findViewById(R.id.vertical_list_item_background);
            itemView.setOnClickListener(this);

        }

        public void bind() {
            tvRangeAmount.setText("\u20B9" + " " + mList.get(getAdapterPosition()).getAmount());
            mRootLinear.setSelected(sSelectedItems.get(getAdapterPosition(), false));
        }

        @Override
        public void onClick(View view) {
            if (sSelectedItems.get(getAdapterPosition(), false)) {
                sSelectedItems.delete(getAdapterPosition());
                mRootLinear.setSelected(false);
                //mLabel.setTextColor(ContextCompat.getColor(sContext, android.R.color.black));
            } else {
                switch (sModo) {
                    case SINGLE:
                        sSelectedItems.put(sPosition, false);
                        break;
                    case MULTIPLE:
                    default:
                        break;
                }
                //mLabel.setTextColor(ContextCompat.getColor(sContext, R.color.colorAccent));
                sSelectedItems.put(getAdapterPosition(), true);
                mRootLinear.setSelected(true);
                mClickListener.onRowItemClick(getAdapterPosition());
            }
        }
        }
    }

