package com.example.livecoaching_oz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.livecoaching_oz.Communication.ClientTask;
import com.example.livecoaching_oz.Communication.Decoder;
import com.example.livecoaching_oz.Logs.Logger;

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
    private final int hapticCode = -1;
    private final int visualCode = 1;
    private final int bothCode = 0;
    private final String separator = ";";

    // UI components
    private Button startButton;
    private Button finishButton;
    private Button leftButton;
    private Button rightButton;
    private Button straightButton;
    private Button checkPointButton;
    protected AlertDialog startRunDialog;
    private Switch hapticSwitch;
    private Switch visualSwitch;
    private Switch testModeSwitch;
    private Chronometer chronometer;

    // Communication
    private ClientTask myClientTask;

    // Logic Values
    private long startTime;
    private long finishTime;
    private long totalTime;
    private String ID;
    private String Order;
    private int interactionType;
    private int trialNumber;
    private int numberOfCorrectionMade;
    private int totalSuccess;

    // Flags
    private boolean isHapticRequested;
    private boolean isVisualRequested;
    private boolean isTestMode;

    // Logger
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
        setFullScreen();
        System.out.println(TAG);
    }

    // ~~~~~~~~~~~~  inits  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void init() {
        initValues();
        initUI();
        initCommunication();
        initLogger();
    }

    private void initValues() {
        ID = "";
        Order = finishOrder;
        Date date = new Date();
        startTime = date.getTime();
        finishTime = date.getTime();
        totalTime = 0;
        numberOfCorrectionMade = 0;
        totalSuccess = 0;
        isVisualRequested = true;
        isHapticRequested = true;
        isTestMode = false;
        determineInteraction();
    }

    private void initUI() {
        findViewById(R.id.startBar).setVisibility(View.VISIBLE);
        findViewById(R.id.runningBar).setVisibility(View.GONE);
        initStartButton();
        initFinishButton();
        initLeftButton();
        initRightButton();
        initStraightButton();
        initCheckPointButton();
        initHapticSwitch();
        initVisualSwitch();
        initTestModeSwitch();
        initChrono();
        updateUI(false);
    }

    private void initCommunication() {
        myClientTask = new ClientTask("hey !", this);
    }

    private void initLogger() {
        logger = new Logger(this);

    }

    private void initChrono() {
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void initStartButton() {
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTestMode) {
                    startRun();
                } else {
                    startRunDialog = buildStartDialog();
                    startRunDialog.show();
                }
            }
        });
    }

    private void initFinishButton() {
        finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTestMode) {
                    finishRun();
                } else {
                    buildStopRunDialog().show();
                }
            }
        });

    }

    private void initRightButton() {
        rightButton = findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goRightOrder);
            }
        });
    }

    private void initLeftButton() {
        leftButton = findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goLeftOrder);
            }
        });
    }

    private void initStraightButton() {
        straightButton = findViewById(R.id.straightButton);
        straightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(goStraightOrder);
            }
        });
    }

    private void initCheckPointButton() {
        checkPointButton = findViewById(R.id.checkPointButton);
        checkPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(checkpointReachedOrder);
            }
        });
    }

    public void initHapticSwitch() {
        hapticSwitch = findViewById(R.id.hapticSwitch);
        hapticSwitch.setChecked(isHapticRequested);
        hapticSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHapticRequested = !isHapticRequested;
            }
        });
    }

    public void initVisualSwitch() {
        visualSwitch = findViewById(R.id.visualSwitch);
        visualSwitch.setChecked(isVisualRequested);
        visualSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisualRequested = !isVisualRequested;
            }
        });
    }

    private void initTestModeSwitch() {
        testModeSwitch = findViewById(R.id.testModeSwitch);
        testModeSwitch.setChecked(isTestMode);
        testModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTestMode = testModeSwitch.isChecked();
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

    private void startRun() {
        initValues();
        sendOrder(startOrder);
        Date date = new Date();
        startTime = date.getTime();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        updateUI(true);
    }

    private void finishRun() {
        Date date = new Date();
        finishTime = date.getTime();
        totalTime = finishTime - startTime;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        if (!isTestMode) {
            logger.writeSimpleLog(ID, getInteractionTypeString(interactionType), trialNumber, numberOfCorrectionMade, totalSuccess, totalTime);
        }
        sendOrder(finishOrder);

        initValues();
        updateUI(false);
    }

    private void sendOrder(String order) {
        Log.d(TAG, "sending order : " + order);
        determineInteraction();
        String message = interactionType + separator + order;
        myClientTask = new ClientTask(message, this);
        myClientTask.execute();
    }

    protected boolean isValid(String text) {
        Pattern pattern = Pattern.compile("\\w+?");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }


    protected boolean isInt(String i) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(i);
        return matcher.matches();
    }

    private void determineInteraction() {
        if (!isHapticRequested && !isVisualRequested) {
            hapticSwitch.performClick();
            visualSwitch.performClick();
            return;
        } else if (isHapticRequested && isVisualRequested) {
            // both
            interactionType = bothCode;
        } else if (isVisualRequested && !isHapticRequested) {
            // only visual
            interactionType = visualCode;

        } else if (isHapticRequested && !isVisualRequested) {
            // only haptic
            interactionType = hapticCode;
        }
    }

    private String getInteractionTypeString(int i) {
        String res = "";
        if (i == bothCode) {
            res = "both";
        } else if (i == hapticCode) {
            res = "haptic";
        } else if (i == visualCode) {
            res = "visual";
        } else {
            res = "invalid";
        }

        return res;
    }

    private double calculateSuccessRate() {
        double nAttempts = numberOfCorrectionMade;
        double nSuccess = totalSuccess;
        if (numberOfCorrectionMade == 0) {
            return 0;
        } else {
            return nSuccess / nAttempts;
        }
    }

    // ~~~~~~~~~~~~  dialog Builder functions  ~~~~~~~~~~~~~~~

    private AlertDialog buildStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_start, null);

        TextView explanation = (TextView) view.findViewById(R.id.dialogExplanation);
        explanation.setText("Please enter a participant ID below");
        builder.setTitle("Information needed");
        final EditText id = (EditText) view.findViewById(R.id.IDparticipant);
        final TextView errorText = view.findViewById(R.id.dialogErrorText);
        final EditText trialNumberPicker = view.findViewById(R.id.trialNumberPicker);

        Button continueButton = view.findViewById(R.id.dialogOkButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textId = id.getText().toString();
                String trialN = trialNumberPicker.getText().toString();
                Log.d(TAG, textId);
                if (isValid(textId) && isInt(trialN)) {
                    ID = textId;
                    trialNumber = Integer.parseInt(trialN);
                    startRun();
                    startRunDialog.dismiss();
                } else {
                    String error = "";
                    if (!isValid(textId)) {
                        error = "Invalid ID, please enter a single word without special characters";
                    } else if (!isInt(trialN)) {
                        error = "Please enter a valid number";
                    } else {
                        error = "please enter a single word without special characters and a valid number";
                    }
                    errorText.setTextColor(Color.RED);
                    errorText.setText(error);
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private AlertDialog buildStopRunDialog() {
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

    private void updateUI(boolean isRunning) {
        if (isRunning) {
            findViewById(R.id.startBar).setVisibility(View.GONE);
            findViewById(R.id.runningBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.startBar).setVisibility(View.VISIBLE);
            findViewById(R.id.runningBar).setVisibility(View.GONE);
        }
        finishButton.setEnabled(isRunning);
        straightButton.setEnabled(isRunning);
        rightButton.setEnabled(isRunning);
        leftButton.setEnabled(isRunning);
        checkPointButton.setEnabled(isRunning);
        startButton.setEnabled(!isRunning);
    }

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

    public long getTotalTime() {
        return totalTime;
    }
}
