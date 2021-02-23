package com.example.dartscounter.mainactivity.gamefragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;

public class GamePlayersListItemViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName, tvScore;

    public GamePlayersListItemViewHolder(View itemView) {
        super(itemView);
        this.tvName = itemView.findViewById(R.id.tv_GamePlayerItemName);
        this.tvScore = itemView.findViewById(R.id.tv_GamePlayerItemScore);
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvScore() {
        return tvScore;
    }
}
