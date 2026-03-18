import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.application.Platform
import javafx.scene.layout.Region
import javafx.animation.ScaleTransition
import javafx.util.Duration
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.animation.PauseTransition
import javafx.animation.FadeTransition
import javafx.animation.TranslateTransition
import javafx.animation.ParallelTransition
import javafx.scene.input.KeyCombination
import javafx.scene.Parent
import javafx.scene.Node

// Váriavel global da dificuldade
object GameSession {
    var dificuldade: Dificuldade = Dificuldade.FACIL
    var modo: ModoJogo = ModoJogo.CLASSICO
}

// Funçãoo utilizada no turno da máquina
fun executarComDelay(delaySegundos: Double, acao: () -> Unit) {
    val pause = PauseTransition(Duration.seconds(delaySegundos))
    pause.setOnFinished {
        acao()
    }
    pause.play()
}

class DominóFX : Application() {

    private companion object {
        const val LARGURA_PECA_MESA = 80.0
        const val ALTURA_PECA_MESA = 40.0
        const val PASSO_PECA_MESA = 86.0
        const val LARGURA_PECA_MAO = 40.0
        const val ALTURA_PECA_MAO = 80.0
        const val LARGURA_ICONE_PECA = 30.0
        const val ALTURA_ICONE_PECA = 30.0
        const val LARGURA_MESA_PREF = 1000.0
        const val LARGURA_MESA_MIN = 600.0
        const val ALTURA_MESA = 220.0
    }

    // Inicializando as váriaveis principais de controle
    internal var jogo = Jogo()
    private lateinit var root: VBox
    private val cacheImagens = mutableMapOf<String, Image>()
    private val styleUrl by lazy { javaClass.getResource("/styles/style.css")!!.toExternalForm() }
    private lateinit var cenaPrincipal: Scene
    private val telaInicialScreen by lazy { TelaInicialScreen(this) }
    private val telaJogoScreen by lazy { TelaJogoScreen(this) }
    private val telaFimRodadaScreen by lazy { TelaFimRodadaScreen(this) }
    private val telaFimJogoScreen by lazy { TelaFimJogoScreen(this) }

    internal fun atualizarCena(novoRoot: Parent, largura: Double = 600.0, altura: Double = 400.0): Scene {
        if (!::cenaPrincipal.isInitialized) {
            cenaPrincipal = Scene(novoRoot, largura, altura)
            cenaPrincipal.stylesheets.add(styleUrl)
        } else {
            cenaPrincipal.root = novoRoot
            if (!cenaPrincipal.stylesheets.contains(styleUrl)) {
                cenaPrincipal.stylesheets.add(styleUrl)
            }
        }
        return cenaPrincipal
    }

    private fun getImagem(caminho: String): Image {
        return cacheImagens.getOrPut(caminho) {
            Image(javaClass.getResource(caminho)!!.toExternalForm())
        }
    }

    internal fun criarImageView(caminho: String, largura: Double, altura: Double): ImageView {
        val imageView = ImageView(getImagem(caminho))
        imageView.fitWidth = largura
        imageView.fitHeight = altura
        imageView.isPreserveRatio = true
        return imageView
    }

    internal fun ativarTelaCheia(stage: Stage) {
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.fullScreenExitHint = ""
        if (!stage.isFullScreen) {
            stage.isFullScreen = true
        }
    }

    internal fun aplicarHoverComEscala(
        botao: Button,
        escalaHover: Double,
        duracaoMs: Double = 150.0
    ) {
        botao.setOnMouseEntered {
            val st = ScaleTransition(Duration.millis(duracaoMs), botao)
            st.toX = escalaHover
            st.toY = escalaHover
            st.play()
        }

        botao.setOnMouseExited {
            val st = ScaleTransition(Duration.millis(duracaoMs), botao)
            st.toX = 1.0
            st.toY = 1.0
            st.play()
        }
    }

    internal fun animarEntradaNaMesa(no: Node, entrouNaEsquerda: Boolean, atrasoMs: Double = 200.0) {
        no.opacity = 0.0
        no.translateX = if (entrouNaEsquerda) -45.0 else 45.0
        no.scaleX = 0.9
        no.scaleY = 0.9

        val fade = FadeTransition(Duration.millis(260.0), no)
        fade.fromValue = 0.0
        fade.toValue = 1.0

        val slide = TranslateTransition(Duration.millis(260.0), no)
        slide.fromX = if (entrouNaEsquerda) -45.0 else 45.0
        slide.toX = 0.0

        val zoom = ScaleTransition(Duration.millis(260.0), no)
        zoom.fromX = 0.9
        zoom.fromY = 0.9
        zoom.toX = 1.0
        zoom.toY = 1.0

        val entrada = ParallelTransition(fade, slide, zoom)
        entrada.delay = Duration.millis(atrasoMs)
        entrada.play()
    }

