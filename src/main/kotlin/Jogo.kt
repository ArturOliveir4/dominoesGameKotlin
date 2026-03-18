enum class Dificuldade {
    FACIL,
    MEDIO,
    DIFICIL
}

enum class ModoJogo {
    CLASSICO,
    PONTOS
}

class Jogo {
    private var pontaEsquerda: Int = -1
    private var pontaDireita: Int = -1
    private var mesa: MutableList<Peca> = mutableListOf()
    private var montante: ArrayDeque<Peca> = ArrayDeque()
    private var jogadorHumano: Jogador = Jogador("Humano")
    private var jogadorMaquina: Jogador = Jogador("Maquina")
    private var turno: Boolean = true
    private var turnoPassadoSemJogar: Int = 0
    private var vencedor: Jogador? = null
    private var modo: ModoJogo = ModoJogo.CLASSICO

    private data class LadosJogada(val podeEsquerda: Boolean, val podeDireita: Boolean)

    private data class OpcaoJogada(
        val peca: Peca,
        val lados: LadosJogada,
        val soma: Int = peca.getLadoEsquerdo() + peca.getLadoDireito()
    )

    constructor()

    private fun definirDificuldadeHumano(dificuldade: Dificuldade) {
        val dificuldadeTexto = when (dificuldade) {
            Dificuldade.FACIL -> "Facil"
            Dificuldade.MEDIO -> "Medio"
            Dificuldade.DIFICIL -> "Dificil"
        }
        jogadorHumano.setDificuldade(dificuldadeTexto)
    }

    private fun resetarPartida() {
        pontaEsquerda = -1
        pontaDireita = -1
        mesa.clear()
        montante.clear()
        jogadorHumano.getMao().clear()
        jogadorMaquina.getMao().clear()
        turno = true
        turnoPassadoSemJogar = 0
        vencedor = null
    }

    private fun iniciarPartida(
        maxValorPeca: Int,
        pecasNaMao: Int,
        dificuldade: Dificuldade,
        modo: ModoJogo
    ) {
        resetarPartida()
        definirDificuldadeHumano(dificuldade)
        this.modo = modo

        for (i in 0..maxValorPeca) {
            for (j in i..maxValorPeca) {
                montante.addLast(Peca(i, j))
            }
        }

        montante = ArrayDeque(montante.shuffled())

        repeat(pecasNaMao) {
            jogadorMaquina.getMao().add(montante.removeFirst())
            jogadorHumano.getMao().add(montante.removeFirst())
        }
    }

    fun iniciarJogo(dificuldade: Dificuldade, modo: ModoJogo) {
        iniciarPartida(maxValorPeca = 6, pecasNaMao = 7, dificuldade = dificuldade, modo = modo)
    }

    fun iniciarJogoRapido(dificuldade: Dificuldade, modo: ModoJogo) {
        iniciarPartida(maxValorPeca = 2, pecasNaMao = 3, dificuldade = dificuldade, modo = modo)
    }

