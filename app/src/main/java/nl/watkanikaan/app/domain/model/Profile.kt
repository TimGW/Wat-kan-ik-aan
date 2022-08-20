package nl.watkanikaan.app.domain.model

data class Profile(
    val thermoception: Thermoception = Thermoception.Normal,
) {
    sealed interface Thermoception {
        object Cold : Thermoception
        object Normal : Thermoception
        object Warm : Thermoception
    }
}