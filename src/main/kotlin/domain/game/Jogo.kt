package domain.game

import domain.ai.ContextoEstrategiaIA
import domain.ai.EstrategiaDificil
import domain.ai.EstrategiaFacil
import domain.ai.EstrategiaMedia
import domain.ai.LadosJogadaIA
import domain.ai.OpcaoJogadaIA

enum class Dificuldade {
    FACIL,
    MEDIO,
    DIFICIL
}

data class ResultadoTurnoMaquina(
    val pecasCompradas: List<Peca>,
    val pecaJogada: Peca?
)

class Jogo {
    private var pontaEsquerda: Int = -1
    private var pontaDireita: Int = -1
    private var mesaInterna: MutableList<Peca> = mutableListOf()
    private var montanteInterno: ArrayDeque<Peca> = ArrayDeque()
    private var jogadorHumanoInterno: Jogador = Jogador("Humano")
    private var jogadorMaquinaInterno: Jogador = Jogador("Maquina")
    private var turnoHumanoInterno: Boolean = true
    private var turnosSemJogar: Int = 0
    private var vencedorInterno: Jogador? = null
    private var desempatePorSomaDaMaoInterno: Boolean = false
    private val estrategiaFacil = EstrategiaFacil()
    private val estrategiaMedia = EstrategiaMedia()
    private val estrategiaDificil = EstrategiaDificil()

    constructor()

    val turnoHumano: Boolean
        get() = turnoHumanoInterno

    val mesa: List<Peca>
        get() = mesaInterna

    val jogadorHumano: Jogador
        get() = jogadorHumanoInterno

    val jogadorMaquina: Jogador
        get() = jogadorMaquinaInterno

    val quantidadeMontante: Int
        get() = montanteInterno.size

    val montanteVazio: Boolean
        get() = montanteInterno.isEmpty()

    val vencedor: Jogador?
        get() = vencedorInterno

    /**
     * Define explicitamente se o turno atual pertence ao humano.
     */
    fun definirTurnoHumano(turnoHumano: Boolean) {
        turnoHumanoInterno = turnoHumano
    }

    /**
     * Remove uma peca especifica da mao do jogador humano.
     */
    fun removerPecaHumano(peca: Peca) {
        jogadorHumanoInterno.removerPeca(peca)
    }

    /**
     * Define o valor do contador de turnos consecutivos sem jogada.
     */
    fun definirTurnosSemJogar(valor: Int) {
        turnosSemJogar = valor
    }

    /**
     * Incrementa o contador de turnos sem jogada (usado para bloqueio).
     */
    fun incrementarTurnosSemJogar() {
        turnosSemJogar++
    }

    /**
     * Converte dificuldade do enum para texto e armazena no jogador humano.
     */
    private fun definirDificuldadeHumano(dificuldade: Dificuldade) {
        val dificuldadeTexto = when (dificuldade) {
            Dificuldade.FACIL -> "Facil"
            Dificuldade.MEDIO -> "Medio"
            Dificuldade.DIFICIL -> "Dificil"
        }
        jogadorHumanoInterno.definirDificuldade(dificuldadeTexto)
    }

    /**
     * Restaura estado inicial da rodada antes de redistribuir pecas.
     */
    private fun resetarPartida() {
        pontaEsquerda = -1
        pontaDireita = -1
        mesaInterna.clear()
        montanteInterno.clear()
        jogadorHumanoInterno.limparMao()
        jogadorMaquinaInterno.limparMao()
        jogadorHumanoInterno.zerarPontuacao()
        jogadorMaquinaInterno.zerarPontuacao()
        turnoHumanoInterno = true
        turnosSemJogar = 0
        vencedorInterno = null
        desempatePorSomaDaMaoInterno = false
    }

    /**
     * Prepara uma nova partida: cria montante, embaralha e distribui pecas.
     */
    private fun iniciarPartida(dificuldade: Dificuldade) {
        resetarPartida()
        definirDificuldadeHumano(dificuldade)

        for (i in 0..6) {
            for (j in i..6) {
                montanteInterno.addLast(Peca(i, j))
            }
        }

        montanteInterno = ArrayDeque(montanteInterno.shuffled())

        repeat(7) {
            jogadorMaquinaInterno.adicionarPeca(montanteInterno.removeFirst())
            jogadorHumanoInterno.adicionarPeca(montanteInterno.removeFirst())
        }
    }

    /**
     * Inicializa uma nova partida com a dificuldade selecionada.
     */
    fun iniciarJogo(dificuldade: Dificuldade) {
        iniciarPartida(dificuldade)
    }

