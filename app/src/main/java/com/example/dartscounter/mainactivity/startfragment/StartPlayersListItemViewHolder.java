package com.example.dartscounter.mainactivity.startfragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StartPlayersListItemViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private FloatingActionButton fabDelete;

    public StartPlayersListItemViewHolder(View itemView) {
        super(itemView);
        this.tvName = itemView.findViewById(R.id.tv_StartPlayerItemName);
        this.fabDelete = itemView.findViewById(R.id.fab_StartPlayerItemDelete);
    }

    public FloatingActionButton getFabDelete() {
        return fabDelete;
    }

    public TextView getTvName() {
        return tvName;
    }
}
