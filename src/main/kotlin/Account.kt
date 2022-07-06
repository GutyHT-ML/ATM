import java.util.UUID
import kotlin.random.Random

data class Account (
        val name: String
        ): MoneyHolder {
        val pin: CharSequence = generateProvisionalPin()
        val cardNumber: CharSequence = generateCard()

        companion object {
                private val charPool = "1234567890".toCharArray()
                private fun generateCard(): CharSequence {
                        return (1..16)
                                .map { i -> Random.nextInt(0, charPool.size) }
                                .map (charPool::get)
                                .joinToString ("")
                }

                private fun generateProvisionalPin(): CharSequence {
                        return (1..4)
                                .map { Random.nextInt(0, charPool.size) }
                                .map(charPool::get)
                                .joinToString("")
                }
        }

        override val movements: MutableList<Movement> = mutableListOf()
        override var money: Int = 0
}