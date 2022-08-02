package nl.watkanikaan.app.domain.model

data class Profile(
    val thermoception: Thermoception = Thermoception.Normal,
    val gender: Gender = Gender.Unspecified,
    val age: Int = 30,
    val movement: Movement = Movement.Rest,
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

    sealed interface Movement {
        object Rest : Movement
        object Light : Movement
        object Heavy : Movement
    }
}