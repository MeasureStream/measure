package measuremanager.measure.configurations

import jakarta.annotation.PostConstruct
import measuremanager.measure.entities.Measure
import measuremanager.measure.entities.MeasurementUnit
import measuremanager.measure.repositories.MURepository
import measuremanager.measure.repositories.MeasureRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random


@Component
class DataInitializer(
    private val mr: MeasureRepository,
    private val mongoTemplate: MongoTemplate,
    private val mur : MURepository
) : InitializingBean{
    @PostConstruct
    fun init(){
        print("\ninitialize fake data\n")
    }

    override fun afterPropertiesSet() {
        mongoTemplate.db.drop()

        val measureUnits = listOf("Celsius", "Kelvin", "Pascal", "Lux")
        //b0be4ea5-17d3-4e63-ad81-510b4532dac8
        val measures1 = List(10) {
            Measure().apply {
                id = null // Lasciato null per essere generato dal database
                value = Random.nextDouble(0.0, 100.0)
                measureUnit = measureUnits.random()
                time = Instant.now().minusSeconds(Random.nextLong(0, 36000))
                nodeId = 1
            }
        }

        val mus = List(20) { it ->
            MeasurementUnit().apply {
                id = null
                muNetworkId = (it + 1 ).toLong()
                userId = "b0be4ea5-17d3-4e63-ad81-510b4532dac8"
            }
        }

        mr.saveAll(measures1)
        mur.saveAll(mus)

    }
}