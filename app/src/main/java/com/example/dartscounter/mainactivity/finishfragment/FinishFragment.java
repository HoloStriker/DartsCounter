package com.example.dartscounter.mainactivity.finishfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.example.dartscounter.mainactivity.MainModel;
import com.example.dartscounter.mainactivity.MainViewModel;
import com.example.dartscounter.mainactivity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishFragment extends Fragment {

    public static final String TAG = "FINISH_FRAGMENT";
    private MainViewModel mViewModel;
    private RecyclerView rvPlayers;
    private FinishPlayersListAdapter adapter;
    private List<Player> players;
    private Button btnNewGame, btnPlayAgain;

    public static FinishFragment newInstance() {
        return new FinishFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.finish_fragment, container, false);

        initialise(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainModel mModel = MainModel.getInstance(getContext());
        MainViewModel.MainViewModelFactory factory = new MainViewModel.MainViewModelFactory(mModel);
        mViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        this.players.clear();
        this.players.addAll(mViewModel.getFinishOrder());
        adapter.notifyDataSetChanged();
        // TODO: Use the ViewModel
    }

    private void initialise(View view) {
        this.players = new ArrayList<>();

        this.btnNewGame = view.findViewById(R.id.btn_FinishNewGame);
        this.btnNewGame.setOnClickListener(v -> {
            mViewModel.newGame();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(FinishFragmentDirections.actionFinishFragmentToStartFragment());
        });
        this.btnPlayAgain = view.findViewById(R.id.btn_FinishPlayAgain);
        this.btnPlayAgain.setOnClickListener(v -> {
            mViewModel.playAgain();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(FinishFragmentDirections.actionFinishFragmentToGameFragment());
        });

        rvPlayers = view.findViewById(R.id.rv_FinishPlayersList);
        rvPlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FinishPlayersListAdapter(players);
        rvPlayers.setAdapter(adapter);
    }
}