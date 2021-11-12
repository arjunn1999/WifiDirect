package com.example.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

public class WiFiDirectBroadcastReceiver  extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel  channel;
    private  MainActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
                //Check for wifi state change
        }
        else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
                manager.requestPeers(channel,this.activity.listener);
        }
        else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){

        }
    }
}
