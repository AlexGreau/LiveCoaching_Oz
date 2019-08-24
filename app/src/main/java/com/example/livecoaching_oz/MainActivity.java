package com.example.livecoaching_oz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.livecoaching_oz.Communication.ClientTask;
import com.example.livecoaching_oz.Communication.Decoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements Decoder {

    private final String TAG = MainActivity.class.getSimpleName();

    // UI components
    private Button startButton;
    private Button finishButton;
    private Button leftButton;
    private Button rightButton;
    private Button straightButton;
    protected AlertDialog startRunDialog;

    // feedback
    private Vibrator vibrator;
    private int interactionType;
    private boolean vibroIsForbidden;
    private int patternIndex = 100; // 3: strait, -1: left, 1: right

    private long[] pattern;
    private int[] amplitudes;
    private int indexInPatternToRepeat = 0;

    // Communication
    private ClientTask myClientTask;

    // Logic Values
        // start time
        // finish time
    private int numberOfCorrectionMade;
    private String ID;
    private String Order;

    // Logger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    // ~~~~~~~~~~~~  inits  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void init() {
        initValues();
        initUI();
        initCommunication();
    }

    private void initValues(){
        numberOfCorrectionMade = 0;
        ID = "";
        Order = "stay";
    }

    private void initUI() {
        initStartButton();
        initFinishButton();
        initLeftButton();
        initRightButton();
        initStraightButton();
    }

    private void initCommunication() {
        myClientTask = new ClientTask("hey !", this);
    }

    private void initStartButton(){
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRunDialog = buildStartDialog();
                startRunDialog.show();
            }
        });
    }

    private void initFinishButton(){
        finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildStopRunDialog().show();
            }
        });

    }

    private void initRightButton(){

    }

    private void initLeftButton(){

    }

    private void initStraightButton(){

    }

    // ~~~~~~~~~~~~ Decoder methods  ~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void decodeResponse(String rep) {
        Log.d(TAG, "rep : " + rep);
    }

    @Override
    public void errorMessage(String err) {
        Log.e(TAG, err);
    }

    // ~~~~~~~~~~~~  Logic functions  ~~~~~~~~~~~~~~~~~~~~~~~~

    private void startRun(String ID){
        // reset values
        // update UI
    }

    private void finishRun(){
        // log Values
        // reset Values ?
        // update UI
    }

    private void orderLeft(){

    }

    private void orderRight(){

    }

    private void orderStraight(){

    }

    protected boolean isValid(String text) {
        Pattern pattern = Pattern.compile("\\w+?");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    // ~~~~~~~~~~~~  dialog Builder functions  ~~~~~~~~~~~~~~~

    private AlertDialog buildStartDialog(){
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_start, null);

        TextView explanation = (TextView) view.findViewById(R.id.dialogExplanation);
        explanation.setText("Please enter a participant ID below");
        builder.setTitle("Information needed");
        final EditText id = (EditText) view.findViewById(R.id.IDparticipant);
        final TextView errorText = view.findViewById(R.id.dialogErrorText);

        Button continueButton = view.findViewById(R.id.dialogOkButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textId = id.getText().toString();
                Log.d(TAG, textId);
                if (isValid(textId)) {
                    ID = textId;
                    startRun(textId);
                    startRunDialog.dismiss();
                } else {
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Invalid ID, please enter a single word without special characters");
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private AlertDialog buildStopRunDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Finish Experience")
                .setMessage("Are you sure you want to end this experience?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishRun();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        return builder.create();
    }

    // ~~~~~~~~~~~~ UI functions  ~~~~~~~~~~~~~~~~~~~~~~~~

    protected void setFullScreen() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // ~~~~~~~~~~~~  gets and sets  ~~~~~~~~~~~~~~~~~~~~~~~~


    public int getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(int interactionType) {
        this.interactionType = interactionType;
    }

    public boolean isVibroIsForbidden() {
        return vibroIsForbidden;
    }

    public void setVibroIsForbidden(boolean vibroIsForbidden) {
        this.vibroIsForbidden = vibroIsForbidden;
    }

    public int getIndexInPatternToRepeat() {
        return indexInPatternToRepeat;
    }

    public void setIndexInPatternToRepeat(int indexInPatternToRepeat) {
        this.indexInPatternToRepeat = indexInPatternToRepeat;
    }

    public ClientTask getMyClientTask() {
        return myClientTask;
    }

    public void setMyClientTask(ClientTask myClientTask) {
        this.myClientTask = myClientTask;
    }

    public int getNumberOfCorrectionMade() {
        return numberOfCorrectionMade;
    }

    public void setNumberOfCorrectionMade(int numberOfCorrectionMade) {
        this.numberOfCorrectionMade = numberOfCorrectionMade;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }
}
