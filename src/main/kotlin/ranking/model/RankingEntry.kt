package ranking.model

import java.time.LocalDateTime

// representa 1 registro do ranking (1 partida salva)
data class RankingEntry(
    val id: Long? = null, //null quando você cria um objeto novo antes de salvar
    val nomeJogador: String,
    val modoJogo: String,
    val dificuldade: String,
    val pontuacao: Int,
    val resultado: Int, // resultado, 3=vitoria, 1=empate, 0=perdeu
    val pontosMao: Int = 0,
    val dataHora: LocalDateTime
)