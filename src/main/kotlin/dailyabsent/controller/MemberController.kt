package dailyabsent.controller

import dailyabsent.dao.MemberDao
import dailyabsent.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(private val memberDao: MemberDao) {

    @GetMapping
    fun findAll(): ResponseEntity<MutableList<Member>> {
        val members = memberDao.findAll()

        return ResponseEntity.ok(members)
    }
}
