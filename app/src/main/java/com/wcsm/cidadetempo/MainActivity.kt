package com.wcsm.cidadetempo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.squareup.picasso.Picasso
import com.wcsm.cidadetempo.api.RetrofitService.weatherAPI
import com.wcsm.cidadetempo.databinding.ActivityMainBinding
import com.wcsm.cidadetempo.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private var getWeatherJob: Job? = null
    private var fillWeatherFieldsJob: Job? = null
    private var city: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            clearData()

            city = binding.cityInputText.text.toString()
            if (validateCity()) {
                hideKeyboard()
                getWeatherData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (validateCity()) {
            getWeatherData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getWeatherJob?.cancel()
        fillWeatherFieldsJob?.cancel()
    }

    private fun validateCity(): Boolean {
        binding.cityInputLayout.error = null
        if(city != null) {
            if (city!!.isEmpty()) {
                binding.cityInputLayout.error = "É necessário inserir uma cidade."
                loading(false)
                return false
            }
        } else {
            loading(false)
            return false
        }
        return true
    }

    private fun getWeatherData() {
        getWeatherJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                loading(true)
            }

            var response: Response<Weather>? = null
            try {
                response = weatherAPI.getWeatherData("$city")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val isValidResponse = response != null && response.isSuccessful
            if(isValidResponse) {
                binding.cityInputLayout.error = null
                val result = response?.body()
                fillWeatherFields(result)
            } else {
                withContext(Dispatchers.Main) {
                    binding.cityInputLayout.errorIconDrawable = null
                    binding.cityInputLayout.error = "Cidade indisponível no momento."
                    loading(false)
                }
            }
        }
    }

    private fun fillWeatherFields(result: Weather?) {
        fillWeatherFieldsJob = CoroutineScope(Dispatchers.IO).launch {
            if (result != null) {
                val city = result.name
                val countryFlag = result.sys.country // BR
                val countryFlagPath = "https://flagsapi.com/${countryFlag}/flat/64.png"

                val weatherDate = convertTimestampToDate(result.dt.toLong())
                val temperature = "Temperatura ${result.main.temp.toInt()} ºC"
                val maxTemperature = "Máxima ${result.main.temp_max.toInt()} ºC"
                val minTemperature = "Mínima ${result.main.temp_min.toInt()} ºC"
                val thermalSensation = "Sensação Térmica ${result.main.feels_like.toInt()} ºC"

                val weatherDescription = result.weather[0].description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                val descriptionImagePath =
                    "https://openweathermap.org/img/wn/${result.weather[0].icon}.png"

                val humidity = "Umidade ${result.main.humidity}%"
                val windSpeed = "Vento ${result.wind.speed}km/h"

                val countryFlagDeferred = async { Picasso.get().load(countryFlagPath).get() }
                val descriptionImageDeferred = async { Picasso.get().load(descriptionImagePath).get() }
                val countryFlagBitmap = countryFlagDeferred.await()
                val descriptionImageBitmap = descriptionImageDeferred.await()

                withContext(Dispatchers.Main) {
                    with(binding) {
                        ivCountryFlag.setImageBitmap(countryFlagBitmap)
                        ivCloud.setImageBitmap(descriptionImageBitmap)

                        tvCity.text = city

                        tvDateTime.text = weatherDate
                        tvTemp.text = temperature
                        tvMinTemp.text = minTemperature
                        tvMaxTemp.text = maxTemperature
                        tvThermalSensation.text = thermalSensation

                        tvDescription.text = weatherDescription

                        tvHumidity.text = humidity
                        tvWind.text = windSpeed

                        showHideIcons(View.VISIBLE)
                        loading(false)
                    }
                }
            }
        }
    }

    private fun clearData() {
        with(binding) {
            tvCity.text = ""
            tvDateTime.text = ""
            tvTemp.text = ""
            tvMinTemp.text = ""
            tvMaxTemp.text = ""
            tvThermalSensation.text = ""
            tvDescription.text = ""
            tvHumidity.text = ""
            tvWind.text = ""
        }
        showHideIcons(View.INVISIBLE)
    }

    private fun showHideIcons(option: Int) {
        with(binding) {
            when(option) {
                View.INVISIBLE -> {
                    ivCountryFlag.visibility = View.INVISIBLE
                    ivCloud.visibility = View.INVISIBLE
                    tvCity.visibility = View.INVISIBLE
                    tvDateTime.visibility = View.INVISIBLE
                    view1.visibility = View.INVISIBLE
                    view2.visibility = View.INVISIBLE
                    view3.visibility = View.INVISIBLE
                }
                View.VISIBLE -> {
                    ivCountryFlag.visibility = View.VISIBLE
                    ivCloud.visibility = View.VISIBLE
                    tvCity.visibility = View.VISIBLE
                    tvDateTime.visibility = View.VISIBLE
                    view1.visibility = View.VISIBLE
                    view2.visibility = View.VISIBLE
                    view3.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if(view != null) {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun loading(isLoading: Boolean) {
        if(isLoading) {
            binding.btnSearch.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnSearch.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert to milliseconds
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}