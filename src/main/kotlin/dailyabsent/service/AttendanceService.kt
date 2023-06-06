package dailyabsent.service

import dailyabsent.dao.AttendanceRecordDao
import dailyabsent.dao.MemberDao
import dailyabsent.dao.SlackLogDao
import dailyabsent.domain.AttendanceCode
import dailyabsent.domain.AttendanceRecord
import dailyabsent.dto.AttendRequest
import dailyabsent.dto.AttendResponse
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AttendanceService(
    private val attendanceRecordDao: AttendanceRecordDao,
    private val memberDao: MemberDao,
    private val slackBot: SlackBot,
    private val slackLogDao: SlackLogDao
) {

    fun findByDate(localDate: LocalDate): List<AttendResponse> {
        val attendanceRecords = attendanceRecordDao.findByDate(localDate)

        return attendanceRecords.map { AttendResponse.from(it) }
    }

    fun attend(request: List<AttendRequest>): String {
        val members = memberDao.findAll().associateBy({ it.id }, { it })
        val attendanceRecords = request.map {
            AttendanceRecord(members[it.memberId]!!, AttendanceCode.from(it.attendance))
        }
        attendanceRecordDao.batchSave(attendanceRecords)

        sendMessageToSlack(attendanceRecords)

        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun sendMessageToSlack(attendanceRecords: List<AttendanceRecord>) {
        val message = createMessage(attendanceRecords)

        val ts = slackBot.publishMessage(message)
        slackLogDao.save(ts)
    }

    private fun createMessage(attendanceRecords: List<AttendanceRecord>): String {
        val absentNames = attendanceRecords.filter { it.attendanceCode == AttendanceCode.ABSENT }
            .joinToString(", ", "결석: ", "\n") { it.member.name }
        val lateNames = attendanceRecords.filter { it.attendanceCode == AttendanceCode.LATE }
            .joinToString(", ", "지각: ", "\n") { it.member.name }

        return if (attendanceRecords.all { it.attendanceCode == AttendanceCode.ATTENDANCE }) {
            "전원 출석 했습니다 ^_^"
        } else {
            absentNames + lateNames
        }
    }

    fun update(request: List<AttendRequest>) {
        val members = memberDao.findAll().associateBy({ it.id }, { it })
        val attendanceRecords = request.map {
            AttendanceRecord(members.get(it.memberId)!!, AttendanceCode.from(it.attendance))
        }
        attendanceRecordDao.update(attendanceRecords)

        val ts = slackLogDao.findByDate(LocalDate.now())
        slackBot.updateMessage(createMessage(attendanceRecords), ts)
    }
}
