package com.example.dartscounter.mainactivity.startfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StartPlayersListAdapter extends RecyclerView.Adapter<StartPlayersListItemViewHolder>{
    private List<String> mDataset;
    private StartPlayersListListener mCallback;

    public StartPlayersListAdapter(List<String> mDataset, StartPlayersListListener mCallback) {
        this.mDataset = mDataset;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public StartPlayersListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_start_players_list_item, parent, false);

        return new StartPlayersListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StartPlayersListItemViewHolder viewGroup, int position) {
        TextView tvName = viewGroup.getTvName();
        tvName.setText(mDataset.get(position));

        FloatingActionButton fabDelete = viewGroup.getFabDelete();
        fabDelete.setOnClickListener(v -> mCallback.onButtonClick(mDataset.get(position)));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface StartPlayersListListener {
        void onButtonClick(String player);
    }
}
