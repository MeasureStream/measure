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
    fun findAllByNodeIdAndMeasureUnitAndTimeBetween(nodeId: Long, measureUnit: String, time: Instant, time2: Instant): List<Measure>
    fun findAllByNodeIdAndMeasureUnitAndTimeBefore(nodeId: Long, measureUnit: String, time: Instant) : List<Measure>
    fun findAllByNodeIdAndMeasureUnit(nodeId: Long, measureUnit: String):List<Measure>
    @Aggregation(pipeline = [
        "{ '\$match': { 'nodeId': ?0 } }",
        "{ '\$group': { '_id': '\$measureUnit' } }"
    ])
    fun findDistinctMeasureUnitsByNodeId(nodeId: Long): List<String>
}