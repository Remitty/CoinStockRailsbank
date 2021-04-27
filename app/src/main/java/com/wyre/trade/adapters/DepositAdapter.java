package com.wyre.trade.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.DepositInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.OrderViewHolder> {
    private List<DepositInfo> arrItems;
    private Listener listener;
    private Context mContext;

    public DepositAdapter(List<DepositInfo> arrItems, Context context) {
        this.arrItems = arrItems;
        this.mContext = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvCoinName, tvDepositAmount, tvDepositDate, tvDepositStatus;
        ImageView coinIcon;

        public OrderViewHolder(View view) {
            super(view);

            tvCoinName = view.findViewById(R.id.coin_name);
            tvDepositAmount = view.findViewById(R.id.deposit_amount);
            tvDepositDate = view.findViewById(R.id.deposit_date);
            tvDepositStatus = view.findViewById(R.id.status);
            coinIcon = view.findViewById(R.id.coin_icon);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deposit, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        DepositInfo item = arrItems.get(position);

        holder.tvCoinName.setText(item.getCoinSymbol());
        holder.tvDepositAmount.setText(item.getAmount());
        holder.tvDepositStatus.setText(item.getStatus());
        holder.tvDepositDate.setText(item.getDepositDate());

//        if(!item.getCoinSymbol().equalsIgnoreCase("DAI"))
            Picasso.with(mContext)
                    .load(item.getCoinIcon())
                    .placeholder(R.drawable.coin_bitcoin)
                    .error(R.drawable.coin_bitcoin)
                    .into(holder.coinIcon);

//        if(item.getCoinSymbol().equalsIgnoreCase("DAI")){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                holder.coinIcon.setImageDrawable(mContext.getDrawable(R.drawable.dai));
//            }
//        }

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

    public interface Listener {
        /**
         * @param position
         */
        void OnDeposit(int position);
    }
}
