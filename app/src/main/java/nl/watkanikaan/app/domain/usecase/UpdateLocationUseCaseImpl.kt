package nl.watkanikaan.app.domain.usecase

import android.location.Location
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.usecase.marker.UpdateLocationUseCase
import javax.inject.Inject

class UpdateLocationUseCaseImpl @Inject constructor(
    private val defaultSharedPrefs: SharedPref // todo use repository & encypted sharedPref
) : UpdateLocationUseCase {

    data class Params(val location: Location)

    override fun execute(
        params: Params
    ) {
        defaultSharedPrefs.setLocationSetting(params.location.latitude, params.location.longitude)
    }
}