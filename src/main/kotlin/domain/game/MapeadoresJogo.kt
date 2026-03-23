package domain.game

/**
 * Converte dificuldade para texto, com opção de acentuação para exibição em UI.
 */
fun Dificuldade.paraTexto(acentuado: Boolean = false): String {
    return when (this) {
        Dificuldade.FACIL -> if (acentuado) "Fácil" else "Facil"
        Dificuldade.MEDIO -> if (acentuado) "Médio" else "Medio"
        Dificuldade.DIFICIL -> if (acentuado) "Difícil" else "Dificil"
    }
}

/**
 * Converte vencedor da rodada na pontuação de resultado do ranking.
 * 3 = vitória humana, 1 = empate, 0 = derrota humana.
 */
fun Jogador?.paraPontuacaoResultadoHumano(): Int {
    return when (this?.nome) {
        "Humano" -> 3
        "Maquina" -> 0
        else -> 1
    }
}

/**
 * Converte o código numérico de resultado em texto para interface.
 */
fun Int.paraTextoResultado(): String {
    return when (this) {
        3 -> "Vitória"
        1 -> "Empate"
        else -> "Derrota"
    }
}

/**
 * Soma os pontos das peças de uma mão de dominó.
 */
fun List<Peca>.somarPontosMao(): Int {
    return sumOf { it.ladoEsquerdo + it.ladoDireito }
}
