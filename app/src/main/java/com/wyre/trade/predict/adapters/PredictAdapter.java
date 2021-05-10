package com.wyre.trade.predict.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.PredictionModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;

public class PredictAdapter extends RecyclerView.Adapter<PredictAdapter.CustomerViewHolder> {
    int type;
    Listener listener;
    ArrayList<PredictionModel> data = new ArrayList<PredictionModel>();
    
    public PredictAdapter(ArrayList data, int type) {
        this.data = data;
        this.type = type;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predict, parent, false);
        CustomerViewHolder vh = new CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position) {
        PredictionModel item = data.get(position);

        holder.mtvSymbol.setText("");
        holder.mtvName.setText(item.getSymbol());
        holder.mtvPrice.setText("$"+ item.getPrice());
        holder.mtvBettingPrice.setText(item.getPayout());
//            holder.mtvContent.setText(item.getJSONObject("item").optString("symbol") + " will be "+getEstType(item.optInt("type")) +" $"+ new DecimalFormat("#,###.##").format(item.optDouble("est_price")));
        holder.mtvContent.setText(item.getContent());
//            holder.mtvCreatedDate.setText(item.optString("created_at").substring(0, 10));
        holder.timer.start(item.getDayLeft()*1000);

//            holder.mtvBidders.setText(item.optJSONArray("bidders").length()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(position);
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(position);
            }
        });
        holder.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHandle(position, 0);
            }
        });

        switch (type) {
            case 0:// new
                holder.answerLayout.setVisibility(View.GONE);
                holder.mtvResult.setText(item.getStatus());
                break;
            case 1://my answer
                holder.btnNo.setVisibility(View.GONE);
                if(!item.getStatus().equals("Finished"))
                    holder.mtvResult.setText(item.getStatus());
                else {
                    String str = item.getWinner();
                    if(str.equals("Win"))
                        holder.mtvResult.setText("Lose");
                    else
                        holder.mtvResult.setText("Win");
//                        holder.mtvResultPrice.setText("( $"+item.optString("result") + ")");
                }
                break;
            case 2://my post
                holder.answerLayout.setVisibility(View.GONE);
                holder.btnNo.setVisibility(View.GONE);
                if(!item.getStatus().equals("Finished"))
                    holder.mtvResult.setText(item.getStatus());
                else {
                    holder.mtvResult.setText(item.getWinner());
//                        holder.mtvResultPrice.setText("( $"+item.optString("result") + ")");
                }
                if(item.getStatus().equals("Created"))
                    holder.btnCancel.setVisibility(View.VISIBLE);
                break;
        }

//            holder.mtvAnswer.setText(item.getJSONArray("bid").getJSONObject(0).optString("answer"));
//            holder.mtvPoster.setText(item.optString("poster"));
    }

    @Override
    public int getItemCount() {
        return data!= null? data.size(): 0;
    }

    private String getEstType(int type) {
        switch (type) {
            case 0:
                return "not change";
            case 1:
                return "LOWER than";
            case 2:
                return "HIGHER than";
            default:
                return "not change";
        }
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder{
        TextView mtvSymbol, mtvName, mtvContent, mtvResult, mtvResultPrice, mtvCreatedDate, mtvPoster, mtvAnswer, mtvBidders, mtvPrice, mtvBettingPrice, mtvTotalPayout;
        CountdownView timer;
        LinearLayout posterLayout, answerLayout, questionLayout;
        Button btnYes, btnNo, btnCancel;
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtvSymbol = itemView.findViewById(R.id.symbol);
            mtvName = itemView.findViewById(R.id.name);
            mtvPrice = itemView.findViewById(R.id.current_price);
            mtvBettingPrice = itemView.findViewById(R.id.betting_price);
            mtvContent = itemView.findViewById(R.id.content);
            mtvResult = itemView.findViewById(R.id.result);
            mtvResultPrice = itemView.findViewById(R.id.result_price);
            mtvCreatedDate = itemView.findViewById(R.id.created_date);
            mtvPoster = itemView.findViewById(R.id.poster_name);
            mtvAnswer = itemView.findViewById(R.id.answer);
            mtvBidders = itemView.findViewById(R.id.bidders);

            mtvTotalPayout = itemView.findViewById(R.id.total_payout);

            timer = itemView.findViewById(R.id.timer);

            posterLayout = itemView.findViewById(R.id.layout_poster);
            answerLayout = itemView.findViewById(R.id.layout_answer);
            questionLayout = itemView.findViewById(R.id.layout_question);

            btnNo = itemView.findViewById(R.id.btn_no);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
//            btnYes = itemView.findViewById(R.id.btn_yes);
        }
    }

    public void setListener(Listener listener){this.listener = listener;}
    
    public interface  Listener {
        void onSelect(int position);
        void onCancel(int position);
        void onHandle(int position, int est);
    }
}
