package domain.game

import domain.ai.ContextoEstrategiaIA
import domain.ai.EstrategiaDificil
import domain.ai.EstrategiaFacil
import domain.ai.EstrategiaIA
import domain.ai.EstrategiaMedia
import domain.ai.LadosJogadaIA
import domain.ai.OpcaoJogadaIA

/**
 * Regras de negocio da rodada. Opera sobre um EstadoJogo.
 */
internal class MotorJogo(
    private val estado: EstadoJogo,
    private val estrategiaFacil: EstrategiaFacil,
    private val estrategiaMedia: EstrategiaMedia,
    private val estrategiaDificil: EstrategiaDificil
) {
    /**
     * Converte dificuldade do enum para texto e armazena no jogador humano.
     */
    private fun definirDificuldadeHumano(dificuldade: Dificuldade) =
        estado.jogadorHumano.definirDificuldade(dificuldade.paraTexto())

    /**
     * Tenta abrir a rodada com a dupla de valor informado para um jogador.
     */
    private fun abrirComDupla(jogador: Jogador, valor: Int, turnoHumanoApos: Boolean): Boolean {
        val dupla = jogador.mao.find {
            it.ladoEsquerdo == valor && it.ladoDireito == valor
        } ?: return false
        jogarPecaAbertura(dupla)
        jogador.removerPeca(dupla)
        estado.turnoHumano = turnoHumanoApos
        return true
    }

    /**
     * Restaura estado inicial da rodada antes de redistribuir pecas.
     */
    private fun resetarPartida() {
        estado.pontaEsquerda = -1
        estado.pontaDireita = -1
        estado.mesa.clear()
        estado.montante.clear()
        estado.jogadorHumano.limparMao()
        estado.jogadorMaquina.limparMao()
        estado.jogadorHumano.zerarPontuacao()
        estado.jogadorMaquina.zerarPontuacao()
        estado.turnoHumano = true
        estado.turnosSemJogar = 0
        estado.vencedor = null
        estado.desempatePorSomaDaMao = false
    }

    /**
     * Prepara uma nova partida: cria montante, embaralha e distribui pecas.
     */
    fun iniciarPartida(dificuldade: Dificuldade) {
        resetarPartida()
        definirDificuldadeHumano(dificuldade)

        for (i in 0..6) {
            for (j in i..6) {
                estado.montante.addLast(Peca(i, j))
            }
        }

        estado.montante = ArrayDeque(estado.montante.shuffled())

        repeat(7) {
            estado.jogadorMaquina.adicionarPeca(estado.montante.removeFirst())
            estado.jogadorHumano.adicionarPeca(estado.montante.removeFirst())
        }
    }

    /**
     * Determina a primeira jogada pela maior dupla; sem dupla, usa maior soma.
     */
    fun primeiraJogadaComLimite() {
        for (i in 6 downTo 0) {
            if (abrirComDupla(estado.jogadorMaquina, i, turnoHumanoApos = true)) return
            if (abrirComDupla(estado.jogadorHumano, i, turnoHumanoApos = false)) return
        }

        val pecaMaquina = estado.jogadorMaquina.mao.maxByOrNull { it.ladoEsquerdo + it.ladoDireito }
        val pecaHumano = estado.jogadorHumano.mao.maxByOrNull { it.ladoEsquerdo + it.ladoDireito }

        if (pecaMaquina == null || pecaHumano == null) return

        val somaHumano = pecaHumano.ladoEsquerdo + pecaHumano.ladoDireito
        val somaMaquina = pecaMaquina.ladoEsquerdo + pecaMaquina.ladoDireito

        if (somaHumano >= somaMaquina) {
            estado.mesa.add(pecaHumano)
            estado.jogadorHumano.removerPeca(pecaHumano)
            estado.turnoHumano = true
        } else {
            estado.mesa.add(pecaMaquina)
            estado.jogadorMaquina.removerPeca(pecaMaquina)
            estado.turnoHumano = false
        }

        atualizarPontasDaMesa()
    }

    /**
     * Recalcula as pontas da mesa com base no primeiro e ultimo elementos.
     */
    private fun atualizarPontasDaMesa() {
        estado.pontaEsquerda = estado.mesa.first().ladoEsquerdo
        estado.pontaDireita = estado.mesa.last().ladoDireito
    }

    /**
     * Coloca uma peca na mesa e atualiza as pontas (uso principal na abertura).
     */
    fun jogarPecaAbertura(peca: Peca) {
        estado.mesa.add(peca)
        atualizarPontasDaMesa()
    }

    /**
     * Calcula em quais lados da mesa a peca pode ser jogada.
     */
    private fun ladosValidosParaPeca(peca: Peca): LadosJogadaIA {
        val podeEsquerda =
            peca.ladoEsquerdo == estado.pontaEsquerda || peca.ladoDireito == estado.pontaEsquerda
        val podeDireita =
            peca.ladoEsquerdo == estado.pontaDireita || peca.ladoDireito == estado.pontaDireita
        return LadosJogadaIA(podeEsquerda, podeDireita)
    }

    /**
     * Indica se existe ao menos uma opcao de lado valida.
     */
    private fun existeJogadaValida(lados: LadosJogadaIA): Boolean =
        lados.podeEsquerda || lados.podeDireita

    /**
     * Lista todas as jogadas validas da IA no estado atual.
     */
    private fun opcoesJogadaMaquina(): List<OpcaoJogadaIA> {
        return estado.jogadorMaquina.mao.mapNotNull { peca ->
            val lados = ladosValidosParaPeca(peca)
            if (existeJogadaValida(lados)) OpcaoJogadaIA(peca, lados) else null
        }
    }

    /**
     * Monta o contexto lido pelas estrategias da IA.
     */
    private fun contextoAtualIA(): ContextoEstrategiaIA {
        return ContextoEstrategiaIA(
            maoMaquina = estado.jogadorMaquina.mao,
            pontaEsquerda = estado.pontaEsquerda,
            pontaDireita = estado.pontaDireita
        )
    }

    /**
     * Compra uma peca do montante; retorna null se estiver vazio.
     */
    private fun comprarDoMontante(): Peca? {
        if (estado.montante.isEmpty()) return null
        return estado.montante.removeFirst()
    }

    /**
     * Verifica se o jogador humano possui alguma jogada valida.
     */
    fun maoValidaHumano(): Boolean =
        estado.jogadorHumano.mao.any { peca ->
            existeJogadaValida(ladosValidosParaPeca(peca))
        }

    /**
     * Compra peca para o humano e adiciona na mao.
     */
    fun comprarMontanteHumano(): Peca {
        val pecaComprada = comprarDoMontante()
            ?: throw IllegalStateException("Montante vazio")
        estado.jogadorHumano.adicionarPeca(pecaComprada)
        return pecaComprada
    }

    /**
     * Traduz os lados validos para o codigo usado pela interface.
     */
    private fun codigoLadoInterface(lados: LadosJogadaIA): Int =
        when {
            lados.podeEsquerda && !lados.podeDireita -> 0
            !lados.podeEsquerda && lados.podeDireita -> 1
            lados.podeEsquerda && lados.podeDireita -> 2
            else -> -1
        }

    /**
     * Diz para a interface se a peca joga na esquerda, direita, ambos ou nenhum.
     */
    fun jogarPecaInterfaceLado(pecaEscolhida: Peca): Int {
        if (!pecaValidaInterface(pecaEscolhida)) return -1
        return codigoLadoInterface(ladosValidosParaPeca(pecaEscolhida))
    }

    /**
     * Atalho para validar se a peca escolhida e jogavel no estado atual.
     */
    fun pecaValidaInterface(pecaEscolhida: Peca): Boolean =
        existeJogadaValida(ladosValidosParaPeca(pecaEscolhida))

    /**
     * Usado pela UI para habilitar ou desabilitar uma peca da mao.
     */
    fun verificarPecaMaoJogador(peca: Peca): Boolean =
        existeJogadaValida(ladosValidosParaPeca(peca))

    /**
     * Normaliza o caractere de lado para E ou D, aceitando minusculas.
     */
    private fun normalizarLado(lado: Char): Char? =
        when (lado.uppercaseChar()) {
            'E' -> 'E'
            'D' -> 'D'
            else -> null
        }

    /**
     * Encaixa uma peca no lado esquerdo da mesa, girando quando necessario.
     */
    private fun jogarNaEsquerda(peca: Peca) {
        if (peca.ladoDireito != estado.pontaEsquerda) {
            peca.girar()
        }
        estado.mesa.add(0, peca)
        atualizarPontasDaMesa()
    }

    /**
     * Encaixa uma peca no lado direito da mesa, girando quando necessario.
     */
    private fun jogarNaDireita(peca: Peca) {
        if (peca.ladoEsquerdo != estado.pontaDireita) {
            peca.girar()
        }
        estado.mesa.add(peca)
        atualizarPontasDaMesa()
    }

    /**
     * Joga a peca no lado escolhido e gira automaticamente quando necessario.
     */
    fun jogarNaMesa(peca: Peca, lado: Char) {
        when (normalizarLado(lado)) {
            'E' -> jogarNaEsquerda(peca)
            'D' -> jogarNaDireita(peca)
            else -> println("Opcao invalida!!")
        }
    }

    /**
     * Verifica condicoes de encerramento: mao vazia ou bloqueio da rodada.
     */
    fun verificarFimDeJogo(): Boolean {
        val vencedorPorMaoVazia = when {
            estado.jogadorHumano.mao.isEmpty() -> estado.jogadorHumano
            estado.jogadorMaquina.mao.isEmpty() -> estado.jogadorMaquina
            else -> null
        }

        if (vencedorPorMaoVazia != null) {
            estado.desempatePorSomaDaMao = false
            estado.vencedor = vencedorPorMaoVazia
            return true
        }

        if (estado.turnosSemJogar == 2) {
            estado.desempatePorSomaDaMao = true
            val somaHumano = estado.jogadorHumano.mao.somarPontosMao()
            val somaMaquina = estado.jogadorMaquina.mao.somarPontosMao()

            estado.vencedor = when {
                somaHumano < somaMaquina -> estado.jogadorHumano
                somaMaquina < somaHumano -> estado.jogadorMaquina
                else -> null
            }
            return true
        }

        return false
    }

    /**
     * Loop do turno da IA com historico de compras para animacao na interface.
     */
    private fun jogarTurnoMaquinaComHistorico(
        seletor: (List<OpcaoJogadaIA>) -> Pair<Peca, Char>
    ): ResultadoTurnoMaquina {
        val comprasRealizadas = mutableListOf<Peca>()

        while (true) {
            val opcoes = opcoesJogadaMaquina()
            if (opcoes.isNotEmpty()) {
                val (pecaEscolhida, ladoEscolhido) = seletor(opcoes)
                jogarNaMesa(pecaEscolhida, ladoEscolhido)
                estado.jogadorMaquina.removerPeca(pecaEscolhida)
                estado.turnosSemJogar = 0
                return ResultadoTurnoMaquina(comprasRealizadas.toList(), pecaEscolhida)
            }

            val compra = comprarDoMontante()
            if (compra != null) {
                estado.jogadorMaquina.adicionarPeca(compra)
                comprasRealizadas.add(compra)
            } else {
                estado.turnoHumano = true
                estado.turnosSemJogar++
                return ResultadoTurnoMaquina(comprasRealizadas.toList(), null)
            }
        }
    }

    /**
     * Resolve a estrategia da IA com base na dificuldade escolhida.
     */
    private fun obterEstrategiaIA(dificuldade: Dificuldade): EstrategiaIA =
        when (dificuldade) {
            Dificuldade.FACIL -> estrategiaFacil
            Dificuldade.MEDIO -> estrategiaMedia
            Dificuldade.DIFICIL -> estrategiaDificil
        }

    /**
     * Executa turno da IA com estrategia correspondente a dificuldade.
     */
    fun jogarMaquinaComHistorico(dificuldade: Dificuldade): ResultadoTurnoMaquina {
        val estrategia = obterEstrategiaIA(dificuldade)
        return jogarTurnoMaquinaComHistorico { opcoes ->
            estrategia.escolher(opcoes, contextoAtualIA())
        }
    }

    /**
     * Aplica a pontuacao final da rodada para humano e IA.
     */
    fun somarPontos() {
        val (pontosHumano, pontosMaquina) = when (estado.vencedor?.nome) {
            "Humano" -> 3 to 0
            "Maquina" -> 0 to 3
            else -> 1 to 1
        }
        estado.jogadorHumano.adicionarPontuacao(pontosHumano)
        estado.jogadorMaquina.adicionarPontuacao(pontosMaquina)
    }
}
