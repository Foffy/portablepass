package com.example.password.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity{
    private Button On,Off,Visible,list;
    private BluetoothAdapter BA;
    private BluetoothDevice device;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
    private OutputStream outStream;
    private BluetoothSocket btSocket;
    private EditText editText;
    private static String arduinoAddress = "20:13:11:04:54:60";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.EditText);
        On = (Button)findViewById(R.id.button_on);
        Off = (Button)findViewById(R.id.button_OFF);
        lv = (ListView)findViewById(R.id.list_bt_view);

        BA = BluetoothAdapter.getDefaultAdapter();

    }

    public void turnOn(View view){
        if(!BA.isEnabled()){
            Intent on = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(on,0);
            Toast.makeText(getApplicationContext(), "Turned On",
                Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Already On",
                Toast.LENGTH_LONG).show();
        }
    }

    public void turnOff(View view){
        if(BA.isEnabled()){
            BA.disable();
            Toast.makeText(getApplicationContext(), "Turned Off",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Already Off",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void doText(View view){
        byte[] msgBuffer = editText.getText().toString().getBytes();
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Nope, son!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void pairArduino(View view){
        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(arduinoAddress);

        try{
            btSocket = device.createRfcommSocketToServiceRecord(uuid);
        }catch (IOException e) {
        }

        BA.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
        }

        Toast.makeText(getApplicationContext(), "Paired with Arduino!",
                Toast.LENGTH_LONG).show();

    }

    public void onPause() {
        super.onPause();

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
        }
    }
}