    private fun primeiraJogadaComLimite(maxDupla: Int) {
        for (i in maxDupla downTo 0) {
            val duplaMaquina = jogadorMaquina.getMao().find {
                it.getLadoEsquerdo() == i && it.getLadoDireito() == i
            }
            if (duplaMaquina != null) {
                jogarPeca(duplaMaquina)
                jogadorMaquina.getMao().remove(duplaMaquina)
                turno = true
                return
            }

            val duplaHumano = jogadorHumano.getMao().find {
                it.getLadoEsquerdo() == i && it.getLadoDireito() == i
            }
            if (duplaHumano != null) {
                jogarPeca(duplaHumano)
                jogadorHumano.getMao().remove(duplaHumano)
                turno = false
                return
            }
        }

        val pecaMaquina = jogadorMaquina.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }
        val pecaHumano = jogadorHumano.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }

        if (pecaMaquina == null || pecaHumano == null) return

        val somaHumano = pecaHumano.getLadoEsquerdo() + pecaHumano.getLadoDireito()
        val somaMaquina = pecaMaquina.getLadoEsquerdo() + pecaMaquina.getLadoDireito()

        if (somaHumano >= somaMaquina) {
            mesa.add(pecaHumano)
            jogadorHumano.getMao().remove(pecaHumano)
            turno = true
        } else {
            mesa.add(pecaMaquina)
            jogadorMaquina.getMao().remove(pecaMaquina)
            turno = false
        }

        pontaEsquerda = mesa.first().getLadoEsquerdo()
        pontaDireita = mesa.last().getLadoDireito()
    }

    fun primeiraJogada() {
        primeiraJogadaComLimite(6)
    }

    fun primeiraJogadaRapido() {
        primeiraJogadaComLimite(2)
    }

    fun jogarPeca(peca: Peca) {
        mesa.add(peca)
        pontaEsquerda = mesa.first().getLadoEsquerdo()
        pontaDireita = mesa.last().getLadoDireito()
    }

    private fun ladosValidosParaPeca(peca: Peca): LadosJogada {
        val podeEsquerda =
            peca.getLadoEsquerdo() == pontaEsquerda || peca.getLadoDireito() == pontaEsquerda
        val podeDireita =
            peca.getLadoEsquerdo() == pontaDireita || peca.getLadoDireito() == pontaDireita
        return LadosJogada(podeEsquerda, podeDireita)
    }

    private fun existeJogadaValida(lados: LadosJogada): Boolean {
        return lados.podeEsquerda || lados.podeDireita
    }

    private fun opcoesJogadaMaquina(): List<OpcaoJogada> {
        return jogadorMaquina.getMao().mapNotNull { peca ->
            val lados = ladosValidosParaPeca(peca)
            if (existeJogadaValida(lados)) OpcaoJogada(peca, lados) else null
        }
    }

    private fun comprarDoMontante(): Peca? {
        if (montante.isEmpty()) return null
        return montante.removeFirst()
    }

    fun maoValidaHumano(): Boolean {
        return jogadorHumano.getMao().any { peca ->
            existeJogadaValida(ladosValidosParaPeca(peca))
        }
    }

    fun comprarMontanteHumano(): Peca {
        val pecaComprada = comprarDoMontante() ?: throw IllegalStateException("Montante vazio")
        jogadorHumano.getMao().add(pecaComprada)
        return pecaComprada
    }

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

    fun pecaValidaInterface(pecaEscolhida: Peca): Boolean {
        return existeJogadaValida(ladosValidosParaPeca(pecaEscolhida))
    }

    fun verificarPecaMaoJogador(peca: Peca): Boolean {
        return existeJogadaValida(ladosValidosParaPeca(peca))
    }

    fun jogarNaMesa(peca: Peca, lado: Char) {
        when (lado) {
            'E', 'e' -> {
                if (peca.getLadoDireito() != pontaEsquerda) {
                    peca.girar()
                }
                mesa.add(0, peca)
                pontaEsquerda = mesa.first().getLadoEsquerdo()
            }

            'D', 'd' -> {
                if (peca.getLadoEsquerdo() != pontaDireita) {
                    peca.girar()
                }
                mesa.add(peca)
                pontaDireita = mesa.last().getLadoDireito()
            }

            else -> println("Opção inválida!!")
        }
    }

    fun verificarFimDeJogo(): Boolean {
        if (jogadorHumano.getMao().isEmpty()) {
            vencedor = jogadorHumano
            return true
        } else if (jogadorMaquina.getMao().isEmpty()) {
            vencedor = jogadorMaquina
            return true
        }

        if (turnoPassadoSemJogar == 2) {
            val tamanhoHumano = jogadorHumano.getMao().size
            val tamanhoMaquina = jogadorMaquina.getMao().size

            if (tamanhoHumano < tamanhoMaquina) {
                vencedor = jogadorHumano
            } else if (tamanhoMaquina < tamanhoHumano) {
                vencedor = jogadorMaquina
            } else {
                val somaHumano = jogadorHumano.getMao().sumOf { it.getLadoEsquerdo() + it.getLadoDireito() }
                val somaMaquina = jogadorMaquina.getMao().sumOf { it.getLadoEsquerdo() + it.getLadoDireito() }

                if (somaHumano < somaMaquina) {
                    vencedor = jogadorHumano
                } else if (somaHumano > somaMaquina) {
                    vencedor = jogadorMaquina
                }
            }
            return true
        }

        return false
    }

    private fun jogarTurnoMaquina(seletor: (List<OpcaoJogada>) -> Pair<Peca, Char>): Peca? {
        while (true) {
            val opcoes = opcoesJogadaMaquina()
            if (opcoes.isNotEmpty()) {
                val (pecaEscolhida, ladoEscolhido) = seletor(opcoes)
                jogarNaMesa(pecaEscolhida, ladoEscolhido)
                jogadorMaquina.getMao().remove(pecaEscolhida)
                turnoPassadoSemJogar = 0
                return pecaEscolhida
            }

            val compra = comprarDoMontante()
            if (compra != null) {
                jogadorMaquina.getMao().add(compra)
            } else {
                turno = true
                turnoPassadoSemJogar++
                return null
            }
        }
    }

    fun jogarMaquina(dificuldade: Dificuldade): Peca? {
        return when (dificuldade) {
            Dificuldade.FACIL -> jogadaFacil()
            Dificuldade.MEDIO -> jogadaMedia()
            Dificuldade.DIFICIL -> jogadaDificil()
        }
    }

    fun jogadaFacil(): Peca? {
        return jogarTurnoMaquina { opcoes ->
            val escolha = opcoes.first()
            val lado = when {
                escolha.lados.podeEsquerda && !escolha.lados.podeDireita -> 'E'
                !escolha.lados.podeEsquerda && escolha.lados.podeDireita -> 'D'
                else -> 'D'
            }
            Pair(escolha.peca, lado)
        }
    }

    fun jogadaMedia(): Peca? {
        return jogarTurnoMaquina { opcoes ->
            val escolha = opcoes.maxByOrNull { it.soma } ?: opcoes.first()
            val lado = when {
                escolha.lados.podeEsquerda && !escolha.lados.podeDireita -> 'E'
                !escolha.lados.podeEsquerda && escolha.lados.podeDireita -> 'D'
                else -> if ((0..1).random() == 0) 'E' else 'D'
            }
            Pair(escolha.peca, lado)
        }
    }

    fun jogadaDificil(): Peca? {
        return jogarTurnoMaquina { opcoes ->
            val frequenciaNumeros = mutableMapOf<Int, Int>()
            for (peca in jogadorMaquina.getMao()) {
                frequenciaNumeros[peca.getLadoEsquerdo()] = (frequenciaNumeros[peca.getLadoEsquerdo()] ?: 0) + 1
                frequenciaNumeros[peca.getLadoDireito()] = (frequenciaNumeros[peca.getLadoDireito()] ?: 0) + 1
            }

            val escolha = opcoes.maxWithOrNull(
                compareBy<OpcaoJogada> {
                    (frequenciaNumeros[it.peca.getLadoEsquerdo()] ?: 0) +
                            (frequenciaNumeros[it.peca.getLadoDireito()] ?: 0)
                }.thenBy { it.soma }
            ) ?: opcoes.first()

            val ladoPreferido = if ((frequenciaNumeros[pontaEsquerda] ?: 0) >=
                (frequenciaNumeros[pontaDireita] ?: 0)
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

            Pair(escolha.peca, lado)
        }
    }

    fun somarPontos() {
        var totalPontos = 0
        jogadorHumano.getMao().forEach { peca ->
            totalPontos += peca.getLadoEsquerdo() + peca.getLadoDireito()
        }
        getJogadorMaquina().setPontuacao(totalPontos)

        totalPontos = 0
        jogadorMaquina.getMao().forEach { peca ->
            totalPontos += peca.getLadoEsquerdo() + peca.getLadoDireito()
        }
        getJogadorHumano().setPontuacao(totalPontos)
    }

    fun getTurno(): Boolean {
        return turno
    }

    fun getMesa(): MutableList<Peca> {
        return mesa
    }

    fun getJogadorHumano(): Jogador {
        return jogadorHumano
    }

    fun getJogadorMaquina(): Jogador {
        return jogadorMaquina
    }

    fun getMontante(): MutableList<Peca> {
        return montante.toMutableList()
    }

    fun setTurno(turno: Boolean) {
        this.turno = turno
    }

    fun getVencedor(): Jogador? {
        return vencedor
    }

    fun setTurnoPassadoSemJogar(turnoPassadoSemJogar: Int) {
        this.turnoPassadoSemJogar = turnoPassadoSemJogar
    }

    fun incrementarTurnoSemPassar() {
        turnoPassadoSemJogar++
    }

    fun getModo(): ModoJogo {
        return modo
    }
}
