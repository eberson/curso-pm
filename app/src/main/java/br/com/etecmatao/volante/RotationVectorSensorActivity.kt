package br.com.etecmatao.volante

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.content.Intent





class RotationVectorSensorActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation_vector_sensor)

        val manager:SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setUpRotationVectorSensor(manager)

        Bluetooth.checkBTPermissions(this)
        checkBTState(Bluetooth.adapter)
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private fun checkBTState(adapter: BluetoothAdapter) {

        if (adapter == null) {
            Log.i(Bluetooth.Tag, "Device does not support bluetooth")
            return
        }

        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1010)
            return
        }

        findBTDevices()
    }

    private fun findBTDevices(){
        val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(Bluetooth.devicesFoundReceiver, discoverDevicesIntent)

        Bluetooth.adapter.startDiscovery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1010 && resultCode == Activity.RESULT_OK){
            Log.i(Bluetooth.Tag, "Temos bluetooth habilitado no aparelho")

            findBTDevices()
        }
    }


    private fun setUpRotationVectorSensor(manager:SensorManager){
        val rvs = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        manager.registerListener(rotationVectorListener(), rvs, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun goLeft(){
        if (Bluetooth.btConnected){
            Bluetooth.btStream?.write("0")
        }

    }

    private fun goRight(){
        if (Bluetooth.btConnected){
            Bluetooth.btStream?.write("1")
        }
    }

    private fun rotationVectorListener(): SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            //https://code.tutsplus.com/tutorials/android-sensors-in-depth-proximity-and-gyroscope--cms-28084

            val rotationMatrix = FloatArray(16)

            SensorManager.getRotationMatrixFromVector(
                rotationMatrix, event!!.values
            )

            // Remap coordinate system
            val remappedRotationMatrix = FloatArray(16)

            SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix
            )

            // Convert to orientations
            val orientations = FloatArray(3)
            SensorManager.getOrientation(remappedRotationMatrix, orientations)

            for (i in 0..2) {
                orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
            }

            var rotacao = orientations[2]

//            Log.i("VOLANTE", rotacao.toString())


            when {
                rotacao > -70 && rotacao < 0 -> {
                    window.decorView.setBackgroundColor(Color.YELLOW)
                    goRight()
                }

                rotacao < -110 -> {
                    window.decorView.setBackgroundColor(Color.BLUE)
                    goLeft()
                }

                rotacao < -70 && rotacao > -110 -> window.decorView.setBackgroundColor(Color.WHITE)

                else -> {
                    window.decorView.setBackgroundColor(Color.BLACK)
                }
            }
        }

    }
}
