package dailyabsent.dao

import dailyabsent.domain.Member
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

@Repository
class MemberDao(private val jdbcTemplate: JdbcTemplate) {

    private val simpleJdbcInsert: SimpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
        .withTableName("member")
        .usingGeneratedKeyColumns("id")

    private val rowMapper: RowMapper<Member> = RowMapper { rs, _ ->
        Member(
            rs.getLong("id"),
            rs.getString("name")
        )
    }

    fun findAll(): MutableList<Member> {
        val sql = "SELECT * FROM MEMBER"

        return jdbcTemplate.query(sql, rowMapper)
    }
}
