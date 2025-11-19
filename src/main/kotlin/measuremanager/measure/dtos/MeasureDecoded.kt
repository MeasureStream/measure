package measuremanager.measure.dtos
import java.time.Instant

data class MeasureDecoded(
    val value: Double,
    val unit: String,
    val nodeId: Long,
    val time: Instant,
)