    // Função que gera os ícones referentes a quantidade de peças restantes dos jogadores
    internal fun gerarIconesRestantes(quantidade: Int): HBox {
        val container = HBox(5.0)
        repeat(quantidade) {
            container.children.add(criarImageView("/images/verso.png", 20.0, 30.0))
        }
        return container
    }

    internal fun assinaturaPecas(pecas: List<Peca>): String {
        return pecas.joinToString(";") { "${it.getLadoEsquerdo()}-${it.getLadoDireito()}" }
    }

    internal fun snapshotMesa(pecas: List<Peca>): List<Int> {
        return pecas.map { System.identityHashCode(it) }
    }

    internal fun criarDivisorVertical(altura: Double): Region {
        val linhaDivisoria = Region()
        linhaDivisoria.style = "-fx-background-color: black;"
        linhaDivisoria.prefWidth = 2.0
        linhaDivisoria.prefHeight = altura
        linhaDivisoria.minWidth = 2.0
        linhaDivisoria.maxWidth = 2.0
        return linhaDivisoria
    }

    internal fun criarDivisorHorizontal(largura: Double): Region {
        val linhaDivisoria = Region()
        linhaDivisoria.style = "-fx-background-color: black;"
        linhaDivisoria.prefWidth = largura
        linhaDivisoria.prefHeight = 2.0
        linhaDivisoria.minWidth = largura
        linhaDivisoria.maxWidth = largura
        linhaDivisoria.minHeight = 2.0
        linhaDivisoria.maxHeight = 2.0
        return linhaDivisoria
    }

    internal fun criarBotaoPecaMesa(peca: Peca): Button {
        val btn = Button()
        btn.isMouseTransparent = true
        btn.styleClass.add("botao-peca")
        btn.prefHeight = ALTURA_PECA_MESA
        btn.minHeight = ALTURA_PECA_MESA
        btn.maxHeight = ALTURA_PECA_MESA
        btn.prefWidth = LARGURA_PECA_MESA
        btn.minWidth = LARGURA_PECA_MESA
        btn.maxWidth = LARGURA_PECA_MESA

        val ladoEsquerdoImage = criarImageView("/images/${peca.getLadoEsquerdo()}.png", LARGURA_ICONE_PECA, ALTURA_ICONE_PECA)
        val ladoDireitoImage = criarImageView("/images/${peca.getLadoDireito()}.png", LARGURA_ICONE_PECA, ALTURA_ICONE_PECA)
        val hbox = HBox(2.0, ladoEsquerdoImage, criarDivisorVertical(30.0), ladoDireitoImage)
        hbox.alignment = Pos.CENTER
        btn.graphic = hbox
        return btn
    }

    internal fun criarBotaoPecaMao(peca: Peca): Button {
        val btn = Button()
        btn.styleClass.add("botao-peca-mao")
        btn.prefHeight = ALTURA_PECA_MAO
        btn.minHeight = ALTURA_PECA_MAO
        btn.maxHeight = ALTURA_PECA_MAO
        btn.prefWidth = LARGURA_PECA_MAO
        btn.minWidth = LARGURA_PECA_MAO
        btn.maxWidth = LARGURA_PECA_MAO

        val ladoEsquerdoImage = criarImageView("/imagesVertical/${peca.getLadoEsquerdo()}.png", LARGURA_ICONE_PECA, ALTURA_ICONE_PECA)
        val ladoDireitoImage = criarImageView("/imagesVertical/${peca.getLadoDireito()}.png", LARGURA_ICONE_PECA, ALTURA_ICONE_PECA)
        val vbox = VBox(2.0, ladoEsquerdoImage, criarDivisorHorizontal(30.0), ladoDireitoImage)
        vbox.alignment = Pos.CENTER
        btn.graphic = vbox
        return btn
    }

    // Função que inicializa a interface gráfica
    override fun start(stage: Stage) {
        val cena = telaInicial(stage) // cria a cena
        stage.scene = cena
        ativarTelaCheia(stage)
        stage.show()

    }

    // Tela inicial (Começar, Dificuldade, Sair)
    fun telaInicial(stage: Stage): Scene {
        return telaInicialScreen.criar(stage)
    }


    fun mostrarTelaJogo(stage: Stage) {
        telaJogoScreen.mostrar(stage)
    }

    fun telaFimRodada(stage: Stage) : Scene {
        return telaFimRodadaScreen.criar(stage)
    }


    // Tela de conclusão após o fim de jogo (Menu inicial, Sair)
    fun telaFimDeJogo(stage: Stage) : Scene {
        return telaFimJogoScreen.criar(stage)
    }
}

fun main(){
    Application.launch(DominóFX::class.java)
}