package measuremanager.measure.services

import measuremanager.measure.dtos.MeasureDTO
import measuremanager.measure.dtos.toDTO

import measuremanager.measure.repositories.MeasureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
@Service
class MeasureServiceImpl(
    private val mr: MeasureRepository
): MeasureService {
    override fun getAll(start: Instant?, end: Instant?): List<MeasureDTO> {
        return when {
            start != null && end != null -> {
                 mr.findByTimeBetween(start,end).map { it.toDTO() }
            }
            start != null && end == null  -> {
                 mr.findByTimeBetween(start, Instant.now()).map { it.toDTO() }
            }
            start == null && end != null -> {
                 mr.findByTimeBefore(end).map { it.toDTO() }
            }
            else ->{
                mr.findAll().map{ it.toDTO() }
            }
        }
    }

    override fun getNode(start: Instant?, end: Instant?, nodeId:Long): List<MeasureDTO> {
        return when {
            start != null && end != null -> {
                mr.findAllByNodeIdAndTimeBetween(nodeId,start,end).map { it.toDTO() }
            }
            start != null && end == null  -> {
                mr.findAllByNodeIdAndTimeBetween(nodeId,start, Instant.now()).map { it.toDTO() }
            }
            start == null && end != null -> {
                mr.findAllByNodeIdAndTimeBefore(nodeId, end).map { it.toDTO() }
            }
            else ->{
                mr.findAllByNodeId(nodeId).map{ it.toDTO() }
            }
        }
    }

    override fun getMeasureUnit(nodeId: Long): List<String> {
        return mr.findDistinctMeasureUnitsByNodeId(nodeId)
    }

    override fun getAllP(start: Instant?, end: Instant?, page: Pageable): Page<MeasureDTO> {
        return when {
            start != null && end != null -> {
                mr.findByTimeBetween(start,end, page).map { it.toDTO() }
            }
            start != null && end == null  -> {
                mr.findByTimeBetween(start, Instant.now(), page).map { it.toDTO() }
            }
            start == null && end != null -> {
                mr.findByTimeBefore(end, page).map { it.toDTO() }
            }
            else ->{
                mr.findAll(page).map{ it.toDTO() }
            }
        }
    }

    override fun create(m: MeasureDTO): MeasureDTO {
        TODO("Not yet implemented")
    }

    override fun delete(m: MeasureDTO) {
        TODO("Not yet implemented")
    }
}