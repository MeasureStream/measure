package measuremanager.measure.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import measuremanager.measure.dtos.MeasureDTO
import measuremanager.measure.services.MeasureService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

import java.time.Instant

@RestController
@RequestMapping("/API/measures")
class MeasureController(private val ms: MeasureService) {
    @GetMapping("/","")
    fun get(@RequestParam(required = false) start: Instant?, @RequestParam(required = false) end:Instant?  ) : List<MeasureDTO> {
        return ms.getAll(start, end)
    }

    @GetMapping("/P","P")
    fun getP(@RequestParam(required = false) start: Instant?, @RequestParam(required = false) end:Instant? , @RequestParam(required = true) p: Pageable) : Page<MeasureDTO> {

        return ms.getAllP(start,end,p)
    }
    @GetMapping("/nodeId/","/nodeId")
    fun getNode(@RequestParam(required = false) start: Instant?, @RequestParam(required = false) end:Instant?, @RequestParam(required = true) nodeId:Long, @RequestParam(required = true) measureUnit:String  ) : List<MeasureDTO> {
        return ms.getNode(start, end, nodeId, measureUnit)
    }

    @GetMapping("/measureUnitOfNode/","/measureUnitOfNode")
    fun getMeasureUnitOfNode(@RequestParam nodeId: Long ): List<String>{
        return ms.getMeasureUnit(nodeId)
    }



    @GetMapping("/download")
    fun downloadMeasures(
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?,
        @RequestParam nodeId: Long,
        @RequestParam measureUnit: String
    ): ResponseEntity<ByteArray> {
        val measures = ms.getNode(start, end, nodeId, measureUnit)

        // Serializza i dati in JSON
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())   // <-- aggiungi questo
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val jsonData = objectMapper.writeValueAsBytes(measures)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            contentDisposition = ContentDisposition
                .attachment()
                .filename("measures.json")
                .build()
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(jsonData)
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/nodeId/","/nodeId")
    fun deleteNode(@RequestParam(required = false) start: Instant?, @RequestParam(required = false) end:Instant?, @RequestParam(required = true) nodeId:Long, @RequestParam(required = true) measureUnit:String  ) {
        return ms.deleteNode(start,end, nodeId, measureUnit)
    }
}