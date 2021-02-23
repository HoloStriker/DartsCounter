package com.example.dartscounter.mainactivity.finishfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.example.dartscounter.mainactivity.Player;

import java.util.List;

public class FinishPlayersListAdapter extends RecyclerView.Adapter<FinishPlayersListItemViewHolder> {
    private List<Player> mDataset;

    public FinishPlayersListAdapter(List<Player> mDataset) {
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public FinishPlayersListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_finish_players_list_item, parent, false);

        return new FinishPlayersListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishPlayersListItemViewHolder viewGroup, int position) {
        TextView tvName = viewGroup.getTvName();
        tvName.setText(mDataset.get(position).getName());

        TextView tvScore = viewGroup.getTvScore();
        tvScore.setText(""+mDataset.get(position).getCurrentScore());

        TextView tvPosition = viewGroup.getTvPosition();
        tvPosition.setText(""+(position+1));

        TextView tvDarts = viewGroup.getTvDarts();
        tvDarts.setText(""+mDataset.get(position).getDartsCount());

        TextView tvAvg = viewGroup.getTvAvg();
        tvAvg.setText(""+mDataset.get(position).getCurrentAvg());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
