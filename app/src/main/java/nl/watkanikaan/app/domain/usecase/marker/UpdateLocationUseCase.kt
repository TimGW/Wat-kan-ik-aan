package nl.watkanikaan.app.domain.usecase.marker

import nl.watkanikaan.app.domain.usecase.UpdateLocationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.UseCase

interface UpdateLocationUseCase : UseCase<UpdateLocationUseCaseImpl.Params, Unit>