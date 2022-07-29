package nl.watkanikaan.app.domain.usecase

import android.location.Location
import nl.watkanikaan.app.data.local.SharedPrefs
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val sharedPrefs: SharedPrefs // todo use repository
) : UseCase<UpdateLocationUseCase.Params, Unit> {

    data class Params(val location: Location)

    override fun execute(
        params: Params
    ) {
       sharedPrefs.setLocationSetting(params.location.latitude, params.location.longitude)
    }
}