package br.com.etecmatao.volante

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
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




class Bluetooth {
    companion object {
        var mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        var CurrentDevice: BluetoothDevice? = null

        const val TAG = "VOLANTE"

        const val DeviceName = "TESTE"

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
                    TAG,
                    "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP."
                )
            }
        }

        /**
         * Broadcast Receiver that detects bond state changes (Pairing status changes)
         */
        val mBluetoothCheck = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    val mDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    //3 cases:
                    //case1: bonded already
                    if (mDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "mBluetoothCheck: BOND_BONDED.")
                    }

                    //case2: creating a bone
                    if (mDevice.bondState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "mBluetoothCheck: BOND_BONDING.")
                    }

                    //case3: breaking a bond
                    if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                        Log.d(TAG, "mBluetoothCheck: BOND_NONE.")
                    }
                }
            }
        }

        // Create a BroadcastReceiver for ACTION_FOUND
        val mBluetoothEnableCheck = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                // When discovery finds a device
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                    when (state) {
                        BluetoothAdapter.STATE_OFF -> Log.d(TAG, "onReceive: STATE OFF")
                        BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                            TAG,
                            "mBluetoothEnableCheck: STATE TURNING OFF"
                        )

                        BluetoothAdapter.STATE_ON -> {
                            Log.d(TAG, "mBluetoothEnableCheck: STATE ON")
                            val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
                            context.registerReceiver(mPesquisaDevicesReceiver, discoverDevicesIntent)

                            mBluetoothAdapter!!.startDiscovery()
                        }

                        BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                            TAG,
                            "mBluetoothEnableCheck: STATE TURNING ON"
                        )
                    }
                }
            }
        }

        /**
         * Broadcast Receiver for listing devices that are not yet paired
         * -Executed by btnDiscover() method.
         */
        private val mPesquisaDevicesReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                Log.d(TAG, "onReceive: ACTION FOUND.")

                if (action == BluetoothDevice.ACTION_FOUND) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    Log.d(TAG, "onReceive: " + device!!.name + ": " + device.address)

                    if (device!!.name == DeviceName){
                        mBluetoothAdapter!!.cancelDiscovery()
                        CurrentDevice = device
                    }
                }
            }
        }

        private class ConnectThread(device: BluetoothDevice) : Thread() {

            private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
                device.createRfcommSocketToServiceRecord(CurrentDevice?.name)
            }

            public override fun run() {
                // Cancel discovery because it otherwise slows down the connection.
                bluetoothAdapter?.cancelDiscovery()

                mmSocket?.use { socket ->
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    socket.connect()

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket)
                }
            }

            // Closes the client socket and causes the thread to finish.
            fun cancel() {
                try {
                    mmSocket?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Could not close the client socket", e)
                }
            }
        }
    }
}