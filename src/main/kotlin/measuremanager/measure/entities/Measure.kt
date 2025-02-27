package measuremanager.measure.entities

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "measures")
class Measure {

    @Id
    var id : String? = null

    @NotNull
    var value: Double = 0.0

    @NotBlank
    lateinit var measureUnit: String

    @PastOrPresent
    lateinit var time : Instant

    @NotNull
    @PositiveOrZero
    var nodeId: Long = 0

}