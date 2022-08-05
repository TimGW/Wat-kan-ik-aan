package nl.watkanikaan.app.domain.usecase

import android.location.Location
import nl.watkanikaan.app.data.local.DefaultSharedPrefs
import nl.watkanikaan.app.data.local.SharedPref
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val defaultSharedPrefs: SharedPref // todo use repository & encypted sharedPref
) : UseCase<UpdateLocationUseCase.Params, Unit> {

    data class Params(val location: Location)

    override fun execute(
        params: Params
    ) {
       defaultSharedPrefs.setLocationSetting(params.location.latitude, params.location.longitude)
    }
}