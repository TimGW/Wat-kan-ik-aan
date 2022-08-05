package nl.watkanikaan.app.domain.model

data class Profile(
    val thermoception: Thermoception = Thermoception.Normal,
    val gender: Gender = Gender.Unspecified,
    val age: Int = 30,
) {
    sealed interface Thermoception {
        object Cold : Thermoception
        object Normal : Thermoception
        object Warm : Thermoception
    }

    sealed interface Gender {
        object Unspecified : Gender
        object Male : Gender
        object Female : Gender
    }
}