package nl.watkanikaan.app.ui

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.WeatherItemBinding
import nl.watkanikaan.app.domain.model.Weather
import kotlin.math.roundToInt

class WeatherItemAdapter(
    private var selectedDay: Weather.Day,
    private val weatherData: MutableMap<Weather.Day, Weather.Forecast> = mutableMapOf(),
    private val listener: (Weather.Day, Weather.Forecast) -> Unit
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = selectedDay.findDay(position) ?: return
        val weather = weatherData[day] ?: return
        holder.bind(day, weather)
    }

    override fun getItemCount() = weatherData.size

    fun updateItems(weatherItems: Map<Weather.Day, Weather.Forecast>) {
        weatherData.clear()
        weatherData.putAll(weatherItems)
        notifyItemRangeChanged(0, weatherItems.size)
    }

    inner class ViewHolder(
        private val binding: WeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var day: Weather.Day
        private lateinit var forecast: Weather.Forecast

        init {
            binding.card.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                setSelected(selectedDay.findDay(adapterPosition))
                listener(day, forecast)
            }
        }

        fun bind(day: Weather.Day, forecast: Weather.Forecast) {
            this.day = day
            this.forecast = forecast

            val context = binding.root.context
            val drawable = try {
                val resourceId = context.resources.getIdentifier(
                    forecast.weatherIcon,
                    "drawable",
                    context.packageName
                )
                ResourcesCompat.getDrawable(context.resources, resourceId, null)
            } catch (e: Resources.NotFoundException) {
                ResourcesCompat.getDrawable(context.resources, R.drawable.refresh, null)
            }
            binding.icon.setImageDrawable(drawable)
            binding.overline.text = context.getText(day.toText(true))
            binding.underline.text = context.getString(R.string.temperature, forecast.windChillTemp.roundToInt().toString())

            val cardColor: Int
            val contentColor: Int
            if (selectedDay == day) {
                cardColor = context.getThemeColor(com.google.android.material.R.attr.colorPrimary)
                contentColor = context.getThemeColor(com.google.android.material.R.attr.colorOnPrimary)
            } else {
                cardColor = context.getThemeColor(com.google.android.material.R.attr.colorSurface)
                contentColor = context.getThemeColor(com.google.android.material.R.attr.colorOnSurface)
            }

            binding.card.setCardBackgroundColor(cardColor)
            binding.overline.setTextColor(contentColor)
            binding.icon.setColorFilter(contentColor)
            binding.underline.setTextColor(contentColor)
        }
    }

    private fun setSelected(day: Weather.Day?) {
        if (day == null) return

        notifyItemChanged(selectedDay.position)
        selectedDay = day
        notifyItemChanged(selectedDay.position)
    }
}
