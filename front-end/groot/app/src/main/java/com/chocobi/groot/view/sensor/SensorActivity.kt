package com.chocobi.groot.view.sensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chocobi.groot.R
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.sensor.model.SensorResponse
import com.chocobi.groot.view.sensor.model.SensorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class SensorActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = "SensorActivity"
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private lateinit var lightValueText: TextView
    private lateinit var envStatus: TextView
    private lateinit var envStatusInfo: TextView
    private var plantId: Int = 0
    private var plantName: String = ""
    private var maxLux : Int = 0
    private var minLux : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            this.onBackPressed()
        }

        plantId = intent.getIntExtra("plantId", 0)
        val plantName = intent.getStringExtra("plantName")
        val plantNameText = findViewById<TextView>(R.id.plantName)
        plantNameText.text = plantName
        lightValueText = findViewById(R.id.lightValueText)
        envStatus = findViewById(R.id.envStatus)
        envStatusInfo = findViewById(R.id.envStatusInfo)

        // 센서 매니저 인스턴스 생성
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var deviceSensors: List<Sensor> = sensorManager!!.getSensorList(Sensor.TYPE_ALL)

        for (i in deviceSensors) {
        Log.d(TAG, "deviceSensor: $i")
        }

        getPlantLux()


        // 조도 센서 인스턴스 생성
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        // 센서 등록
        sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
 }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
//        Log.d(
//            "Sensor",
//            "onSensorChanged(): sensorType=${event.sensor.type}, values=${event.values.joinToString()}"
//        )

        // 센서 타입 확인
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lightValue = event.values[0]
                lightValueText.text = lightValue.toString()

                if (minLux <= lightValue && lightValue <= maxLux) {
                    envStatus.text = "적합"
                    envStatusInfo.text = "이곳에 화분을 놓아주세요"
                    envStatus.setTextColor(ContextCompat.getColor(this, R.color.main))
                    envStatusInfo.setTextColor(ContextCompat.getColor(this, R.color.main))


                } else if (minLux > lightValue) {
                    envStatus.text = "부적합"
                    envStatusInfo.text = "식물을 키우기에 너무 어두워요"
                    envStatus.setTextColor(ContextCompat.getColor(this, R.color.bug))
                    envStatusInfo.setTextColor(ContextCompat.getColor(this, R.color.bug))
                } else {
                    envStatus.text = "부적합"
                    envStatusInfo.text = "식물을 키우기에 너무 밝아요"
                    envStatus.setTextColor(ContextCompat.getColor(this, R.color.bug))
                    envStatusInfo.setTextColor(ContextCompat.getColor(this, R.color.bug))
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 필요 없는 코드
    }

    override fun onPause() {
        super.onPause()

        // 센서 해제
        sensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // 센서 등록
        sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
 }

    private fun getPlantLux() {
        var retrofit = RetrofitClient.getClient()!!
        var sensorService = retrofit.create(SensorService::class.java)
        sensorService.getPlantLux(plantId).enqueue(object :Callback<SensorResponse> {
            override fun onResponse(
                call: Call<SensorResponse>,
                response: Response<SensorResponse>
            ) {
                if (response.code() == 200) {
                    val body = response.body()!!
                    maxLux = body.env.maxLux
                    minLux = body.env.minLux
                }
            }

            override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
