package nl.watkanikaan.app.ui

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.WeatherItemBinding
import nl.watkanikaan.app.domain.model.Weather
import kotlin.math.roundToInt

class WeatherItemAdapter(
    private val weatherData: MutableMap<Weather.Day, Weather.Forecast> = mutableMapOf(),
    private val listener: (Weather.Day, Weather.Forecast) -> Unit
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {
    private var selectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = Weather.Day.values()[position]
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

        fun bind(day: Weather.Day, forecast: Weather.Forecast) {
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

            val tempString = forecast.windChillTemp.roundToInt().toString()
            binding.underline.text = context.getString(R.string.temperature, tempString)
            binding.card.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                notifyItemChanged(selectedPos)
                selectedPos = adapterPosition
                notifyItemChanged(selectedPos)

                listener(day, forecast)
            }

            val cardColor: Int
            val contentColor: Int

            if (selectedPos == adapterPosition) {
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
}
