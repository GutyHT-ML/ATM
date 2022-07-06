import java.time.format.DateTimeFormatter

const val rC = "\u001b[31m"
const val gC = "\u001B[32m"
const val bC = "\u001B[34m"
const val wC = "\u001b[0m"
const val pC = "\u001B[35m"
const val cC = "\u001B[36m"

fun String.toInput(): String {
    return "$pC$this$wC"
}

fun String.toInfo(): String {
    return "$cC$this$wC"
}

fun String.toError(): String {
    return "${rC}$this$wC"
}

fun String.title(): String {
    val scores = headerLen - this.length
    var scoreString = ""
    for (i in 0..scores / 2) {
        scoreString += "-"
    }
    return "$bC$scoreString$this$scoreString$wC"
}

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

const val inputCard = "Introduzca tarjeta de cuenta"
const val inputPin = "Introduzca pin"
const val inputName = "Introduzca nombre"
const val inputQuantity = "Introduzca una cantidad"
const val inputBill = "Inserte billete"

const val infoBalance = "Su saldo es: "

const val optionMore = "Depositar mas (s/n)"

const val invalidPin = "Pin invalido, debe contener 4 caracteres numericos"
const val invalidNomination = "Inserte una cantidad valida!"
const val invalidQuantity = "Inserte una cantidad numerica"
const val invalidOption = "Escoja una opcion valida"
const val invalidAccount = "No existe la cuenta"
const val invalidBalance = "Saldo insuficiente"

const val headerLen = 50


fun main(args: Array<String>) {
    val bank = Bank("WutyBank", mutableListOf())
    val cajero = ATM(bank, mutableListOf<Varo>(Varo.BilleteDe100(), Varo.BilleteDe500(), Varo.BilleteDe50()))
    val optionMap = mapOf(
        1 to "Meter dinero al cajero",
        2 to "Retirar dinero",
        3 to "Crear cuenta",
        4 to "Depositar",
        5 to "Estado de cuenta",
        6 to "Saber cuanto dinero tiene el cajero",
        7 to "Salir"
    )
    var cycle = true;
    loop@ while (cycle) {
        println("Seleccione opcion".title())
        optionMap.forEach { (k: Int, v: String) ->
            println("$gC$k$wC - $v")
        }
        val option = readln()
        if (option.length > 1 || option.isEmpty()) {
            println(invalidOption.toError())
            continue
        }
        val o = option.toCharArray()[0].digitToIntOrNull() ?: continue
        println("${optionMap[o]}".title())
        when (o) {
            1 -> {
                var insertingMoney = true
                while (insertingMoney) {
                    val nominations = Varo.getNominations()
                    println(inputBill.toInput())
                    var nomSt = "|"
                    for (i in nominations.keys) {
                        nomSt += " $i |"
                    }
                    println(nomSt.toInfo())
                    val rawMoney = readln()
                    try {
                        if (rawMoney.toInt() in nominations.keys) {
                            cajero.bills.add(nominations[rawMoney.toInt()] ?: throw Exception())
                        } else {
                            println(invalidNomination.toError())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println(invalidQuantity.toError())
                    }
                    println(optionMore.toInput())
                    insertingMoney = readln().equals("s", true)
                }
            }
            2 -> {
                println(inputCard.toInput())
                val card = readln()
                var isValid = false
                for (i in bank.accounts.indices) {
                    val a = bank.accounts[i]
                    if (a.cardNumber == card) {
                        isValid = true
                        println(inputPin.toInput())
                        val pin = readln()
                        if (a.pin == pin) {
                            println(inputQuantity.toInput())
                            val q = readln()
                            val quantity = q.toIntOrNull()
                            if (quantity != null) {
                                if (quantity <= a.money) {
                                    val used = Varo.getVaros(quantity, cajero.bills)
                                    if (used.size == 0) {
                                        println(invalidQuantity.toError())
                                        break
                                    }
                                    println(used.contentToString())
                                    cajero.bills.removeAll(used.toSet())
                                    a.money -= quantity
                                    a.movements.add(Movement(quantity, false))
                                    println("$infoBalance${a.money}")
                                    break
                                }
                                println(invalidBalance.toError())
                                break
                            }
                            println(invalidQuantity.toError())
                            break
                        }
                        println(invalidPin.toError())
                        break
                    }
                }
                if (!isValid) {
                    println(invalidAccount.toError())
                }
            }
            3 -> {
                var accountCreated = false
                while (!accountCreated) {
                    println(inputName.toInput())
                    val name = readln()
                    val account = Account(name)
                    bank.accounts.add(account)
                    println("Pin: ${account.pin} \nCard: ${account.cardNumber}".toInfo())
                    accountCreated = true
                }
            }
            4 -> {
                println(inputCard.toInput())
                val card = readln()
                var isValid = false
                for (i in bank.accounts.indices) {
                    val a = bank.accounts[i]
                    if (a.cardNumber == card) {
                        isValid = true
                        var transfer = true
                        while (transfer) {
                            println(inputBill.toInput())
                            val q = readln().toIntOrNull()
                            if (q != null) {
                                if (q in Varo.getNominations().keys) {
                                    val bill = Varo.getBill(q) ?: continue
                                    cajero.bills.add(bill)
                                    a.money += q
                                    a.movements.add(Movement(q, true))
                                    println(optionMore.toInput())
                                    val answer = readln()
                                    transfer = answer == "s"
                                    continue
                                }
                                println(invalidNomination.toError())
                                continue
                            }
                            println(invalidQuantity.toError())
                            continue
                        }
                    }
                }
                if (!isValid) {
                    println(invalidAccount.toError())
                }
            }
            5 ->{
                println(inputCard.toInput())
                val card = readln()
                for (i in bank.accounts.indices) {
                    val a = bank.accounts[i]
                    if (a.cardNumber == card) {
                        println(inputPin.toInput())
                        val pin = readln()
                        if (pin == a.pin) {
                            if (a.movements.isEmpty()) {
                                println("Nada para mostrar".toInfo())
                            } else {
                                for (m in a.movements.indices) {
                                    val movement = a.movements[m]
                                    println(
                                        "${timeFormatter.format(movement.date)} - Se " +
                                                if (movement.deposit) {
                                                    "deposito: "
                                                } else {
                                                    "retiro: "
                                                } + "${movement.quantity}"
                                    )
                                }
                            }
                            break
                        }
                        println(invalidPin)
                        break
                    }
                    println(invalidAccount)
                    break
                }
            }
            6 -> {
                println("Dinero en el cajero:\n$gC${cajero.money}$wC\nBilletes disponibles:\n$gC${cajero.totalBills}$wC".toInfo())
            }
            7 -> {
                cycle = false
            }
        }
    }
}