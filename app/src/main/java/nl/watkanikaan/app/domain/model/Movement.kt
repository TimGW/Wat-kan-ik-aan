package nl.watkanikaan.app.domain.model

sealed interface Movement {
    object Rest : Movement
    object Light : Movement
    object Heavy : Movement
}