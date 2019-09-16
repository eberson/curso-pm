package br.com.etecmatao.volante

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager:SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setUpGyroscope(manager)
    }

    private fun setUpGyroscope(manager: SensorManager){
        val gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        manager.registerListener(gyroscopeListener(), gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun gyroscopeListener():SensorEventListener{
        return object : SensorEventListener{
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                var color = when {
                    event!!.values[2] > 0.5f -> // anticlockwise
                        Color.BLUE
                    event.values[2] < -0.5f -> // clockwise
                        Color.YELLOW
                    else -> Color.WHITE
                }

                window.decorView.setBackgroundColor(color)
            }
        }
    }
}
