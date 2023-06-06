package dailyabsent.controller

import dailyabsent.dto.AttendRequest
import dailyabsent.dto.AttendResponse
import dailyabsent.service.AttendanceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDate

@RestController
@RequestMapping("/attendance-records")
class AttendanceController(private val attendanceService: AttendanceService) {

    @GetMapping("/{localDate}")
    fun findByDate(
        @PathVariable
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        localDate: LocalDate
    ): ResponseEntity<List<AttendResponse>> {
        return ResponseEntity.ok(attendanceService.findByDate(localDate))
    }

    @PostMapping
    fun attend(@RequestBody request: List<AttendRequest>): ResponseEntity<Void> {
        val dailyLocalDate = attendanceService.attend(request)

        return ResponseEntity.created(URI.create(dailyLocalDate)).build()
    }

    @PutMapping
    fun update(@RequestBody request: List<AttendRequest>): ResponseEntity<Void> {
        attendanceService.update(request)

        return ResponseEntity.noContent().build()
    }
}
