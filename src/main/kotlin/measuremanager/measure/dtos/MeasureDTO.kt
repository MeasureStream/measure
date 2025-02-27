package measuremanager.measure.dtos


import java.time.Instant

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import measuremanager.measure.entities.Measure

data class MeasureDTO (

    val id : String?,
    @NotNull
    val value: Double,

    @NotBlank
    val measureUnit: String,
    @PastOrPresent
    val time : Instant,

    @NotNull
    val nodeId: Long
)

fun Measure.toDTO() = MeasureDTO(id, value, measureUnit, time, nodeId)