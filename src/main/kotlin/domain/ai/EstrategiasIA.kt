package domain.ai

import domain.game.Peca

data class LadosJogadaIA(val podeEsquerda: Boolean, val podeDireita: Boolean)

data class OpcaoJogadaIA(
    val peca: Peca,
    val lados: LadosJogadaIA,
    val soma: Int = peca.ladoEsquerdo + peca.ladoDireito
)

data class ContextoEstrategiaIA(
    val maoMaquina: List<Peca>,
    val pontaEsquerda: Int,
    val pontaDireita: Int
)

interface EstrategiaIA {
    /**
     * Escolhe qual peca a IA joga e em qual lado da mesa.
     */
    fun escolher(opcoes: List<OpcaoJogadaIA>, contexto: ContextoEstrategiaIA): Pair<Peca, Char>
}

class EstrategiaFacil : EstrategiaIA {
    /**
     * Estrategia simples: pega a primeira opcao valida disponivel.
     */
    override fun escolher(opcoes: List<OpcaoJogadaIA>, contexto: ContextoEstrategiaIA): Pair<Peca, Char> {
        val escolha = opcoes.first()
        val lado = when {
            escolha.lados.podeEsquerda && !escolha.lados.podeDireita -> 'E'
            !escolha.lados.podeEsquerda && escolha.lados.podeDireita -> 'D'
            else -> 'D'
        }
        return Pair(escolha.peca, lado)
    }
}

class EstrategiaMedia : EstrategiaIA {
    /**
     * Prioriza a peca de maior soma; em duplo encaixe pode alternar lado.
     */
    override fun escolher(opcoes: List<OpcaoJogadaIA>, contexto: ContextoEstrategiaIA): Pair<Peca, Char> {
        val escolha = opcoes.maxByOrNull { it.soma } ?: opcoes.first()
        val lado = when {
            escolha.lados.podeEsquerda && !escolha.lados.podeDireita -> 'E'
            !escolha.lados.podeEsquerda && escolha.lados.podeDireita -> 'D'
            else -> if ((0..1).random() == 0) 'E' else 'D'
        }
        return Pair(escolha.peca, lado)
    }
}

class EstrategiaDificil : EstrategiaIA {
    /**
     * Escolhe jogada com base na frequencia de numeros na mao para manter vantagem.
     */
    override fun escolher(opcoes: List<OpcaoJogadaIA>, contexto: ContextoEstrategiaIA): Pair<Peca, Char> {
        val frequenciaNumeros = mutableMapOf<Int, Int>()
        for (peca in contexto.maoMaquina) {
            frequenciaNumeros[peca.ladoEsquerdo] = (frequenciaNumeros[peca.ladoEsquerdo] ?: 0) + 1
            frequenciaNumeros[peca.ladoDireito] = (frequenciaNumeros[peca.ladoDireito] ?: 0) + 1
        }

        val escolha = opcoes.maxWithOrNull(
            compareBy<OpcaoJogadaIA> {
                (frequenciaNumeros[it.peca.ladoEsquerdo] ?: 0) +
                    (frequenciaNumeros[it.peca.ladoDireito] ?: 0)
            }.thenBy { it.soma }
        ) ?: opcoes.first()

        val ladoPreferido = if ((frequenciaNumeros[contexto.pontaEsquerda] ?: 0) >=
            (frequenciaNumeros[contexto.pontaDireita] ?: 0)
        ) {
            'E'
        } else {
            'D'
        }

        val lado = when {
            escolha.lados.podeEsquerda && !escolha.lados.podeDireita -> 'E'
            !escolha.lados.podeEsquerda && escolha.lados.podeDireita -> 'D'
            else -> ladoPreferido
        }

        return Pair(escolha.peca, lado)
    }
}
