package ranking

import java.time.LocalDateTime

data class RankingEntry(
    val id: Long? = null,
    val nomeJogador: String,
    val modoJogo: String,
    val dificuldade: String,
    val pontuacao: Int,
    val dataHora: LocalDateTime
)