data class ATM(
    val bank: Bank,
    var bills: MutableList<Varo>
): MoneyHolder {
    val totalBills: String
    get() {
        val map = mutableMapOf<Int, Int>()
        for (i in Varo.getNominations().keys) {
            map [i] = 0
        }
        for (i in bills.indices) {
            val b = bills[i]
            map[b.value] = map[b.value]!! + 1
        }
        var string = "| "
        for (i in map.keys) {
            val b = map[i]
            string += "Billetes de $$i => $b | "
        }
        return string
    }

    override val movements: MutableList<Movement>
        get() = mutableListOf()
    override var money: Int
        get() {
            var m = 0
            for(i in bills.indices) {
                m += bills[i].value
            }
            return m
        }
        set(value) {}
}