package br.com.etecmatao.volante

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.math.abs

class RotationVectorSensorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation_vector_sensor)

        val manager:SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setUpRotationVectorSensor(manager)
    }

    private fun setUpRotationVectorSensor(manager:SensorManager){
        val rvs = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        manager.registerListener(rotationVectorListener(), rvs, SensorManager.SENSOR_DELAY_NORMAL)
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

            when {
                orientations[2] > 45 -> window.decorView.setBackgroundColor(Color.YELLOW)
                orientations[2] < -45 -> window.decorView.setBackgroundColor(Color.BLUE)
                abs(orientations[2]) < 10 -> window.decorView.setBackgroundColor(Color.WHITE)
            }
        }

    }
}
