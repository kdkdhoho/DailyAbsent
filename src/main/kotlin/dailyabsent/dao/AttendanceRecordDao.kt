package dailyabsent.dao

import dailyabsent.domain.AttendanceCode
import dailyabsent.domain.AttendanceRecord
import dailyabsent.domain.Member
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.time.LocalDate

@Repository
class AttendanceRecordDao(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper: RowMapper<AttendanceRecord> = RowMapper { rs, _ ->
        AttendanceRecord(
            rs.getLong("id"),
            Member(
                rs.getLong("member_id"),
                rs.getString("member.name")
            ),
            AttendanceCode.from(rs.getInt("attendance_code")),
            rs.getDate("date").toLocalDate()
        )
    }

    fun batchSave(attendanceRecords: List<AttendanceRecord>) {
        val sql = "INSERT INTO attendance_record(member_id, attendance_code) VALUES (?, ?)"

        jdbcTemplate.batchUpdate(
            sql,
            object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, index: Int) {
                    val data = attendanceRecords[index]
                    ps.setLong(1, data.member.id!!)
                    ps.setInt(2, data.attendanceCode.code)
                }

                override fun getBatchSize(): Int {
                    return attendanceRecords.size
                }
            }
        )
    }

    fun findByDate(date: LocalDate): MutableList<AttendanceRecord> {
        val sql = "SELECT * " +
            "FROM attendance_record " +
            "JOIN member ON attendance_record.member_id = member.id " +
            "WHERE date = ?"
        return jdbcTemplate.query(sql, rowMapper, date)
    }

    fun update(attendanceRecord: List<AttendanceRecord>) {
        val sql = "UPDATE attendance_record SET attendance_code = ? WHERE member_id = ?"

        jdbcTemplate.batchUpdate(
            sql,
            object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, index: Int) {
                    val data = attendanceRecord[index]
                    ps.setInt(1, data.attendanceCode.code)
                    ps.setLong(2, data.member.id!!)
                }

                override fun getBatchSize(): Int {
                    return attendanceRecord.size
                }
            }
        )
    }
}
