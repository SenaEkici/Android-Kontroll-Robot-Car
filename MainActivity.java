package com.example.lenovo.led1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
      private BluetoothAdapter mybluetooth=null;
      private Set<BluetoothDevice> eslenmisCihazlar;
      Button cihazlarigoster;
      ListView  cihazlistesi;

     public static String EXTRA_ADDRESS="device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cihazlarigoster=(Button) findViewById(R.id.btncihazlarigoster);
        cihazlarigoster.setOnClickListener(this);
        cihazlistesi=(ListView) findViewById(R.id.cihazlistesi);

        mybluetooth=BluetoothAdapter.getDefaultAdapter();
        if (mybluetooth==null)
        {
            Toast.makeText(getApplicationContext(),"Bluetooth Desteklenmiyor",Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(!mybluetooth.isEnabled())
        {
            Intent bluetoothac=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothac,1);
        }

    }

    @Override
    public void onClick(View view) {
        eslenmisCihazlariGoster();

    }

    private void eslenmisCihazlariGoster() {
        eslenmisCihazlar=mybluetooth.getBondedDevices();
        ArrayList list=new ArrayList();
        if(eslenmisCihazlar.size()>0)
        {
            for (BluetoothDevice bt :eslenmisCihazlar){
                list.add(bt.getName()+"\n"+bt.getAddress());
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "Eslemiş Cihaz Bulanamadı", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_activated_1,list);
        cihazlistesi.setAdapter(adapter);
        cihazlistesi.setOnItemClickListener(cihazSec);
    }
    private AdapterView.OnItemClickListener cihazSec=new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info=((TextView)view).getText().toString();//mac adresi alındı.
            String address=info.substring(info.length()-17);

           Intent git=new Intent(MainActivity.this,ledKontrol.class);
           git.putExtra(EXTRA_ADDRESS,address);
            startActivity(git);

        }


    };
}
