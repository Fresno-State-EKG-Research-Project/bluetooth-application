package com.example.wearabletechble;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener
{
    //Initialize and define data members
    private final static String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;

    private Button btn_Scan;

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 7500, -75);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);

        btn_Scan = (Button) findViewById(R.id.btn_scan);
        findViewById(R.id.btn_scan).setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopScan();//stop the scanner
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();//Stop the scanner
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Check which request we are responding to
        if(requestCode == REQUEST_ENABLE_BT)
        {
            //Make sure request was successful
            if(resultCode == RESULT_OK)
            {
                //Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }

    //Called when an item in ListView is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //to be added
    }

    //Called when the scan button is clicked
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_scan:
                Utils.toast(getApplicationContext(), "Scan Button Pressed");
                if(!mBTLeScanner.isScanning())//if the scanner is not already scanning this begin scanning
                {
                    startScan();//Begin the scanner
                }
                else
                {
                    stopScan();//stop the scanner
                }
                break;
            default:
                break;
        }
    }


    //Add a new device to the lists or update its signal strength
    public void addDevice(BluetoothDevice device, int new_rssi)
    {
        String address = device.getAddress();//get device MAC address

        if(!mBTDevicesHashMap.containsKey(address))//check that device does not already exist in list
        {
            BTLE_Device btle_device = new BTLE_Device(device);//create new object for newly found device
            btle_device.setRSSI(new_rssi);//store its signal strength

            mBTDevicesHashMap.put(address, btle_device);//add to hashmap
            mBTDevicesArrayList.add(btle_device);//add device list
        }
        else
        {
            mBTDevicesHashMap.get(address).setRSSI(new_rssi);//update the signal strength if object already in list
        }
        adapter.notifyDataSetChanged();//Notify the list adapter that the data has changed
    }


    //Begin scanning for devices
    public void startScan()
    {
        btn_Scan.setText("Scanning...");//Update the GUI button text to indicate a scan is happening

        mBTDevicesArrayList.clear();//Initialize array list by removing garbage values
        mBTDevicesHashMap.clear();//Initialize hashmap by removing garbage values

        adapter.notifyDataSetChanged();//Notify the adapter that data has changed after the clear
        mBTLeScanner.start();//Start the scanner
    }

    //Stop scanning for devices
    public void stopScan()
    {
        btn_Scan.setText("Scan Again");//Change button text to indicate another scan if pressed
        mBTLeScanner.stop();//Stop the scanner
    }
}
