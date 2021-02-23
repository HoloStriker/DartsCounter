package com.example.dartscounter.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.dartscounter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    //TODO: maybe util?
    public static final String DOUBLE = "double";
    public static final String TRIPLE = "triple";
    private static String INPUT_LANGUAGE_KEY;
    private static String FINISH_MODE_KEY;
    private static String[] FINISH_MODES;
    private static String[] INPUT_LANGUAGES;
    private static MainModel mModelInstance;

    private SpeechRecognizer sr;
    private Resources mResources;

    private List<Player> playersList;
    private Player currentPlayerPointer;
    private Map<Integer, ArrayList<String>> scores;

    private MutableLiveData<List<Player>> players;
    private MutableLiveData<Player> currentPlayer;
    private MutableLiveData<Throwable> mError;
    private MutableLiveData<Integer> throwCount;
    private MutableLiveData<Boolean> gameHasFinished;

    private int points, legThrows, currentPlayIndex;
    private boolean doubleOut, doubHit = false, tripHit = false, isListening = false;
    private static final String TAG = "Darts Counter";
    private int backupScore;
    private String finishMode;
    private String inputLanguage;

    private List<Player> finishOrder;
    private boolean gameFinished = false;
    private boolean nextPlayer = false;

    public static MainModel getInstance(Context context) {
        if (mModelInstance == null) {
            mModelInstance = new MainModel(context);
        }
        return mModelInstance;
    }

    private MainModel(Context context) {
        //TODO: SpeechRecognizer in andere Klasse auslagrn
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(new listener());
        mResources = context.getResources();

        FINISH_MODES = context.getResources().getStringArray(R.array.finish_mode_values);
        FINISH_MODE_KEY = context.getString(R.string.finish_mode_key);
        INPUT_LANGUAGES = context.getResources().getStringArray(R.array.input_languages_values);
        INPUT_LANGUAGE_KEY = context.getString(R.string.voice_input_language_key);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.finishMode = sharedPrefs.getString(FINISH_MODE_KEY, FINISH_MODES[0]);
        this.inputLanguage = sharedPrefs.getString(INPUT_LANGUAGE_KEY, INPUT_LANGUAGES[0]);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        loadScores();

        playersList = new ArrayList<>();
        finishOrder = new ArrayList<>();
        players = new MutableLiveData<>();

        currentPlayer = new MutableLiveData<>();
        gameHasFinished = new MutableLiveData<>();
        mError = new MutableLiveData<>();
    }

    //TODO: an deutsche Eingaben anpassen

    private void loadScores() {
        try {
            scores = new HashMap<>();
            StringBuilder sb = new StringBuilder();

            InputStreamReader ir;

            switch (inputLanguage){
                case "german":
                    ir = new InputStreamReader(mResources.openRawResource(R.raw.scores_de));
                    break;

                case "english":
                    ir = new InputStreamReader(mResources.openRawResource(R.raw.scores_en));
                    break;

                default:
                    ir = new InputStreamReader(mResources.openRawResource(R.raw.scores));

            }
            BufferedReader br = new BufferedReader(ir);
            String currLine;

            while ((currLine = br.readLine()) != null) {
                sb.append(currLine);
            }

            String gameData = sb.toString();
            JSONArray scoresJSONArray;
            JSONObject currentScore;

            scoresJSONArray = new JSONArray(gameData);

            for (int i = 0; i < scoresJSONArray.length(); i++) {
                currentScore = scoresJSONArray.getJSONObject(i);
                Integer score = currentScore.getInt("score");
                JSONArray stringsJSONArray = currentScore.getJSONArray("strings");
                ArrayList<String> strings = new ArrayList<>();

                for (int j = 0; j < stringsJSONArray.length(); j++) {
                    strings.add(j, stringsJSONArray.getString(j));
                }
                this.scores.put(score, strings);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenToVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        sr.startListening(intent);
    }

    public MutableLiveData<Throwable> getError() {
        return mError;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Player> getFinishOrder() {
        return finishOrder;
    }

    public void initGame() {
        for (Player player : playersList) {
            player.setCurrentScore(points);
        }
        currentPlayerPointer = playersList.get(0);
        currentPlayer.postValue(currentPlayerPointer);
        reset();
    }

    public boolean isDoubleOut() {
        return doubleOut;
    }

    public void setDoubleOut(boolean doubleOut) {
        this.doubleOut = doubleOut;
    }

    public void addPlayer(String player) {
        this.playersList.add(new Player(player));
        this.players.postValue(playersList);
    }

    public MutableLiveData<List<Player>> getPlayers() {
        return players;
    }

    public MutableLiveData<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    public void removePlayerFromGame(String playerName) {
        List<Player> removeList = new ArrayList<>();
        for (Player player : playersList) {
            if (player.getName().equals(playerName)) {
                removeList.add(player);
            }
        }
        this.playersList.removeAll(removeList);
        this.players.postValue(playersList);
    }

    public void stopListening() {
        this.isListening = false;
    }

    public void startListening() {
        this.isListening = true;
        listenToVoiceInput();
    }

    public void scoreHit(int points) {
        if(nextPlayer){
            switchToNextPlayer();
        }
        evaluateScore(points);
    }

    public void doubleHit(boolean hit) {
        if(nextPlayer){
            switchToNextPlayer();
        }
        doubHit = hit;
    }

    public void tripleHit(boolean hit) {
        if(nextPlayer){
            switchToNextPlayer();
        }
        tripHit = hit;
    }

    public void undo() {
        if (legThrows > 0) {
            if(nextPlayer){
                nextPlayer = false;
            }
            legThrows--;
            String tryToRedo = currentPlayerPointer.getTries()[legThrows];
            int scoreToRedo = 0;
            if (tryToRedo.startsWith("T")) {
                tryToRedo = tryToRedo.substring(1);
                scoreToRedo = Integer.parseInt(tryToRedo);
                scoreToRedo *= 3;
            } else if (tryToRedo.startsWith("D")) {
                tryToRedo = tryToRedo.substring(1);
                scoreToRedo = Integer.parseInt(tryToRedo);
                scoreToRedo *= 2;
            } else {
                scoreToRedo = Integer.parseInt(tryToRedo);
            }
            currentPlayerPointer.setCurrentScore(currentPlayerPointer.getCurrentScore() + scoreToRedo);
            currentPlayerPointer.setTry(legThrows, "-");
            currentPlayerPointer.decreaseDartsCount();
            calculateAverage(currentPlayerPointer);

            players.postValue(playersList);
            currentPlayer.postValue(currentPlayerPointer);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.finishMode = sharedPreferences.getString(FINISH_MODE_KEY, FINISH_MODES[0]);

        if (key.equals(INPUT_LANGUAGE_KEY)) {
            this.inputLanguage = sharedPreferences.getString(FINISH_MODE_KEY, INPUT_LANGUAGES[0]);
            loadScores();
        }
    }

    public MutableLiveData<Boolean> getGameHasFinished() {
        return gameHasFinished;
    }

    public void newGame() {
        playersList.clear();
        finishOrder.clear();
        currentPlayerPointer = null;
        reset();
    }

    public void playAgain() {
        for (Player player : playersList) {
            player.setCurrentScore(points);
            player.setCurrentAvg(0);
            player.setDartsCount(0);
            player.setTries(new String[]{"-", "-", "-"});
        }
        reset();
        this.players.postValue(playersList);
        this.currentPlayerPointer = playersList.get(currentPlayIndex);
        currentPlayer.postValue(currentPlayerPointer);
    }

    private void reset() {
        legThrows = 0;
        currentPlayIndex = 0;
        doubHit = false;
        tripHit = false;
        isListening = false;
        gameFinished = false;
        nextPlayer = false;
        gameHasFinished.setValue(false);
        finishOrder.clear();
    }

    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            if (isListening) {
                listenToVoiceInput();
            }
        }

        public void onResults(Bundle results) {
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int score = -1;

            for (int i = 0; i < data.size(); i++) {
                String rep = ((String) data.get(i)).toLowerCase();
                data.set(i, rep);
            }

            for (int i = 0; i < data.size(); i++) {
                if (((String) data.get(i)).contains(TRIPLE)) {
                    tripHit = true;
                    String rep = ((String) data.get(i)).replace(TRIPLE + " ", "");
                    data.set(i, rep);
                }
                else if (((String) data.get(i)).contains(DOUBLE)) {
                    doubHit = true;
                    String rep = ((String) data.get(i)).replace(DOUBLE + " ", "");
                    data.set(i, rep);
                }
            }

            //TODO: Evtl. Leerzeichen raus löschen
            for (Map.Entry<Integer, ArrayList<String>> entry : scores.entrySet()) {
                for (int i = 0; i < data.size(); i++) {
                    if (entry.getValue().contains(data.get(i))) {
                        score = entry.getKey();
                    }
                }
                if (score > -1) {
                    break;
                }
            }


            if (score > -1) {
                evaluateScore(score);
            }
            //launchSpeechRecognition();
            if (isListening) {
                listenToVoiceInput();
            }
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    private void evaluateScore(int score) {
        String scoreTag = generateScoreTag(score);
        currentPlayerPointer.setTry(legThrows, scoreTag);
        currentPlayerPointer.increaseDartsCount();

        processScore(currentPlayerPointer, score);
        calculateAverage(currentPlayerPointer);

        players.postValue(playersList);
        currentPlayer.postValue(currentPlayerPointer);

        legThrows++;
        doubHit = false;
        tripHit = false;
        if (legThrows >= 3) {
            //TODO: Tell remaining points
            tellScore();
            if(!gameFinished){
                nextPlayer = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(nextPlayer){
                            switchToNextPlayer();
                        }
                    }
                }, 2500);
            }
        }
    }

    private void switchToNextPlayer() {
        nextPlayer = false;
        legThrows = 0;
        do {
            currentPlayIndex++;
            if (currentPlayIndex >= playersList.size()) {
                currentPlayIndex = 0;
            }
        } while (playersList.get(currentPlayIndex).getCurrentScore() <= 0);

        currentPlayerPointer = playersList.get(currentPlayIndex);
        currentPlayer.postValue(currentPlayerPointer);
        backupScore = currentPlayerPointer.getCurrentScore();
        currentPlayerPointer.setTries(new String[]{"-", "-", "-"});
    }

    private String generateScoreTag(int score) {
        StringBuilder sb = new StringBuilder("" + score);
        if (doubHit) {
            sb.insert(0, "D");
        }
        if (tripHit) {
            sb.insert(0, "T");
        }
        String scoreTag = sb.toString();
        Log.d(TAG, "result " + scoreTag);
        return scoreTag;
    }

    private void calculateAverage(Player player) {
        if (player.getDartsCount() > 0) {
            float avg = (points - player.getCurrentScore()) / player.getDartsCount();
            player.setCurrentAvg(avg);
        } else {
            player.setCurrentAvg(0);
        }
    }

    private void processScore(Player player, int score) {
        if (doubHit) {
            score *= 2;
        }
        if (tripHit) {
            score *= 3;
        }
        if (doubleOut) {
            if (player.getCurrentScore() < score || player.getCurrentScore() == score + 1) {
                overthrown(player);
            } else if (player.getCurrentScore() > score + 1 || doubHit && player.getCurrentScore() == score) {
                player.setCurrentScore(player.getCurrentScore() - score);
            }
        } else {
            if (player.getCurrentScore() < score) {
                overthrown(player);
            } else if (player.getCurrentScore() >= score) {
                player.setCurrentScore(player.getCurrentScore() - score);
            }
        }
        if (player.getCurrentScore() == 0) {
            legThrows = 3;
            if (finishMode.equals(FINISH_MODES[1])) {
                finishOrder.addAll(playersList);
                if (finishOrder.size() > 1) {
                    Collections.sort(finishOrder);
                }
                stopListening();
                gameFinished = true;
                this.gameHasFinished.postValue(true);
            } else if (finishMode.equals(FINISH_MODES[0])) {
                finishOrder.add(player);
                boolean finish = true;
                for (Player p : playersList) {
                    if (p.getCurrentScore() > 0) {
                        finish = false;
                    }
                }
                if (finish) {
                    stopListening();
                    gameFinished = true;
                    this.gameHasFinished.postValue(true);
                }
            }
        }
    }

    private void overthrown(Player player) {
        player.setCurrentScore(backupScore);
        legThrows = 3;
        //TODO: Toast
    }

    private void tellScore() {

    }
}
