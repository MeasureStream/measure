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
    private fun decodePayload(bytes: ByteArray): MeasureDecoded {
        val buffer = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN)

        // 1) value (4 bytes)
        val valueFloat = buffer.float

        // 2) unit (2 bytes → codice)
        val unitCode = buffer.short.toInt() // esempio: 1=°C, 2=%, etc.
        val unit = decodeUnit(unitCode)

        // 3) nodeId (4 bytes)
        val nodeId = buffer.int.toLong()

        return MeasureDecoded(
            value = valueFloat.toDouble(),
            unit = unit,
            nodeId = nodeId,
            time = Instant.now(),
        )
    }

    private fun decodeUnit(code: Int): String =
        when (code) {
            1 -> "°C"
            2 -> "%"
            3 -> "Pa"
            else -> "unknown"
        }

    @KafkaListener(topics = ["ttn-uplink"], groupId = "measurestream")
    fun consume(message: String) {
        try {
            // val data = objectMapper.readValue(message, Measure::class.java)
            println("arrived message: $message")
            val decodedKafka = Base64.getDecoder().decode(message)
            println("decoded message:  $decodedKafka")
            val jsonStr = String(decodedKafka)
            val root: JsonNode = objectMapper.readTree(jsonStr)

            val dataNode =
                root["data"]
                    ?: throw Exception("Missing 'data' field in message")

            // Device ID
            val deviceId = 1
            // dataNode["end_device_ids"]?.get("device_id")?.asText()
            //    ?: throw Exception("Missing device_id in message")

            // Base64 LoRaWAN uplink
            val frmPayload =
                dataNode["uplink_message"]?.get("frm_payload")?.asText()
                    ?: throw Exception("Missing frm_payload in message")

            // Decode Base64
            val decodedBytes: ByteArray = Base64.getDecoder().decode(frmPayload)

            // Convert decoded bytes → Measure object
            val bytes = Base64.getDecoder().decode(frmPayload)

            val decoded = decodePayload(bytes)

            val measure =
                Measure().apply {
                    value = decoded.value
                    measureUnit = decoded.unit
                    nodeId = decoded.nodeId
                    time = decoded.time
                }

            // Store in MongoDB
            mr.save(measure)

            println("Saved measure from $deviceId: $measure")
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

