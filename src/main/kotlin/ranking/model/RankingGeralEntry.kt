package ranking.model

data class RankingGeralEntry(
    val nomeJogador: String,
    val pontosTotais: Int,
    val partidas: Int,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int
)
