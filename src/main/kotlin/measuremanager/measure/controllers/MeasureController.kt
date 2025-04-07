package measuremanager.measure.controllers

import measuremanager.measure.dtos.MeasureDTO
import measuremanager.measure.services.MeasureService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    @GetMapping("/","")
    fun getNode(@RequestParam(required = false) start: Instant?, @RequestParam(required = false) end:Instant?, @RequestParam(required = true) nodeId:Long ) : List<MeasureDTO> {
        return ms.getNode(start, end, nodeId)
    }
}