package dailyabsent.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class SlackLogDao(private val jdbcTemplate: JdbcTemplate) {

    fun save(ts: String) {
        val sql = "INSERT INTO slack_log(ts) VALUES(?)"
        jdbcTemplate.update(sql, ts)
    }

    fun findByDate(date: LocalDate): String {
        val sql = "SELECT ts FROM slack_log WHERE date = ?"
        return jdbcTemplate.queryForObject(
            sql,
            { rs, _ ->
                rs.getString("ts")
            },
            date
        )!!
    }
}
