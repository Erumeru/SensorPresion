package com.example.sensorpresion

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.pow

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorValue: TextView
    private lateinit var altitudValue: TextView
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref=getApplicationContext().getSharedPreferences("altitud",0)
        editor=pref.edit()
        editor.putFloat("altitud_maxima",0f)
        editor.commit()
        sensorValue = findViewById(R.id.sensorValue)
        altitudValue = findViewById(R.id.altitudValue)

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
            if(altitudMaximaSuperada(calculateAltitude(pressureValue))){
                editor.putFloat("altitud_maxima", calculateAltitude(pressureValue))
                editor.commit()
            }
            altitudValue.text="Su altitud actual es: ${calculateAltitude(pressureValue)}   \n la altitud maxima fue: ${pref.getFloat("altitud_maxima",0f)}"
        }
    }

    fun altitudMaximaSuperada(altitud: Float): Boolean{
        if(pref.getFloat("altitud_maxima",0f) < altitud){ return true }
        else{
            return false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun calculateAltitude(pressure: Float): Float {
        val T0 = 288.15  // Temperatura estándar en Kelvin
        val L = 0.0065   // Gradiente de temperatura estándar (K/m)
        val P0 = 1013.25 // Presión atmosférica estándar al nivel del mar (hPa)
        val R = 287.05   // Constante de los gases ideales (J/(kg·K))
        val g = 9.80665  // Aceleración debida a la gravedad (m/s²)
        val M = 0.0289644 // Masa molar del aire (kg/mol)

        val altitude = (T0 / L) * (1 - (pressure / P0).pow((R * L) / (g * M)))
        return altitude.toFloat()
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