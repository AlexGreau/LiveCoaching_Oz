package com.example.livecoaching_oz.Communication;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class ClientTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    String msgToServer;

    Decoder activity;

    private final int PORT = 8080;
    private final String SERVER_IP = "192.168.43.1"; //"192.168.43.239";

    public ClientTask(String msgTo, Decoder act) {
        dstAddress = SERVER_IP;
        dstPort = PORT;
        msgToServer = msgTo;
        this.activity = act;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            if (msgToServer != null) {
                Date date = new Date();
                msgToServer = msgToServer + "_" + date.getTime();
                dataOutputStream.writeUTF(msgToServer);
                System.out.println("sent : " + msgToServer);
            }

            response = dataInputStream.readUTF();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
            activity.errorMessage("Sorry, could not connect, try again");
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
            activity.errorMessage("Sorry, could not connect, try again");
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    activity.errorMessage("Sorry, could not connect, try again");
                    e.printStackTrace();
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    activity.errorMessage("Sorry, could not connect, try again");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        activity.decodeResponse(response);
    }
}
