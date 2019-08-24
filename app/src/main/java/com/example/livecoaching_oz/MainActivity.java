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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.livecoaching_oz.Communication.ClientTask;
import com.example.livecoaching_oz.Communication.Decoder;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements Decoder {

    // finals
    private final String TAG = MainActivity.class.getSimpleName();
    private final String goRightOrder = "Right";
    private final String goLeftOrder = "Left";
    private final String goStraightOrder = "Straight";
    private final String finishOrder = "Finish";
    private final String startOrder = "Start";
    private final String checkpointReachedOrder = "CP";

    // UI components
    private Button startButton;
    private Button finishButton;
    private Button leftButton;
    private Button rightButton;
    private Button straightButton;
    private Button checkPointButton;
    protected AlertDialog startRunDialog;

    // Communication
    private ClientTask myClientTask;

    // Logic Values
    private long startTime;
    private long finishTime;
    private int numberOfCorrectionMade;
    private String ID;
    private String Order;

    // Logger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
        setFullScreen();
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
        Date date = new Date();
        startTime = date.getTime();
        finishTime = date.getTime();
    }

    private void initUI() {
        initStartButton();
        initFinishButton();
        initLeftButton();
        initRightButton();
        initStraightButton();
        initCheckPointButton();
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
        rightButton = findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goRightOrder);
            }
        });
    }

    private void initLeftButton(){
        leftButton = findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goLeftOrder);
            }
        });
    }

    private void initStraightButton(){
        straightButton = findViewById(R.id.straightButton);
        straightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goStraightOrder);
            }
        });
    }

    private void initCheckPointButton(){
        checkPointButton = findViewById(R.id.checkPointButton);
        checkPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(checkpointReachedOrder);
            }
        });
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
        initValues();
        sendOrder(startOrder);
        // update UI
    }

    private void finishRun(){
        Date date = new Date();
        finishTime = date.getTime();
        // log Values
        initValues();
        sendOrder(finishOrder);
        // update UI
    }

    private void sendOrder(String order){
        Log.d(TAG, "sending order : " + order);
        myClientTask = new ClientTask(order, this);
        myClientTask.execute();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setFullScreen();
        }
    }
    // ~~~~~~~~~~~~  lifecycle functions  ~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopping...");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // ~~~~~~~~~~~~  gets and sets  ~~~~~~~~~~~~~~~~~~~~~~~~

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
