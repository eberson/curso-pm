package br.com.etecmatao.volante

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.content.IntentFilter
import android.nfc.Tag
import java.io.IOException
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.widget.Toast


class Bluetooth {
    companion object {
        const val Tag = "VOLANTE"

        const val DeviceName = "HC-05"

        // SPP UUID service - this should work for most devices
        private val BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        val adapter = BluetoothAdapter.getDefaultAdapter()

        var btConnected = false
        var btStream : ConnectedThread? = null

        /**
         * Broadcast Receiver for listing devices that are not yet paired
         * -Executed by btnDiscover() method.
         */
        val devicesFoundReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                Log.d(Tag, "devicesFoundReceiver: ACTION FOUND.")

                if (action == BluetoothDevice.ACTION_FOUND) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    Log.i(Tag, "devicesFoundReceiver: " + device!!.name + ": " + device.address)

                    if (device.name == DeviceName){
                        if (device.bondState != BluetoothDevice.BOND_BONDED){
                            if (device.createBond()){
                                Log.i(Tag, "devicesFoundReceiver: device pareado com sucesso")
                            }
                        }

                        btStream = ConnectedThread(createBluetoothSocket(device))
                        btConnected = true
                    }
                }
            }
        }


        /**
         * This method is required for all devices running API23+
         * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
         * in the manifest is not enough.
         *
         * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
         */
        fun checkBTPermissions(activity: AppCompatActivity) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                var permissionCheck =
                    activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
                permissionCheck += activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")

                if (permissionCheck != 0) {

                    activity.requestPermissions(
                        arrayOf(
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION
                        ), 1001
                    ) //Any number
                }
            } else {
                Log.d(
                    Tag,
                    "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP."
                )
            }
        }

        @Throws(IOException::class)
        private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
            //creates secure outgoing connecetion with BT device using UUID
            return device.createRfcommSocketToServiceRecord(BTMODULEUUID)
        }
    }
}