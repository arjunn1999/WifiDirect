package com.example.wifidirect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView connectionStatus,messageTextView;
    Button aSwitch,discoverButton,sendButton;
    ListView listView;
    EditText typeMsg;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter  intentFilter;
    List<WifiP2pDevice> peers =  new ArrayList<WifiP2pDevice>();
    String[] deviceNames;
    String deviceIp;
    WifiP2pDevice[] devices;
    WifiP2pManager.PeerListListener listener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        if(!wifiP2pDeviceList.equals(peers))
        {
            peers.clear();
            peers.addAll(wifiP2pDeviceList.getDeviceList());
            deviceNames = new String[wifiP2pDeviceList.getDeviceList().size()];
            devices = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
            int index=0;
            for(WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
                deviceNames[index]=device.deviceName;
                devices [index] = device;

            }
            ArrayAdapter <String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,deviceNames);
            listView.setAdapter(adapter);
        }
        if(peers.size()==0){
            connectionStatus.setText("No device found");
            return;
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();
        exqListener();
    }

    private void exqListener() {
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(i,1);
            }
        });
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                        connectionStatus.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                    }

                    @Override
                    public void onFailure(int i) {
                        //connectionStatus.setText("Discovery Failed");
                        connectionStatus.setTextColor(getResources().getColor(R.color.red));
                        if(WifiP2pManager.P2P_UNSUPPORTED==i){
                            connectionStatus.setText("Discovery Failed P2P unsupported");
                        }
                        if(WifiP2pManager.BUSY==i){
                            connectionStatus.setText("Discovery Failed Busy");
                        }
                        if(WifiP2pManager.ERROR==i){
                            connectionStatus.setText("Discovery Failed due to internal error");
                        }

                    }
                });
            }
        });
    }

    private void initialWork() {
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        messageTextView = (TextView) findViewById(R.id.readMsg);
        aSwitch = (Button)  findViewById(R.id.onOff);
        discoverButton=(Button) findViewById(R.id.discover);
        sendButton = (Button)  findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        typeMsg = (EditText)  findViewById(R.id.writeMsg);
        manager = (WifiP2pManager)  getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this,getMainLooper(),null);
        receiver = new WiFiDirectBroadcastReceiver(manager,channel,this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,intentFilter);

    }
}