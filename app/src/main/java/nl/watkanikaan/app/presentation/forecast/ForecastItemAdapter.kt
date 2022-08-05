package nl.watkanikaan.app.presentation.forecast

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import nl.watkanikaan.app.R
import com.google.android.material.R as AndroidR
import nl.watkanikaan.app.databinding.WeatherItemBinding
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.presentation.getThemeColor
import kotlin.math.roundToInt

class ForecastItemAdapter(
    private var selectedPosition: Int,
    private val weatherData: MutableList<Weather.Forecast> = mutableListOf(),
    private val listener: (Weather.Forecast, Int) -> Unit
) : RecyclerView.Adapter<ForecastItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherData[position]
        holder.bind(weather)
    }

    override fun getItemCount() = weatherData.size

    fun updateItems(weatherItems: List<Weather.Forecast>) {
        weatherData.clear()
        weatherData.addAll(weatherItems)
        notifyItemRangeChanged(0, weatherItems.size)
    }

    inner class ViewHolder(
        private val binding: WeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var forecast: Weather.Forecast

        init {
            binding.card.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                setSelected(adapterPosition)
                listener(forecast, adapterPosition)
            }
        }

        fun bind(forecast: Weather.Forecast) {
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
            binding.overline.text = context.getText(forecast.day.toText())
            binding.underline.text = context.getString(R.string.temperature, forecast.windChillTemp.roundToInt().toString())

            val cardColor: Int
            val contentColor: Int
            val strokeWidth: Int
            if (selectedPosition == adapterPosition) {
                cardColor = context.getThemeColor(AndroidR.attr.colorPrimaryContainer)
                contentColor = context.getThemeColor(AndroidR.attr.colorOnPrimaryContainer)
                strokeWidth = 0
            } else {
                cardColor = context.getThemeColor(AndroidR.attr.colorSurface)
                contentColor = context.getThemeColor(AndroidR.attr.colorOnSurface)
                strokeWidth = context.resources.getDimensionPixelSize(R.dimen.card_stroke_width)
            }

            binding.card.setCardBackgroundColor(cardColor)
            binding.card.strokeWidth = strokeWidth
            binding.overline.setTextColor(contentColor)
            binding.icon.setColorFilter(contentColor)
            binding.underline.setTextColor(contentColor)
        }
    }

    private fun setSelected(selected: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = selected
        notifyItemChanged(selectedPosition)
    }
}
