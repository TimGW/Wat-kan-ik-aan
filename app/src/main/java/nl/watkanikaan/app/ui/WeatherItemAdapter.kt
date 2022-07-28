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
    private val weatherData: MutableList<Pair<Weather.Day, Weather.Forecast>> = mutableListOf(),
    private val listener: (Weather.Day, Weather.Forecast) -> Unit
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val weather = weatherData[position].second
        val day = weatherData[position].first

        with(holder.binding) {
            val resourceId = context.resources.getIdentifier(
                weather.weatherIcon, "drawable",
                context.packageName
            )
            val drawable = try {
                ResourcesCompat.getDrawable(context.resources, resourceId, null)
            } catch (e: Resources.NotFoundException) {
                ResourcesCompat.getDrawable(context.resources, R.drawable.refresh, null)
            }

            icon.setImageDrawable(drawable)
            overline.text = context.getText(day.toText())

            val tempString = if (day == Weather.Day.NOW) {
                weather.temperature.toString()
            } else {
                weather.temperature.roundToInt().toString()
            }
            underline.text = context.getString(R.string.temperature, tempString)
            root.setOnClickListener {
                listener(day, weather) // todo show current selected
            }
        }
    }

    override fun getItemCount() = weatherData.size

    fun updateItems(weatherItems: List<Pair<Weather.Day, Weather.Forecast>>) {
        weatherData.clear()
        weatherData.addAll(weatherItems)
        notifyItemRangeChanged(0, weatherItems.size)
    }

    private fun Weather.Day.toText() = when (this) {
        Weather.Day.NOW -> R.string.now
        Weather.Day.TODAY -> R.string.today
        Weather.Day.TOMORROW -> R.string.tomorrow
        Weather.Day.DAY_AFTER_TOMORROW -> R.string.day_after_tomorrow
    }

    class ViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
}
