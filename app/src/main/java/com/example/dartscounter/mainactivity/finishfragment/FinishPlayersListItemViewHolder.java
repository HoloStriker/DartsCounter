package com.example.dartscounter.mainactivity.finishfragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;

public class FinishPlayersListItemViewHolder extends RecyclerView.ViewHolder{
    private TextView tvName, tvScore, tvPosition;

    public FinishPlayersListItemViewHolder(View itemView) {
        super(itemView);
        this.tvPosition = itemView.findViewById(R.id.tv_FinishPlayerItemPosition);
        this.tvName = itemView.findViewById(R.id.tv_FinishPlayerItemName);
        this.tvScore = itemView.findViewById(R.id.tv_FinishPlayerItemScore);
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
}
