package io.github.nanamochi.chio.packet.model

import io.github.nanamochi.chio.packet.types.Country
import io.github.nanamochi.chio.packet.types.Permissions

data class UserPresence
@JvmOverloads
constructor(
    val isIrc: Boolean = false,
    val timezone: Int = 0,
    val countryIndex: Int = 0,
    val permissions: Permissions = Permissions.Regular,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val city: String = "",
) {
    val countryName: String
        get() = Country.fromIndex(countryIndex).displayName

    val countryAcronym: String
        get() = Country.fromIndex(countryIndex).name

    /** Country name plus city when present. */
    val countryString: String
        get() = if (city.isBlank()) countryName else "$countryName / $city"
}
