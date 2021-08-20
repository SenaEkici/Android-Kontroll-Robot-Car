package com.example.lenovo.led1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;


import java.io.IOException;

import android.os.AsyncTask;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.UUID;

public class ledKontrol extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    Button btnackapa, btndiss, sag, sol, ileri, geri, stop;
    private ProgressDialog progress;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean baglimi = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_kontrol);
        btnackapa = (Button) findViewById(R.id.btnackapa);
        btndiss = (Button) findViewById(R.id.btnDiss);


        sag = (Button) findViewById(R.id.btnsag);
        sol = (Button) findViewById(R.id.btnsol);
        ileri = (Button) findViewById(R.id.btnileri);
        geri = (Button) findViewById(R.id.btngeri);
        stop = (Button) findViewById(R.id.btndur);

        btndiss.setOnClickListener(this);
        btnackapa.setOnClickListener(this);
        /*ileri.setOnClickListener(this);
        geri.setOnClickListener(this);
        sag.setOnClickListener(this);
        sol.setOnClickListener(this);*/
        stop.setOnClickListener(this);
        ileri.setOnTouchListener(this);
        geri.setOnTouchListener(this);
        sag.setOnTouchListener(this);
        sol.setOnTouchListener(this);


        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        new BTbaglan().execute();

    }
// seri haberleşme ile veri gonderme
    private void sendData(String message) {

        byte[] msg1 = message.getBytes();
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(msg1);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }
//ledi yakmak için kullandığımız fonk.Fonksiyonda pice A bilgisi gönderilir.
    private void LedYak() {
        sendData("A");

    }
//ledi söndürmek için kullandığımız fonk.Ledi sondurmek için pice B bilgisi gonderilir.
    private void LedSondur() {
        sendData("B");
    }
//arabanın ileri gitmasi için pice C bilgisi gönderildi.
    private void ileri() {
        sendData("C");
    }
//arabanın durabilmesi için 5 kere D bilgisinin gitmesi gerekir.
    private void dur() {
        sendData("D");
        sendData("D");
        sendData("D");
        sendData("D");
        sendData("D");

    }
//arabanın sola gitmesi için ise 4 kere E gitmesi gerekir.
    private void sol() {
        sendData("E");
        sendData("E");
        sendData("E");
        sendData("E");

    }
//saga gitmesiiçin ise 3 kere f bilgisinin gönderilmesi gerekir.
    private void sag() {
        sendData("F");
        sendData("F");
        sendData("F");

    }
 //arabanın geri gidebilmesi için pice 2 kere G bilgisinin gönderilmesi gerekir.
    private void geri() {
        sendData("G");
        sendData("G");

    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnackapa)) {
            TextView text = (TextView) findViewById(R.id.textView);
            if (text.getText().toString().equals("LED YANDI")) {
                btnackapa.setBackgroundResource(R.drawable.ledsondur);
                text.setText("LED SONDU");
                LedSondur();
            } else {
                LedYak();
                btnackapa.setBackgroundResource(R.drawable.ledyak);  //Replace otherimage with proper drawable id
                text.setText("LED YANDI");
            }
        }

        if(view.equals(stop)){
            dur();
        }

        if (view.equals(btndiss)) {
            Disconnect();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.equals(ileri)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ileri();
                    break;
                case MotionEvent.ACTION_UP:
                    dur();
                    break;
            }
        }

        if(view.equals(geri)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    geri();
                    break;
                case MotionEvent.ACTION_UP:
                    dur();
                    break;
            }
        }
        if(view.equals(sag)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sag();
                    break;
                case MotionEvent.ACTION_UP:
                    dur();
                    break;
            }
        }
        if(view.equals(sol)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sol();
                    break;
                case MotionEvent.ACTION_UP:
                    dur();
                    break;
            }
        }
        return true;
    }


    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledKontrol.this, "Bağlanıyor....", "Lütfen Bekleyiniz");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (btSocket == null || !baglimi) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("baglantı hatası var");
                finish();
            } else {
                msg("baglantı basarılı");
                baglimi = true;
            }
            progress.dismiss();
        }
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    @Override//geriye basıldıgında baglantıyı sonlandır.
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }

    //Hata mesajı
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}



