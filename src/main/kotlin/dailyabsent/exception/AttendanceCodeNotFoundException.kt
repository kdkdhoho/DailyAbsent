package dailyabsent.exception

class AttendanceCodeNotFoundException : RuntimeException {

    constructor() : super(DEFAULT_MESSAGE)
    constructor(message: String?) : super(message)

    companion object {
        private const val DEFAULT_MESSAGE = "존재하지 않는 출석 코드입니다."
    }
}
