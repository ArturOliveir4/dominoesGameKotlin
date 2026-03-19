package domain.game

/**
 * Estado mutavel da rodada de domino.
 */
internal class EstadoJogo {
    var pontaEsquerda: Int = -1
    var pontaDireita: Int = -1
    val mesa: MutableList<Peca> = mutableListOf()
    var montante: ArrayDeque<Peca> = ArrayDeque()
    val jogadorHumano: Jogador = Jogador("Humano")
    val jogadorMaquina: Jogador = Jogador("Maquina")
    var turnoHumano: Boolean = true
    var turnosSemJogar: Int = 0
    var vencedor: Jogador? = null
    var desempatePorSomaDaMao: Boolean = false
}
