package dailyabsent.domain

import java.time.LocalDate

data class AttendanceRecord(
    val id: Long?,
    val member: Member,
    val attendanceCode: AttendanceCode,
    val localDate: LocalDate?
) {

    constructor(member: Member, attendanceCode: AttendanceCode) : this(null, member, attendanceCode, null)
}
