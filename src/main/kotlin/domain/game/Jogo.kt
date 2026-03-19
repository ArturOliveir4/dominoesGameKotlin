package domain.game

import domain.ai.EstrategiaDificil
import domain.ai.EstrategiaFacil
import domain.ai.EstrategiaMedia

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
    private val estado = EstadoJogo()
    private val estrategiaFacil = EstrategiaFacil()
    private val estrategiaMedia = EstrategiaMedia()
    private val estrategiaDificil = EstrategiaDificil()
    private val motor = MotorJogo(
        estado = estado,
        estrategiaFacil = estrategiaFacil,
        estrategiaMedia = estrategiaMedia,
        estrategiaDificil = estrategiaDificil
    )

    val turnoHumano: Boolean
        get() = estado.turnoHumano

    val mesa: List<Peca>
        get() = estado.mesa

    val jogadorHumano: Jogador
        get() = estado.jogadorHumano

    val jogadorMaquina: Jogador
        get() = estado.jogadorMaquina

    val quantidadeMontante: Int
        get() = estado.montante.size

    val montanteVazio: Boolean
        get() = estado.montante.isEmpty()

    val vencedor: Jogador?
        get() = estado.vencedor

    /**
     * Define explicitamente se o turno atual pertence ao humano.
     */
    fun definirTurnoHumano(turnoHumano: Boolean) {
        estado.turnoHumano = turnoHumano
    }

    /**
     * Remove uma peca especifica da mao do jogador humano.
     */
    fun removerPecaHumano(peca: Peca) = estado.jogadorHumano.removerPeca(peca)

    /**
     * Define o valor do contador de turnos consecutivos sem jogada.
     */
    fun definirTurnosSemJogar(valor: Int) {
        estado.turnosSemJogar = valor
    }

    /**
     * Incrementa o contador de turnos sem jogada (usado para bloqueio).
     */
    fun incrementarTurnosSemJogar() {
        estado.turnosSemJogar++
    }

    /**
     * Inicializa uma nova partida com a dificuldade selecionada.
     */
    fun iniciarJogo(dificuldade: Dificuldade) = motor.iniciarPartida(dificuldade)

    /**
     * Executa a primeira jogada da rodada.
     */
    fun primeiraJogada() = motor.primeiraJogadaComLimite()

    /**
     * Coloca uma peca na mesa e atualiza as pontas (uso principal na abertura).
     */
    fun jogarPeca(peca: Peca) = motor.jogarPecaAbertura(peca)

    /**
     * Verifica se o jogador humano possui alguma jogada valida.
     */
    fun maoValidaHumano(): Boolean = motor.maoValidaHumano()

    /**
     * Compra peca para o humano e adiciona na mao.
     */
    fun comprarMontanteHumano(): Peca = motor.comprarMontanteHumano()

    /**
     * Diz para a "interface" se a peca joga na esquerda, direita, ambos ou nenhum.
     */
    fun jogarPecaInterfaceLado(pecaEscolhida: Peca): Int =
        motor.jogarPecaInterfaceLado(pecaEscolhida)

    /**
     * Atalho para validar se a peca escolhida e jogavel no estado atual.
     */
    fun pecaValidaInterface(pecaEscolhida: Peca): Boolean =
        motor.pecaValidaInterface(pecaEscolhida)

    /**
     * Usado pela UI para habilitar ou desabilitar uma peca da mao.
     */
    fun verificarPecaMaoJogador(peca: Peca): Boolean =
        motor.verificarPecaMaoJogador(peca)

    /**
     * Joga a peca no lado escolhido e gira automaticamente quando necessario.
     */
    fun jogarNaMesa(peca: Peca, lado: Char) = motor.jogarNaMesa(peca, lado)

    /**
     * Verifica condicoes de encerramento: mao vazia ou bloqueio da rodada.
     */
    fun verificarFimDeJogo(): Boolean = motor.verificarFimDeJogo()

    /**
     * Executa turno da IA com estrategia correspondente a dificuldade.
     */
    fun jogarMaquinaComHistorico(dificuldade: Dificuldade): ResultadoTurnoMaquina =
        motor.jogarMaquinaComHistorico(dificuldade)

    /**
     * Aplica a pontuacao final da rodada para humano e IA.
     */
    fun somarPontos() = motor.somarPontos()

    /**
     * Informa se houve desempate por soma das maos.
     */
    fun houveDesempatePorSomaDaMao(): Boolean = estado.desempatePorSomaDaMao
}
