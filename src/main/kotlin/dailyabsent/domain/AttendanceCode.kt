package dailyabsent.domain

import dailyabsent.exception.AttendanceCodeNotFoundException

enum class AttendanceCode(val code: Int) {

    ATTENDANCE(0),
    ABSENT(1),
    LATE(2);

    companion object {

        fun from(code: Int): AttendanceCode {
            return values().firstOrNull() { it.code == code }
                ?: throw AttendanceCodeNotFoundException()
        }
    }
}
