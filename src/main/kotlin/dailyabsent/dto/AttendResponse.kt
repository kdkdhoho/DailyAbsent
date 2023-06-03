package dailyabsent.dto

import dailyabsent.domain.AttendanceRecord

data class AttendResponse(val memberId: Long, val memberName: String, val attendanceCode: Int) {

    companion object {
        fun from(attendanceRecord: AttendanceRecord): AttendResponse {
            return AttendResponse(attendanceRecord.member.id!!, attendanceRecord.member.name, attendanceRecord.attendanceCode.code)
        }
    }
}
