package domain.game

class Peca(
    var ladoEsquerdo: Int,
    var ladoDireito: Int
) {

    override fun toString(): String {
        return "[$ladoEsquerdo|$ladoDireito]"
    }

    /**
     * Inverte os lados da peca para adequar a orientacao na mesa.
     */
    fun girar() {
        val temp = ladoEsquerdo
        ladoEsquerdo = ladoDireito
        ladoDireito = temp
    }
}