    /**
     * Determina a primeira jogada pela maior dupla; sem dupla, usa maior soma.
     */
    private fun primeiraJogadaComLimite() {
        for (i in 6 downTo 0) {
            val duplaMaquina = jogadorMaquinaInterno.mao.find {
                it.ladoEsquerdo == i && it.ladoDireito == i
            }
            if (duplaMaquina != null) {
                jogarPeca(duplaMaquina)
                jogadorMaquinaInterno.removerPeca(duplaMaquina)
                turnoHumanoInterno = true
                return
            }

            val duplaHumano = jogadorHumanoInterno.mao.find {
                it.ladoEsquerdo == i && it.ladoDireito == i
            }
            if (duplaHumano != null) {
                jogarPeca(duplaHumano)
                jogadorHumanoInterno.removerPeca(duplaHumano)
                turnoHumanoInterno = false
                return
            }
        }

        val pecaMaquina = jogadorMaquinaInterno.mao.maxByOrNull { it.ladoEsquerdo + it.ladoDireito }
        val pecaHumano = jogadorHumanoInterno.mao.maxByOrNull { it.ladoEsquerdo + it.ladoDireito }

        if (pecaMaquina == null || pecaHumano == null) return

        val somaHumano = pecaHumano.ladoEsquerdo + pecaHumano.ladoDireito
        val somaMaquina = pecaMaquina.ladoEsquerdo + pecaMaquina.ladoDireito

        if (somaHumano >= somaMaquina) {
            mesaInterna.add(pecaHumano)
            jogadorHumanoInterno.removerPeca(pecaHumano)
            turnoHumanoInterno = true
        } else {
            mesaInterna.add(pecaMaquina)
            jogadorMaquinaInterno.removerPeca(pecaMaquina)
            turnoHumanoInterno = false
        }

        pontaEsquerda = mesaInterna.first().ladoEsquerdo
        pontaDireita = mesaInterna.last().ladoDireito
    }

    /**
     * Executa a primeira jogada da rodada.
     */
    fun primeiraJogada() {
        primeiraJogadaComLimite()
    }

    /**
     * Coloca uma peca na mesa e atualiza as pontas (uso principal na abertura).
     */
    fun jogarPeca(peca: Peca) {
        mesaInterna.add(peca)
        pontaEsquerda = mesaInterna.first().ladoEsquerdo
        pontaDireita = mesaInterna.last().ladoDireito
    }

    /**
     * Calcula em quais lados da mesa a peca pode ser jogada.
     */
    private fun ladosValidosParaPeca(peca: Peca): LadosJogadaIA {
        val podeEsquerda =
            peca.ladoEsquerdo == pontaEsquerda || peca.ladoDireito == pontaEsquerda
        val podeDireita =
            peca.ladoEsquerdo == pontaDireita || peca.ladoDireito == pontaDireita
        return LadosJogadaIA(podeEsquerda, podeDireita)
    }

    /**
     * Indica se existe ao menos uma opcao de lado valida.
     */
    private fun existeJogadaValida(lados: LadosJogadaIA): Boolean {
        return lados.podeEsquerda || lados.podeDireita
    }

    /**
     * Lista todas as jogadas validas da IA no estado atual.
     */
    private fun opcoesJogadaMaquina(): List<OpcaoJogadaIA> {
        return jogadorMaquinaInterno.mao.mapNotNull { peca ->
            val lados = ladosValidosParaPeca(peca)
            if (existeJogadaValida(lados)) OpcaoJogadaIA(peca, lados) else null
        }
    }

    /**
     * Monta o contexto lido pelas estrategias da IA.
     */
    private fun contextoAtualIA(): ContextoEstrategiaIA {
        return ContextoEstrategiaIA(
            maoMaquina = jogadorMaquinaInterno.mao,
            pontaEsquerda = pontaEsquerda,
            pontaDireita = pontaDireita
        )
    }

    /**
     * Compra uma peca do montante; retorna null se estiver vazio.
     */
    private fun comprarDoMontante(): Peca? {
        if (montanteInterno.isEmpty()) return null
        return montanteInterno.removeFirst()
    }

    /**
     * Verifica se o jogador humano possui alguma jogada valida.
     */
    fun maoValidaHumano(): Boolean {
        return jogadorHumanoInterno.mao.any { peca ->
            existeJogadaValida(ladosValidosParaPeca(peca))
        }
    }

    /**
     * Compra peca para o humano e adiciona na mao.
     */
    fun comprarMontanteHumano(): Peca {
        val pecaComprada = comprarDoMontante() ?: throw IllegalStateException("Montante vazio")
        jogadorHumanoInterno.adicionarPeca(pecaComprada)
        return pecaComprada
    }

    /**
     * Diz para a interface se a peca joga na esquerda, direita, ambos ou nenhum.
     */
    fun jogarPecaInterfaceLado(pecaEscolhida: Peca): Int {
        if (pecaValidaInterface(pecaEscolhida)) {
            val lados = ladosValidosParaPeca(pecaEscolhida)
            return when {
                lados.podeEsquerda && !lados.podeDireita -> 0
                !lados.podeEsquerda && lados.podeDireita -> 1
                lados.podeEsquerda && lados.podeDireita -> 2
                else -> -1
            }
        }

        return -1
    }

