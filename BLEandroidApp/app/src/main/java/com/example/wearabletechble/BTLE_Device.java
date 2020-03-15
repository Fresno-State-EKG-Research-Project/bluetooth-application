package com.example.wearabletechble;

import android.bluetooth.BluetoothDevice;

public class BTLE_Device
{
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    //Instantiates the bluetooth device found into a variable
    public BTLE_Device(BluetoothDevice bluetoothDevice)
    {
        this.bluetoothDevice = bluetoothDevice;
    }


    //Returns the MAC address of the bluetooth device found
    public String getAddress()
    {
        return bluetoothDevice.getAddress();
    }

    //Returns the local name of the bluetooth device found
    public String getName()
    {
        return bluetoothDevice.getName();
    }

    //Stores the Received Signal Strength Indicator from device found
    public void setRSSI(int rssi)
    {
        this.rssi = rssi;
    }

    //Returns the RSSI from device found
    public int getRSSI()
    {
        return rssi;
    }
}
