package measuremanager.measure.services

import measuremanager.measure.dtos.MeasureDTO
import measuremanager.measure.dtos.toDTO

import measuremanager.measure.repositories.MeasureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.Instant
@Service
class MeasureServiceImpl(
    private val mr: MeasureRepository
): MeasureService {
    override fun getAll(start: Instant?, end: Instant?): List<MeasureDTO> {
        //println("current user info ${getCurrentUserInfo()}")
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



    override fun getNode(start: Instant?, end: Instant?, nodeId: Long, measureUnit: String): List<MeasureDTO> {
        //println("current user info ${getCurrentUserInfo()}")
        return when {
            start != null && end != null -> {
                mr.findAllByNodeIdAndMeasureUnitAndTimeBetween(nodeId,measureUnit,start,end).map { it.toDTO() }
            }
            start != null && end == null  -> {
                mr.findAllByNodeIdAndMeasureUnitAndTimeBetween(nodeId,measureUnit,start, Instant.now()).map { it.toDTO() }
            }
            start == null && end != null -> {
                mr.findAllByNodeIdAndMeasureUnitAndTimeBefore(nodeId,measureUnit ,end).map { it.toDTO() }
            }
            else ->{
                mr.findAllByNodeIdAndMeasureUnit(nodeId,measureUnit).map{ it.toDTO() }
            }
        }
    }

    override fun getMeasureUnit(nodeId: Long): List<String> {
        return mr.findDistinctMeasureUnitsByNodeId(nodeId)
    }

    override fun deleteNode(start: Instant?, end: Instant?, nodeId: Long, measureUnit: String) {
         when {
            start != null && end != null -> {

                val measures  = mr.findAllByNodeIdAndMeasureUnitAndTimeBetween(nodeId,measureUnit,start,end)
                mr.deleteAll(measures)
            }
            start != null && end == null  -> {
                val measures  =  mr.findAllByNodeIdAndMeasureUnitAndTimeBetween(nodeId,measureUnit,start, Instant.now())
                mr.deleteAll(measures)
            }
            start == null && end != null -> {
                val measures  = mr.findAllByNodeIdAndMeasureUnitAndTimeBefore(nodeId,measureUnit ,end)
                mr.deleteAll(measures)
            }
            else ->{
                val measures  = mr.findAllByNodeIdAndMeasureUnit(nodeId,measureUnit)
                mr.deleteAll(measures)
            }
        }
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

    fun isAdmin() : Boolean{
        val auth = SecurityContextHolder.getContext().authentication
        val isAdmin = auth.authorities.any { it.authority == "ROLE_app-admin" }
        return isAdmin
    }
    fun getCurrentUserInfo(): Map<String, String?> {
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = auth.principal as Jwt
        return mapOf(
            "userId" to jwt.subject,
            "email" to jwt.getClaim<String>("email"),
            "givenName" to jwt.getClaim<String>("given_name"),
            "familyName" to jwt.getClaim<String>("family_name"),
            "preferredUsername" to jwt.getClaim<String>("preferred_username")
        )
    }
}