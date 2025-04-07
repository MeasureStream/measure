package measuremanager.measure.repositories

import measuremanager.measure.entities.Measure
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface MeasureRepository :  MongoRepository<Measure, String> {
    fun findByTimeBetween(start: Instant, end: Instant): List<Measure>
    fun findByTimeBetween(start: Instant, end: Instant, pageable: Pageable): Page<Measure>
    fun findByTimeBefore(end: Instant) : List<Measure>
    fun findByTimeBefore(end: Instant, pageable: Pageable) : Page<Measure>
    fun findAllByNodeIdAndTimeBetween(nodeId: Long,start: Instant, end: Instant): List<Measure>
    fun findAllByNodeIdAndTimeBefore(nodeId: Long, end:Instant) : List<Measure>
    fun findAllByNodeId(nodeId: Long):List<Measure>
    @Aggregation(pipeline = [
        "{ '\$match': { 'nodeId': ?0 } }",
        "{ '\$group': { '_id': '\$measureUnit' } }"
    ])
    fun findDistinctMeasureUnitsByNodeId(nodeId: Long): List<String>
}