package nl.watkanikaan.app.domain.usecase.marker

import kotlinx.coroutines.flow.Flow
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.usecase.CalcRecommendationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.UseCase

interface CalcRecommendationUseCase :
    UseCase<CalcRecommendationUseCaseImpl.Params, @JvmSuppressWildcards Flow<Recommendation>>