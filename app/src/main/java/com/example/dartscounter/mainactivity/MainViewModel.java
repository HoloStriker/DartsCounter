package com.example.dartscounter.mainactivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Throwable> mError;
    private MainModel mModel;

    private MutableLiveData<Integer> throwCount;

    private Map<String, Integer> playerPoints;

    public MainViewModel(MainModel mModel) {
        this.mModel = mModel;
        this.mError = this.mModel.getError();
        this.playerPoints = new HashMap<>();

    }

    public boolean isDoubleOut() {
        return mModel.isDoubleOut();
    }

    private void setDoubleOut(String mode) {
        switch (mode) {
            case "Single Out":
                mModel.setDoubleOut(false);
                break;
            case "Double Out":
                mModel.setDoubleOut(true);
                break;
        }
    }

    public int getPoints() {
        return mModel.getPoints();
    }

    public MutableLiveData<List<Player>> getPlayers() {
        return mModel.getPlayers();
    }

    public MutableLiveData<Player> getCurrentPlayer() {
        return mModel.getCurrentPlayer();
    }

    public MutableLiveData<Boolean> getGameHasFinished() {
        return mModel.getGameHasFinished();
    }

    //TODO: wieder ordentlich machen
    private void setPoints(String points) {
        switch (points){
            case "301":
                mModel.setPoints(1);
                break;
            case "501":
                mModel.setPoints(501);
                break;
            case "701":
                mModel.setPoints(701);
                break;
            case "901":
                mModel.setPoints(901);
                break;
            default:
                //TODO: Fehler abfangen
        }
    }

    public int getPlayerPoints(String player){
        return playerPoints.get(player);
    }

    public void setPlayerPoints(String player, int points){
        playerPoints.put(player, points);
    }

    public void addPlayer(String player){
        mModel.addPlayer(player);
    }

    public void initGame(String points, String mode){
        setPoints(points);
        setDoubleOut(mode);
        mModel.initGame();
    }

    public void removePlayerFromGame(String playerName) {
        mModel.removePlayerFromGame(playerName);
    }

    public void startListening() {
        mModel.startListening();
    }

    public void stopListening() {
        mModel.stopListening();
    }

    public void scoreHit(int points) {
        mModel.scoreHit(points);
    }

    public void setDoubleHit(boolean hit) {
        mModel.doubleHit(hit);
    }

    public void setTripleHit(boolean hit) {
        mModel.tripleHit(hit);
    }

    public void undo() {
        mModel.undo();
    }

    public List<Player> getFinishOrder() {
        return mModel.getFinishOrder();
    }

    public void newGame() {
        mModel.newGame();
    }

    public void playAgain() {
        mModel.playAgain();
    }

    public static class MainViewModelFactory implements ViewModelProvider.Factory {

        private MainModel mModel;

        public MainViewModelFactory(MainModel mModel) {
            this.mModel = mModel;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(mModel);
        }
    }
}