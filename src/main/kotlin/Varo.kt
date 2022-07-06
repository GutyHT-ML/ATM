import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

sealed class Varo (val value: Int) {


    class BilleteDe20: Varo(20)
    class BilleteDe50: Varo(50)
    class BilleteDe100: Varo(100)
    class BilleteDe200: Varo(200)
    class BilleteDe500: Varo(500)
    class BilleteDe1000: Varo(1000)
    class BilleteDe2000: Varo(2000)

    override fun toString(): String {
        return """
            Billete de a ${this.value}
        """.trimIndent()
    }

    companion object {
        private val nominationClasses: Map<Int, KClass<out Varo>>
        get() {
            val nominations = mutableMapOf<Int, KClass<out Varo>>()
            val instances = getCurrentInstances()
            for (i in instances.indices) {
                val s = instances[i]
                nominations[s.value] = s::class
            }
            return nominations
        }

        private fun getTotal(list: List<Varo>): Int {
            var m = 0
            for(i in list.indices) {
                m += list[i].value
            }
            return m
        }

        private fun getCurrentInstances (): Array<Varo> {
            val subclasses: List<KClass<out Varo>> = Varo::class.sealedSubclasses
            val instances = arrayListOf<Varo>()
            subclasses.forEach {
                val inst = it.constructors.first().call()
                instances.add(inst)
            }
            return instances.toTypedArray()
        }

        fun getNominations(): Map<Int, Varo> {
            val nominations = mutableMapOf<Int, Varo>()
            val values = nominationClasses.keys.toTypedArray()
            for (i in values.indices) {
                val v = values[i]
                nominations[v] = getBill(v) ?: continue
            }
            return nominations
        }

        fun List<Varo>.getLowestNominationAvailable(): Int {
            val s = sortedBy { it.value }
            return s.first().value
        }

        fun getBill (nomination: Int): Varo? {
            if (nomination !in nominationClasses.keys) {
                return null
            }
            val kc = nominationClasses[nomination] ?: return null
            val inst = kc.primaryConstructor?.call() ?: return null
            return inst
        }

        fun getLowestNomination (): Int {
            return getNominations().keys.minOf { it }
        }

        fun getHighestNomination () : Int {
            return getNominations().keys.maxOf { it }
        }

        fun getVaros (valor: Int, billList: List<Varo>): Array<Varo> {
            if (valor % billList.getLowestNominationAvailable() != 0) {
                println("${valor % billList.getLowestNominationAvailable()}")
                println("Error ${billList.getLowestNominationAvailable()}")
                return emptyArray()
            }
            var sortedValue = valor
            val sortedList = billList.toMutableList()
            val usedBills = mutableListOf<Varo>()
            while (sortedValue > 0) {
                for (i in sortedList.indices) {
                    if (sortedValue <= 0) {
                        break
                    }
                    val bill = sortedList[i]
                    if (bill.value <= sortedValue) {
                        usedBills.add(bill)
                        sortedList.removeAt(i)
                        sortedValue -= bill.value
                    }
                }
            }
            return usedBills.toTypedArray()
        }
    }
}
