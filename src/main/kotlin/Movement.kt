import java.time.LocalDateTime

data class Movement (
    val quantity: Int,
    val deposit: Boolean
    ) {
    val date: LocalDateTime = LocalDateTime.now()
}