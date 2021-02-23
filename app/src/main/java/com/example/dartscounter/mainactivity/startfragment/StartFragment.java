package com.example.dartscounter.mainactivity.startfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dartscounter.R;
import com.example.dartscounter.mainactivity.MainActivity;
import com.example.dartscounter.mainactivity.MainModel;
import com.example.dartscounter.mainactivity.MainViewModel;
import com.example.dartscounter.mainactivity.Player;

import java.util.ArrayList;
import java.util.List;

public class StartFragment extends Fragment implements StartPlayersListAdapter.StartPlayersListListener {

    public static final String TAG = "START_FRAGMENT";
    private MainViewModel mViewModel;

    private ArrayList<String> players;
    private EditText etPlayerName;
    private RecyclerView rvPlayers;
    private Spinner spPoints, spMode;
    private StartPlayersListAdapter adapter;

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);

        initialise(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainModel mModel = MainModel.getInstance(getContext());
        MainViewModel.MainViewModelFactory factory = new MainViewModel.MainViewModelFactory(mModel);
        mViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        mViewModel.getPlayers().observe(requireActivity(), this::updatePlayersList);
        // TODO: Use the ViewModel
    }

    private void initialise(View view) {
        players = new ArrayList<>();

        etPlayerName = view.findViewById(R.id.et_StartPlayerName);

        spPoints = view.findViewById(R.id.sp_StartPoints);
        ArrayAdapter<CharSequence> pointsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.game_points, android.R.layout.simple_spinner_item);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPoints.setAdapter(pointsAdapter);

        spMode = view.findViewById(R.id.sp_StartMode);
        ArrayAdapter<CharSequence> modesAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.game_modes, android.R.layout.simple_spinner_item);
        modesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMode.setAdapter(modesAdapter);

        rvPlayers = view.findViewById(R.id.rv_StartPlayersList);
        rvPlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StartPlayersListAdapter(players, this);
        rvPlayers.setAdapter(adapter);

        view.findViewById(R.id.btn_StartAddPlayer).setOnClickListener(v -> {
            String player = etPlayerName.getText().toString();
            addPlayer(player);
            etPlayerName.setText("");
        });

        view.findViewById(R.id.btn_StartStart).setOnClickListener(v -> startGame());
    }

    private void updatePlayersList(List<Player> playersList) {
        this.players.clear();
        for (Player player : playersList) {
            this.players.add(player.getName());
        }
        adapter.notifyDataSetChanged();
    }

    private void startGame() {
        if (players.size() > 0) {
            mViewModel.initGame((String) spPoints.getSelectedItem(), (String) spMode.getSelectedItem());

            loadGameFragment();
        }
    }

    private void loadGameFragment() {
        MainActivity.hideKeyboard(getActivity());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(StartFragmentDirections.actionStartFragmentToGameFragment());
    }

    private void addPlayer(String player) {
        mViewModel.addPlayer(player);
    }

    @Override
    public void onButtonClick(String playerName) {
        mViewModel.removePlayerFromGame(playerName);
    }
}