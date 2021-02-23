package com.example.dartscounter.mainactivity.finishfragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;

public class FinishPlayersListItemViewHolder extends RecyclerView.ViewHolder{
    private TextView tvName, tvScore, tvPosition, tvDarts, tvAvg;

    public FinishPlayersListItemViewHolder(View itemView) {
        super(itemView);
        this.tvPosition = itemView.findViewById(R.id.tv_FinishPlayerItemPosition);
        this.tvName = itemView.findViewById(R.id.tv_FinishPlayerItemName);
        this.tvScore = itemView.findViewById(R.id.tv_FinishPlayerItemScore);
        this.tvDarts = itemView.findViewById(R.id.tv_FinishPlayerItemDarts);
        this.tvAvg = itemView.findViewById(R.id.tv_FinishPlayerItemAverage);
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvScore() {
        return tvScore;
    }

    public TextView getTvPosition() {
        return tvPosition;
    }

    public TextView getTvDarts() {
        return tvDarts;
    }

    public TextView getTvAvg() {
        return tvAvg;
    }
}
