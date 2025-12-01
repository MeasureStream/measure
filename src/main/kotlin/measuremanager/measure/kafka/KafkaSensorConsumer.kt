package measuremanager.measure.kafka

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.polito.tesi.measuremanager.dtos.EventNode
import measuremanager.measure.dtos.MeasureDecoded
import measuremanager.measure.entities.Measure
import measuremanager.measure.entities.MeasurementUnit
import measuremanager.measure.repositories.MURepository
import measuremanager.measure.repositories.MeasureRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.time.Instant
import java.util.Base64

@Service
class KafkaSensorConsumer(
    private val mr: MeasureRepository,
    private val objectMapper: ObjectMapper,
    private val mur: MURepository,
) {
    @KafkaListener(topics = ["ttn-uplink-measure"], groupId = "measurestream")
    fun consume(message: String) {
        try {
            println("Arrived message: $message")

            // Parsing del JSON in MeasureDecoded
            val decoded = objectMapper.readValue(message, MeasureDecoded::class.java)

            // Conversione nel modello da salvare
            val measure =
                Measure().apply {
                    value = decoded.value
                    measureUnit = decoded.unit
                    nodeId = decoded.nodeId
                    // rssi = decoded.rssi
                    // devEui = decoded.devEUI
                    time = Instant.parse(decoded.time)
                }

            mr.save(measure)
            println("Saved measure: $measure")
        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }
    }

// changed to mus to node-event
    @KafkaListener(topics = ["node-event"], groupId = "measurestream-mus")
    fun consumeNode(message: String) {
        try {
            val event = objectMapper.readValue(message, EventNode::class.java)
            when (event.eventType) {
                "CREATE" -> {
                    mur.save(
                        MeasurementUnit().apply {
                            muNetworkId = event.node.networkId
                            userId = event.node.userId
                        },
                    )
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
