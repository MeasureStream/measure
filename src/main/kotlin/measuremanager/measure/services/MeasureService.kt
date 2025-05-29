package measuremanager.measure.services

import measuremanager.measure.dtos.MeasureDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant

interface MeasureService {
    fun getAll(start: Instant?, end: Instant?): List<MeasureDTO>
    fun getAllP(start: Instant?, end: Instant?, page:Pageable): Page<MeasureDTO>
    fun create(m : MeasureDTO): MeasureDTO
    fun delete(m : MeasureDTO)
    fun getNode(start: Instant?, end: Instant?, nodeId:Long, measureUnit: String): List<MeasureDTO>
    fun getMeasureUnit(nodeId: Long): List<String>
    fun deleteNode(start: Instant?, end: Instant?, nodeId:Long, measureUnit: String)
}