    /**
     * Atalho para validar se a peca escolhida e jogavel no estado atual.
     */
    fun pecaValidaInterface(pecaEscolhida: Peca): Boolean {
        return existeJogadaValida(ladosValidosParaPeca(pecaEscolhida))
    }

    /**
     * Usado pela UI para habilitar ou desabilitar uma peca da mao.
     */
    fun verificarPecaMaoJogador(peca: Peca): Boolean {
        return existeJogadaValida(ladosValidosParaPeca(peca))
    }

    /**
     * Joga a peca no lado escolhido e gira automaticamente quando necessario.
     */
    fun jogarNaMesa(peca: Peca, lado: Char) {
        when (lado) {
            'E', 'e' -> {
                if (peca.ladoDireito != pontaEsquerda) {
                    peca.girar()
                }
                mesaInterna.add(0, peca)
                pontaEsquerda = mesaInterna.first().ladoEsquerdo
            }

            'D', 'd' -> {
                if (peca.ladoEsquerdo != pontaDireita) {
                    peca.girar()
                }
                mesaInterna.add(peca)
                pontaDireita = mesaInterna.last().ladoDireito
            }

            else -> println("Opção inválida!!")
        }
    }

    /**
     * Verifica condicoes de encerramento: mao vazia ou bloqueio da rodada.
     */
    fun verificarFimDeJogo(): Boolean {
        if (jogadorHumanoInterno.mao.isEmpty()) {
            desempatePorSomaDaMaoInterno = false
            vencedorInterno = jogadorHumanoInterno
            return true
        } else if (jogadorMaquinaInterno.mao.isEmpty()) {
            desempatePorSomaDaMaoInterno = false
            vencedorInterno = jogadorMaquinaInterno
            return true
        }

        if (turnosSemJogar == 2) {
            desempatePorSomaDaMaoInterno = true
            val somaHumano = jogadorHumanoInterno.mao.sumOf { it.ladoEsquerdo + it.ladoDireito }
            val somaMaquina = jogadorMaquinaInterno.mao.sumOf { it.ladoEsquerdo + it.ladoDireito }

            vencedorInterno = when {
                somaHumano < somaMaquina -> jogadorHumanoInterno
                somaMaquina < somaHumano -> jogadorMaquinaInterno
                else -> null
            }
            return true
        }

        return false
    }

    /**
     * Loop do turno da IA com historico de compras para animacao na interface.
     */
    private fun jogarTurnoMaquinaComHistorico(seletor: (List<OpcaoJogadaIA>) -> Pair<Peca, Char>): ResultadoTurnoMaquina {
        val comprasRealizadas = mutableListOf<Peca>()

        while (true) {
            val opcoes = opcoesJogadaMaquina()
            if (opcoes.isNotEmpty()) {
                val (pecaEscolhida, ladoEscolhido) = seletor(opcoes)
                jogarNaMesa(pecaEscolhida, ladoEscolhido)
                jogadorMaquinaInterno.removerPeca(pecaEscolhida)
                turnosSemJogar = 0
                return ResultadoTurnoMaquina(comprasRealizadas.toList(), pecaEscolhida)
            }

            val compra = comprarDoMontante()
            if (compra != null) {
                jogadorMaquinaInterno.adicionarPeca(compra)
                comprasRealizadas.add(compra)
            } else {
                turnoHumanoInterno = true
                turnosSemJogar++
                return ResultadoTurnoMaquina(comprasRealizadas.toList(), null)
            }
        }
    }

    /**
     * Executa turno da IA com estrategia correspondente a dificuldade.
     */
    fun jogarMaquinaComHistorico(dificuldade: Dificuldade): ResultadoTurnoMaquina {
        return when (dificuldade) {
            Dificuldade.FACIL -> jogarTurnoMaquinaComHistorico { opcoes ->
                estrategiaFacil.escolher(opcoes, contextoAtualIA())
            }

            Dificuldade.MEDIO -> jogarTurnoMaquinaComHistorico { opcoes ->
                estrategiaMedia.escolher(opcoes, contextoAtualIA())
            }

            Dificuldade.DIFICIL -> jogarTurnoMaquinaComHistorico { opcoes ->
                estrategiaDificil.escolher(opcoes, contextoAtualIA())
            }
        }
    }

    /**
     * Aplica a pontuacao final da rodada para humano e IA.
     */
    fun somarPontos() {
        when (vencedorInterno?.nome) {
            "Humano" -> {
                jogadorHumanoInterno.adicionarPontuacao(3)
            }

            "Maquina" -> {
                jogadorMaquinaInterno.adicionarPontuacao(3)
            }

            else -> {
                // Empate: 1 ponto para cada jogador.
                jogadorHumanoInterno.adicionarPontuacao(1)
                jogadorMaquinaInterno.adicionarPontuacao(1)
            }
        }
    }

    /**
     * Informa se houve desempate por soma das maos.
     */
    fun houveDesempatePorSomaDaMao(): Boolean {
        return desempatePorSomaDaMaoInterno
    }
}
