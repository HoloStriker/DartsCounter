package com.example.dartscounter.mainactivity.gamefragment;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {

    public static final String TAG = "GAME_FRAGMENT";
    private MainViewModel mViewModel;
    private List<Player> players;
    private TextView tvName, tvFirst, tvSecond, tvThird, tvAvg, tvScore, tvCount;
    private RecyclerView rvPlayers;
    private ConstraintLayout clNumpad;
    private final ArrayList<Button> btnPoints = new ArrayList<>();
    private Button btnUndo, btnDouble, btnTriple, btnMiss;
    private boolean doubleHit, tripleHit;
    private GamePlayersListAdapter adapter;
    private Button btnBull;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        initialise(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showAlertDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainModel mModel = MainModel.getInstance(getContext());
        MainViewModel.MainViewModelFactory factory = new MainViewModel.MainViewModelFactory(mModel);
        mViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        mViewModel.getCurrentPlayer().observe(requireActivity(), this::updateCurrentPlayer);
        mViewModel.getPlayers().observe(requireActivity(), this::updatePlayers);
        mViewModel.getGameHasFinished().observe(getViewLifecycleOwner(), this::gameHasFinished);
        // TODO: Use the ViewModel
    }

    private void gameHasFinished(Boolean gameHasFinished) {
        if(gameHasFinished){
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(GameFragmentDirections.actionGameFragmentToFinishFragment());
        }
    }

    private void updatePlayers(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        adapter.notifyDataSetChanged();
    }

    private void updateCurrentPlayer(Player player) {
        tvName.setText(player.getName());
        tvScore.setText(""+player.getCurrentScore());
        tvCount.setText(""+player.getDartsCount());
        tvAvg.setText(""+player.getCurrentAvg());

        tvFirst.setText(player.getTries()[0]);
        tvSecond.setText(player.getTries()[1]);
        tvThird.setText(player.getTries()[2]);
    }

    private void initialise(View view) {
        players = new ArrayList<>();

        clNumpad = view.findViewById(R.id.cl_GameNumpad);

        tvName = view.findViewById(R.id.tv_GameCurrentPlayer);
        tvAvg = view.findViewById(R.id.tv_GameCurrentAverage);
        tvScore = view.findViewById(R.id.tv_GameCurrentScore);
        tvCount = view.findViewById(R.id.tv_GameCurrentDartsCount);

        tvFirst = view.findViewById(R.id.tv_GameCurrentFirst);
        tvSecond = view.findViewById(R.id.tv_GameCurrentSecond);
        tvThird = view.findViewById(R.id.tv_GameCurrentThird);

        view.findViewById(R.id.tbtn_GameInputMode).setOnClickListener(v -> {
            boolean voice = ((ToggleButton) v).isChecked();
            if(voice) {
                mViewModel.startListening();
                //TODO: hide Keyboard
                clNumpad.setVisibility(View.GONE);
            } else {
                mViewModel.stopListening();
                //TODO: show Keydoard
                clNumpad.setVisibility(View.VISIBLE);
            }
        });

        //TODO: wie kann man das vereinfachen?
        for (int i = 1; i <= 20; i++) {
            String buttonID = "btn_GameNumpad" + i;
            int resID = getResources().getIdentifier(buttonID, "id", "com.example.dartscounter");
            Button btn = view.findViewById(resID);
            int finalI = i;
            btn.setOnClickListener(v -> {
                mViewModel.scoreHit(finalI);
                resetNumpad();
            });
            btnPoints.add(btn);
        }
        btnBull = view.findViewById(R.id.btn_GameNumpad25);
        btnBull.setOnClickListener(v -> {
            mViewModel.scoreHit(25);
            resetNumpad();
        });
        btnPoints.add(btnBull);

        btnDouble = view.findViewById(R.id.btn_GameNumpadDouble);
        btnTriple = view.findViewById(R.id.btn_GameNumpadTriple);
        btnDouble.setOnClickListener(v -> {
            doubleHit = !doubleHit;
            mViewModel.setDoubleHit(doubleHit);
            if(doubleHit){
                v.setBackgroundColor(fetchColor(R.attr.colorSecondary));
                if(tripleHit)
                {
                    btnTriple.callOnClick();
                }
            } else {
                v.setBackgroundColor(fetchColor(R.attr.colorPrimary));
            }
        });
        btnTriple.setOnClickListener(v -> {
            tripleHit = !tripleHit;
            mViewModel.setTripleHit(tripleHit);
            if(tripleHit){
                //v.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_variant));
                v.setBackgroundColor(fetchColor(R.attr.colorSecondary));
                btnBull.setEnabled(false);
                if(doubleHit)
                {
                    btnDouble.callOnClick();
                }
            } else {
                v.setBackgroundColor(fetchColor(R.attr.colorPrimary));
                btnBull.setEnabled(true);
            }
        });
        btnUndo = view.findViewById(R.id.btn_GameNumpadUndo);
        btnUndo.setOnClickListener(v -> {
            mViewModel.undo();
        });
        btnMiss = view.findViewById(R.id.btn_GameNumpadMiss);
        btnMiss.setOnClickListener (v -> {
            mViewModel.scoreHit(0);
            resetNumpad();
        });

        rvPlayers = view.findViewById(R.id.rv_GamePlayerList);
        rvPlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GamePlayersListAdapter(players);
        rvPlayers.setAdapter(adapter);
    }

    private void showAlertDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    onExit();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.exit_dialog_title)).setMessage(getString(R.string.exit_dialog_msg)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    private void resetNumpad() {
        doubleHit = false;
        tripleHit = false;
        btnBull.setEnabled(true);
        btnDouble.setBackgroundColor(fetchColor(R.attr.colorPrimary));
        btnTriple.setBackgroundColor(fetchColor(R.attr.colorPrimary));
    }

    public void onExit() {
        mViewModel.newGame();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(GameFragmentDirections.actionGameFragmentToStartFragment());
    }

    private int fetchColor(int colorID) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[] { colorID });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}