package dailyabsent.controller

import dailyabsent.dao.AttendanceRecordDao
import dailyabsent.dao.MemberDao
import dailyabsent.dao.SlackLogDao
import dailyabsent.domain.AttendanceCode
import dailyabsent.domain.AttendanceRecord
import dailyabsent.dto.AttendRequest
import dailyabsent.dto.AttendResponse
import dailyabsent.service.SlackBot
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
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/attendance-records")
class AttendanceController(
    private val attendanceRecordDao: AttendanceRecordDao,
    private val memberDao: MemberDao,
    private val slackBot: SlackBot,
    private val slackLogDao: SlackLogDao
) {

    @GetMapping("/{localDate}")
    fun findByDate(
        @PathVariable
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        localDate: LocalDate
    ): ResponseEntity<List<AttendResponse>> {
        val attendanceRecords = attendanceRecordDao.findByDate(localDate)

        val response = attendanceRecords.map {
            AttendResponse.from(it)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun attend(@RequestBody request: List<AttendRequest>): ResponseEntity<Void> {
        val members = memberDao.findAll()
            .associateBy({ it.id }, { it })
        val attendanceRecords = request.map {
            AttendanceRecord(members.get(it.memberId)!!, AttendanceCode.from(it.attendance))
        }

        attendanceRecordDao.batchSave(attendanceRecords)

        var message: String = getMessage(attendanceRecords)

        val ts = slackBot.publishMessage(message)
        slackLogDao.save(ts)

        return ResponseEntity.created(URI.create(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))).build()
    }

    private fun getMessage(attendanceRecords: List<AttendanceRecord>): String {
        val absent = attendanceRecords.filter { it.attendanceCode == AttendanceCode.ABSENT }
            .map { it.member.name }.joinToString(", ", "결석: ", "\n")
        val late = attendanceRecords.filter { it.attendanceCode == AttendanceCode.LATE }
            .map { it.member.name }.joinToString(", ", "지각: ", "\n")

        var message: String
        if (attendanceRecords.all { it.attendanceCode == AttendanceCode.ATTENDANCE }) {
            message = "전원 출석 했습니다 ^_^"
        } else {
            message = absent + late
        }
        return message
    }

    @PutMapping
    fun update(@RequestBody request: List<AttendRequest>): ResponseEntity<Void> {
        val members = memberDao.findAll()
            .associateBy({ it.id }, { it })
        val attendanceRecords = request.map {
            AttendanceRecord(members.get(it.memberId)!!, AttendanceCode.from(it.attendance))
        }

        attendanceRecordDao.update(attendanceRecords)
        val ts = slackLogDao.findByDate(LocalDate.now())
        slackBot.updateMessage(getMessage(attendanceRecords), ts)
        return ResponseEntity.noContent().build()
    }
}
