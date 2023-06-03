package dailyabsent.service

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatUpdateRequest
import org.springframework.stereotype.Component

@Component
class SlackBot {

    fun publishMessage(message: String): String {
        val client = Slack.getInstance().methods()
        val result = runCatching {
            client.chatPostMessage {
                it.token("xoxb-5368094080258-5391821420192-5a2oU06hAJ26Jjgb6f3vjg9P")
                    .channel("C05AU2T8VNW")
                    .text(message)
            }
        }.onFailure { e ->
            println("Slack Send Error: " + e.message)
        }

        return result.getOrThrow().ts!!
    }

    fun updateMessage(message: String, ts: String) {
        val client = Slack.getInstance().methods()
        runCatching {
            client.chatUpdate(
                ChatUpdateRequest.builder()
                    .ts(ts)
                    .token("xoxb-5368094080258-5391821420192-5a2oU06hAJ26Jjgb6f3vjg9P")
                    .channel("C05AU2T8VNW")
                    .text(message)
                    .build()
            )
        }.onFailure { e ->
            println("Slack Send Error: " + e.message)
        }
    }
}
