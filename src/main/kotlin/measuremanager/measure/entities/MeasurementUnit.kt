package measuremanager.measure.entities

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.UniqueElements
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "measurementunits")
class MeasurementUnit {
    @Id
    var id: String? = null

    @NotNull
    @PositiveOrZero
    @UniqueElements
    var muNetworkId : Long = 0

    @NotBlank
    lateinit var userId: String
}