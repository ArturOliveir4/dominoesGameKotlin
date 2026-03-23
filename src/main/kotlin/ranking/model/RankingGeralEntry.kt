package ranking.model


// representa um resumo estatístico por jogador.
data class RankingGeralEntry(
    val nomeJogador: String,
    val pontosTotais: Int,
    val partidas: Int,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int
)
