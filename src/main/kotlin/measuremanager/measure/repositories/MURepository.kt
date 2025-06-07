package measuremanager.measure.repositories

import measuremanager.measure.entities.MeasurementUnit
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MURepository: MongoRepository<MeasurementUnit,String> {
    fun deleteByMuNetworkId(muNetworkId: Long)
}