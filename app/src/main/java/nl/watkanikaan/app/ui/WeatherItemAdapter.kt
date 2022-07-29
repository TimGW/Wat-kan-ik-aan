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
    private val weatherData: MutableMap<Weather.Day, Weather.Forecast> = mutableMapOf(),
    private val listener: (Weather.Day, Weather.Forecast) -> Unit
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val day = Weather.Day.values()[position]
        val weather = weatherData[day] ?: return

        val resourceId = context.resources.getIdentifier(
            weather.weatherIcon, "drawable",
            context.packageName
        )
        val drawable = try {
            ResourcesCompat.getDrawable(context.resources, resourceId, null)
        } catch (e: Resources.NotFoundException) {
            ResourcesCompat.getDrawable(context.resources, R.drawable.refresh, null)
        }

        holder.binding.icon.setImageDrawable(drawable)
        holder.binding.overline.text = context.getText(day.toText(true))

        val tempString = weather.temperature.roundToInt().toString()
        holder.binding.underline.text = context.getString(R.string.temperature, tempString)
        holder.binding.card.setOnClickListener {
            listener(day, weather) // todo show current selected
        }
    }

    override fun getItemCount() = weatherData.size

    fun updateItems(weatherItems: Map<Weather.Day, Weather.Forecast>) {
        weatherData.clear()
        weatherData.putAll(weatherItems)
        notifyItemRangeChanged(0, weatherItems.size)
    }

    class ViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
}
