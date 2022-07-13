package com.nfc_read_write.nfc_read_write_plugin_example;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {
    protected String[][] techList;
    protected IntentFilter[] intentFilters;
    protected PendingIntent pendingIntent;
    @Override
    protected void onResume() {


        super.onResume();
        techList = new String[][] { new String[] { NfcV.class.getName() },
                new String[] { NfcA.class.getName() } };
        intentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED), };
        // 创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFilters, techList);

    }

    @Override
    protected void onPause() {
        super.onPause();

        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFilters, techList);
    }

}