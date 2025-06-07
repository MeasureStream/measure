package measuremanager.measure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.measure.dtos.EventMU
import measuremanager.measure.entities.Measure
import measuremanager.measure.entities.MeasurementUnit
import measuremanager.measure.entities.Node
import measuremanager.measure.repositories.MeasureRepository
import measuremanager.measure.repositories.MURepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaSensorConsumer(private val mr: MeasureRepository, private val objectMapper: ObjectMapper, private val mur:MURepository) {

    @KafkaListener(topics = ["measures"], groupId = "measurestream")
    fun consume(message:String){

        try {
            val data = objectMapper.readValue(message, Measure::class.java)
            mr.save(data)
            println("Saved data: $data")
        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }

    }

    @KafkaListener(topics = ["mus"], groupId = "measurestream-mus")
    fun consumeNode(message: String) {
        try {

            val muData = objectMapper.readValue(message, EventMU::class.java)
            when (muData.eventType) {
                "CREATE" -> {
                    mur.save(MeasurementUnit().apply { muNetworkId = muData.mu.muNetworkId; userId = muData.mu.userId })
                }

                "DELETE" -> { mur.deleteByMuNetworkId(muData.mu.muNetworkId) }
                else -> {
                    throw Exception("OPERATION NOT recognized $muData")
                }
            }

            println("Received node data: $muData")
        } catch (e: Exception) {
            println("Error parsing node message: $message")
            e.printStackTrace()
        }
    }
}