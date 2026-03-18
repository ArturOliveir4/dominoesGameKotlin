class Peca(
    private var ladoEsquerdo: Int,
    private var ladoDireito: Int
) {
    fun getLadoEsquerdo(): Int {
        return ladoEsquerdo
    }

    fun getLadoDireito(): Int {
        return ladoDireito
    }

    override fun toString(): String {
        return "[$ladoEsquerdo|$ladoDireito]"
    }

    fun girar() {
        val temp = ladoEsquerdo
        ladoEsquerdo = ladoDireito
        ladoDireito = temp
    }
}
