package com.example.dartscounter.mainactivity.gamefragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.example.dartscounter.mainactivity.Player;

import java.util.List;

public class GamePlayersListAdapter extends RecyclerView.Adapter<GamePlayersListItemViewHolder> {
    private List<Player> mDataset;

    public GamePlayersListAdapter(List<Player> mDataset) {
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public GamePlayersListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_game_players_list_item, parent, false);

        return new GamePlayersListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GamePlayersListItemViewHolder viewGroup, int position) {
        TextView tvName = viewGroup.getTvName();
        tvName.setText(mDataset.get(position).getName());

        TextView tvScore = viewGroup.getTvScore();
        tvScore.setText(""+mDataset.get(position).getCurrentScore());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

