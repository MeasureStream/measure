package measuremanager.measure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.measure.entities.Measure
import measuremanager.measure.repositories.MeasureRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaSensorConsumer(private val mr: MeasureRepository, private val objectMapper: ObjectMapper) {

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
}