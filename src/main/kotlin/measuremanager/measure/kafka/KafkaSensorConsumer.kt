package measuremanager.measure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.polito.tesi.measuremanager.dtos.EventNode
import measuremanager.measure.entities.Measure
import measuremanager.measure.entities.MeasurementUnit
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
//changed to mus to node-event
    @KafkaListener(topics = ["node-event"], groupId = "measurestream-mus")
    fun consumeNode(message: String) {
        try {

            val event = objectMapper.readValue(message, EventNode::class.java)
            when (event.eventType) {
                "CREATE" -> {
                    mur.save(MeasurementUnit().apply { muNetworkId = event.node.networkId; userId = event.node.userId })
                    println("CREATED node ${event.node}")
                }

                "DELETE" -> {
                    mur.deleteByMuNetworkId(event.node.networkId)
                    println("DELETED node ${event.node}")
                }

                else -> {
                    throw Exception("OPERATION NOT recognized $event")
                }
            }

            println("Received node data: $event")
        } catch (e: Exception) {
            println("Error parsing node message: $message")
            e.printStackTrace()
        }
    }
}