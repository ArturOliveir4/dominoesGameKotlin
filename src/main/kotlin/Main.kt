package app

import app.services.FabricaVisualPeca
import app.services.ServicoAnimacao
import app.services.ServicoImagem
import app.services.ServicoRankingUi
import domain.game.Dificuldade
import domain.game.Jogo
import domain.game.Peca
import javafx.application.Application
import javafx.animation.PauseTransition
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.input.KeyCombination
import javafx.scene.Parent
import ranking.db.Database
import ui.screens.TelaFinalJogo
import ui.screens.TelaInicial
import ui.screens.TelaJogo
import ui.screens.TelaRankingGeral
import ui.screens.TelaHistoricoPartidas
import javafx.util.Duration

// Variável global da dificuldade
object GameSession {
    var dificuldade: Dificuldade = Dificuldade.FACIL
    var nomeJogador: String = "Humano"
}

/**
 * Executa uma ação após um pequeno atraso.
 * Usado para dar ritmo visual ao turno da IA.
 */
fun executarComDelay(delaySegundos: Double, acao: () -> Unit) {
    val pause = PauseTransition(Duration.seconds(delaySegundos))
    pause.setOnFinished {
        acao()
    }
    pause.play()
}

class DominoApp : Application() {
    internal var jogo = Jogo()
    internal val servicoAnimacao = ServicoAnimacao()
    internal val servicoImagem = ServicoImagem()
    internal val fabricaVisualPeca = FabricaVisualPeca(servicoImagem)
    internal val servicoRankingUi = ServicoRankingUi()
    private val urlEstilo by lazy { javaClass.getResource("/styles/style.css")!!.toExternalForm() }
    private lateinit var cenaPrincipal: Scene
    private val telaInicial by lazy { TelaInicial(this) }
    private val telaJogo by lazy { TelaJogo(this) }
    private val telaFinalJogo by lazy { TelaFinalJogo(this) }
    private val telaHistoricoPartidas by lazy { TelaHistoricoPartidas(this) }
    private val telaRankingGeral by lazy { TelaRankingGeral(this) }

    /**
     * Cria a cena principal na primeira chamada e reaproveita nas próximas,
     * trocando apenas o root para evitar recriação desnecessária.
     */
    internal fun atualizarCena(novoRoot: Parent, largura: Double = 600.0, altura: Double = 400.0): Scene {
        if (!::cenaPrincipal.isInitialized) {
            cenaPrincipal = Scene(novoRoot, largura, altura)
            cenaPrincipal.stylesheets.add(urlEstilo)
        } else {
            cenaPrincipal.root = novoRoot
            if (!cenaPrincipal.stylesheets.contains(urlEstilo)) {
                cenaPrincipal.stylesheets.add(urlEstilo)
            }
        }
        return cenaPrincipal
    }

    /**
     * Ativa tela cheia sem atalho de escape nem hint visual.
     */
    internal fun ativarTelaCheia(stage: Stage) {
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.fullScreenExitHint = ""
        if (!stage.isFullScreen) {
            stage.isFullScreen = true
        }
    }

    /**
     * Gera assinatura textual da mao para detectar mudanca de estado.
     */
    internal fun assinaturaPecas(pecas: List<Peca>): String {
        return pecas.joinToString(";") { "${it.ladoEsquerdo}-${it.ladoDireito}" }
    }

    /**
     * Captura identidade dos objetos da mesa para detectar nova peca inserida.
     */
    internal fun snapshotMesa(pecas: List<Peca>): List<Int> {
        return pecas.map { System.identityHashCode(it) }
    }

    /**
     * Inicializa banco e abre a primeira tela da aplicação.
     */
    override fun start(stage: Stage) {
        Database.init()
        val cena = telaInicial(stage) // cria a cena
        stage.scene = cena
        ativarTelaCheia(stage)
        stage.show()
    }

    /**
     * Retorna a cena da tela inicial.
     */
    fun telaInicial(stage: Stage): Scene {
        return telaInicial.criar(stage)
    }

    /**
     * Abre a tela principal de jogo.
     */
    fun mostrarTelaJogo(stage: Stage) {
        telaJogo.mostrar(stage)
    }

    /**
     * Retorna a cena de encerramento de rodada.
     */
    fun telaFimDeJogo(stage: Stage) : Scene {
        return telaFinalJogo.criar(stage)
    }

    /**
     * Retorna a cena com histórico de partidas.
     */
    fun telaRanking(stage: Stage): Scene {
        return telaHistoricoPartidas.criar(stage)
    }

    /**
     * Retorna a cena com "ranking" geral agregado.
     */
    fun telaRankingGeral(stage: Stage): Scene {
        return telaRankingGeral.criar(stage)
    }

}

/**
 * Ponto de entrada da aplicação JavaFX.
 */
fun main(){
    Application.launch(DominoApp::class.java)
}