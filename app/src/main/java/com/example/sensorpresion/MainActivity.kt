package com.example.sensorpresion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorValue: TextView
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorValue = findViewById(R.id.sensorValue)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        if (pressureSensor == null) {
            sensorValue.text = "Pressure sensor not available on this device."
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PRESSURE) {
            val pressureValue = event.values[0]
            sensorValue.text = "Presión: $pressureValue hPa"
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }
}