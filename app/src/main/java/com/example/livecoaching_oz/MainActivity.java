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
    private final String goUpLeftOrder = "Up Left";
    private final String goUpRightOrder = "Up Right";
    private final String goDownLeftOrder = "Down Left";
    private final String goDownRightOrder = "Down Right";
    private final String goDownOrder = "Down";
    private final String finishOrder = "Finish";
    private final String startOrder = "Start";
    private final String checkpointReachedOrder = "CheckPoint";
    private final int hapticCode = -1;
    private final int visualCode = 1;
    private final int bothCode = 0;
    private final String separator = ";";

    // UI components
    private Button startButton;
    private Button finishButton;
    private Button upLeftButton;
    private Button straightButton;
    private Button upRightButton;
    private Button leftButton;
    private Button rightButton;
    private Button downLeftButton;
    private Button downButton;
    private Button downRightButton;
    private Button checkPointButton;
    private Button failButton;
    private Button repeatOrderButton;
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
    private String order;
    private int orderNumber;
    private int interactionType;
    private int totalCheckpoints;
    private int totalOrdersSent;
    private int totalSuccess;
    private long timetookForOrder;
    private long startOfOrderTime;
    private long endOfOrderTime;

    // Flags
    private boolean isHapticRequested = true;
    private boolean isVisualRequested = true;
    private boolean isTestMode = false;

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
        order = finishOrder;
        Date date = new Date();
        startTime = date.getTime();
        finishTime = date.getTime();
        totalTime = 0;
        totalCheckpoints = 0;
        totalOrdersSent = 0;
        totalSuccess = 0;
        orderNumber = 0;
        determineInteraction();
    }

    private void initUI() {
        findViewById(R.id.startBar).setVisibility(View.VISIBLE);
        findViewById(R.id.runningBar).setVisibility(View.GONE);
        initStartButton();
        initFinishButton();
        initLeftButton();
        initRightButton();
        initUpLeftButton();
        initStraightButton();
        initUpRightButton();
        initDownLeftButton();
        initDownButton();
        initDownRightButton();
        initCheckPointButton();
        initRepeatOrderButton();
        initHapticSwitch();
        initVisualSwitch();
        initTestModeSwitch();
        initChrono();
        initSuccessFailButtons();
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
                updateTotalCheckPoints();
                order = goRightOrder;
                orderButtonClicked();
            }
        });
    }

    private void initLeftButton() {
        leftButton = findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goLeftOrder;
                orderButtonClicked();
            }
        });
    }

    private void initUpLeftButton() {
        upLeftButton = findViewById(R.id.upLeftButton);
        upLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goUpLeftOrder;
                orderButtonClicked();
            }
        });
    }

    private void initStraightButton() {
        straightButton = findViewById(R.id.straightButton);
        straightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goStraightOrder;
                orderButtonClicked();
            }
        });
    }

    private void initUpRightButton() {
        upRightButton = findViewById(R.id.upRightButton);
        upRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goUpRightOrder;
                orderButtonClicked();
            }
        });
    }

    private void initDownLeftButton() {
        downLeftButton = findViewById(R.id.downLeftButton);
        downLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goDownLeftOrder;
                orderButtonClicked();
            }
        });
    }

    private void initDownButton() {
        downButton = findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goDownOrder;
                orderButtonClicked();
            }
        });
    }

    private void initDownRightButton() {
        downRightButton = findViewById(R.id.downRightButton);
        downRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalCheckPoints();
                order = goDownRightOrder;
                orderButtonClicked();
            }
        });
    }

    private void initRepeatOrderButton() {
        repeatOrderButton = findViewById(R.id.avatar);
        repeatOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderButtonClicked();
                Date date = new Date();
                long timestamp = date.getTime();
                if (!isTestMode) {
                    logger.writeCompleteLog(ID, getInteractionTypeString(interactionType), totalCheckpoints, order, timestamp, "repetition");
                }
            }
        });
    }

    private void initCheckPointButton() {
        checkPointButton = findViewById(R.id.checkPointButton);
        checkPointButton.setText("Checkpoint");
        checkPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                endOfOrderTime = date.getTime();
                long timestamp = date.getTime();
                timetookForOrder = endOfOrderTime - startOfOrderTime;
                totalSuccess++;
                if (!isTestMode) {
                    logger.writeCompleteLog(ID, getInteractionTypeString(interactionType), totalCheckpoints, order, timestamp, "success");
                }
                order = checkpointReachedOrder;
                sendOrder(checkpointReachedOrder);
                enableSuccFailButtons(false);
                enableOrdersButtons(true);
            }
        });
    }

    private void initHapticSwitch() {
        hapticSwitch = findViewById(R.id.hapticSwitch);
        hapticSwitch.setChecked(isHapticRequested);
        hapticSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHapticRequested = !isHapticRequested;
                determineInteraction();
            }
        });
    }

    private void initVisualSwitch() {
        visualSwitch = findViewById(R.id.visualSwitch);
        visualSwitch.setChecked(isVisualRequested);
        visualSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisualRequested = !isVisualRequested;
                determineInteraction();
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

    private void initSuccessFailButtons() {
        failButton = findViewById(R.id.failButton);
        enableSuccFailButtons(false);

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                endOfOrderTime = date.getTime();
                long timestamp = date.getTime();
                timetookForOrder = endOfOrderTime - startOfOrderTime;
                if (!isTestMode) {
                    logger.writeCompleteLog(ID, getInteractionTypeString(interactionType), totalCheckpoints, order, timestamp, "fail");
                }
                enableSuccFailButtons(false);
                enableOrdersButtons(true);
            }
        });
    }

    // ~~~~~~~~~~~~ Decoder methods  ~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void decodeResponse(String rep) {
        Log.d(TAG, "rep : " + rep);
        if (!rep.equals("starting") && !rep.equals("cp")) {
            System.out.println("yay");
            enableSuccFailButtons(true);
        }
        Date date = new Date();
        startOfOrderTime = date.getTime();
    }

    @Override
    public void errorMessage(String err) {
        Log.e(TAG, err);
    }

    // ~~~~~~~~~~~~  Logic functions  ~~~~~~~~~~~~~~~~~~~~~~~~

    private void startRun() {
        initValues();
        checkPointButton.setText("Checkpoint");
        order = startOrder;
        sendOrder(startOrder);
        Date date = new Date();
        startTime = date.getTime();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        updateUI(true);
        enableOrdersButtons(true);
        enableSuccFailButtons(false);
    }

    private void finishRun() {
        Date date = new Date();
        finishTime = date.getTime();
        totalTime = finishTime - startTime;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        if (!isTestMode) {
            logger.writeSimpleLog(ID, getInteractionTypeString(interactionType), totalCheckpoints, totalOrdersSent, totalSuccess, totalTime);
        }
        sendOrder(finishOrder);

        initValues();
        updateUI(false);
    }

    private void sendOrder(String order) {
        Log.d(TAG, "sending order : " + order + ";" + interactionType);
        determineInteraction();
        if (!order.equals(startOrder) && !order.equals(finishOrder) && !order.equals(checkpointReachedOrder)) {
            totalOrdersSent++;
        }
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
        double nAttempts = totalOrdersSent;
        double nSuccess = totalSuccess;
        if (totalOrdersSent == 0) {
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

        Button continueButton = view.findViewById(R.id.dialogOkButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textId = id.getText().toString();
                Log.d(TAG, textId);
                if (isValid(textId)) {
                    ID = textId;
                    startRun();
                    startRunDialog.dismiss();
                } else {
                    String error = "";
                    if (!isValid(textId)) {
                        error = "Invalid ID, please enter a single word without special characters";
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
                        order = finishOrder;
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
        enableOrdersButtons(isRunning);
        enableSuccFailButtons(!isRunning);
        startButton.setEnabled(!isRunning);
    }

    private void enableSuccFailButtons(boolean enable) {
        repeatOrderButton.setEnabled(enable);
        checkPointButton.setEnabled(enable);
        failButton.setEnabled(enable);
    }

    private void enableOrdersButtons(boolean enable) {
        finishButton.setEnabled(enable);
        straightButton.setEnabled(enable);
        rightButton.setEnabled(enable);
        leftButton.setEnabled(enable);
        upLeftButton.setEnabled(enable);
        upRightButton.setEnabled(enable);
        downLeftButton.setEnabled(enable);
        downButton.setEnabled(enable);
        downRightButton.setEnabled(enable);
    }

    private void orderButtonClicked() {
        enableSuccFailButtons(true);
        enableOrdersButtons(false);
        sendOrder(order);
    }

    private void updateTotalCheckPoints() {
        totalCheckpoints++;
        checkPointButton.setText("CheckPoint # " + totalCheckpoints);
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

    public int getTotalOrdersSent() {
        return totalOrdersSent;
    }

    public void setTotalOrdersSent(int totalOrdersSent) {
        this.totalOrdersSent = totalOrdersSent;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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
