package dailyabsent.service

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatUpdateRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private const val TOKEN = "xoxb-5368094080258-5391821420192-5a2oU06hAJ26Jjgb6f3vjg9P"
private const val CHANNEL = "C05AU2T8VNW"

@Component
class SlackBot {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val client: MethodsClient = Slack.getInstance().methods()

    fun publishMessage(message: String): String {
        val result = runCatching {
            client.chatPostMessage {
                it.token(TOKEN)
                    .channel(CHANNEL)
                    .text(message)
            }
        }.onFailure { e ->
            logger.error("Slack Send Message Error " + e.message)
        }

        return result.getOrThrow().ts!!
    }

    fun updateMessage(message: String, ts: String) {
        runCatching {
            client.chatUpdate(
                ChatUpdateRequest.builder()
                    .ts(ts)
                    .token(TOKEN)
                    .channel(CHANNEL)
                    .text(message)
                    .build()
            )
        }.onFailure { e ->
            logger.error("Slack Update Message Error " + e.message)
        }
    }
}
