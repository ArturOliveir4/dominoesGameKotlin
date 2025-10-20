import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
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
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode

// Váriavel global da dificuldade
object GameSession {
    var dificuldade: Int = 0
}

// Funçãoo utilizada no turno da máquina
fun executarComDelay(delaySegundos: Double, acao: () -> Unit) {
    val pause = PauseTransition(Duration.seconds(delaySegundos))
    pause.setOnFinished {
        Platform.runLater {
            acao()
        }
    }
    pause.play()
}

class DominóFX : Application() {

    // Inicializando as váriaveis principais de controle
    private var jogo = Jogo()
    private lateinit var root: VBox

    // Função que gera os ícones referentes a quantidade de peças restantes dos jogadores
    private fun gerarIconesRestantes(quantidade: Int): HBox {
        val container = HBox(5.0)
        repeat(quantidade) {
            val img = ImageView(Image(javaClass.getResource("/images/verso.png").toExternalForm()))
            img.fitWidth = 20.0
            img.fitHeight = 30.0
            container.children.add(img)
        }
        return container
    }

    // Função que auxilia na geração das peças (Número -> Imagem)
    fun getImagemLado(valor: Int): ImageView {
        val caminho = when (valor) {
            0 -> "/images/0.png"
            1 -> "/images/1.png"
            2 -> "/images/2.png"
            3 -> "/images/3.png"
            4 -> "/images/4.png"
            5 -> "/images/5.png"
            6 -> "/images/6.png"
            else -> "/images/vazio.png"
        }

        val imagem = Image(javaClass.getResourceAsStream(caminho))
        val imageView = ImageView(imagem)
        imageView.fitWidth = 30.0
        imageView.fitHeight = 30.0
        imageView.isPreserveRatio = true

        return imageView
    }

    // Função que inicializa a interface gráfica
    override fun start(stage: Stage) {
        val cena = telaInicial(stage) // cria a cena
        cena.stylesheets.add(javaClass.getResource("/styles/style.css").toExternalForm()) // linka o CSS
        stage.scene = cena
        stage.isFullScreen = true
        stage.show()
    }

    // Tela inicial (Começar, Dificuldade, Sair)
    fun telaInicial(stage: Stage): Scene {
        val rootInicial = VBox(20.0)
        rootInicial.alignment = Pos.CENTER

        val titulo = Label("Bem-vindo ao Dominó!")

        val botaoComecar = Button("Começar Jogo")
        val botaoSair = Button("Sair")
        val botaoDificuldade = Button("Dificuldade")

        botaoComecar.styleClass.add("botao-comecar")
        botaoSair.styleClass.add("botao-sair")
        botaoDificuldade.styleClass.add("botao-dificuldade")

        val opcoesDificuldade = HBox(10.0)
        opcoesDificuldade.isVisible = false
        opcoesDificuldade.isManaged = false
        opcoesDificuldade.alignment = Pos.CENTER

        val facil = Button("Fácil")
        val medio = Button("Médio")
        val dificil = Button("Difícil")

        facil.styleClass.add("botao-facil")
        medio.styleClass.add("botao-facil")
        dificil.styleClass.add("botao-facil")

        botaoComecar.setOnAction {
            mostrarTelaJogo(stage)
        }

        botaoDificuldade.setOnAction {
            val novoEstado = !opcoesDificuldade.isVisible
            opcoesDificuldade.isVisible = novoEstado
            opcoesDificuldade.isManaged = novoEstado
        }

        facil.setOnAction {
            GameSession.dificuldade = 0
            titulo.text = "Dificuldade alterada (Fácil)"
        }

        medio.setOnAction {
            GameSession.dificuldade = 1
            titulo.text = "Dificuldade alterada (Média)"
        }

        dificil.setOnAction {
            GameSession.dificuldade = 2
            titulo.text = "Dificuldade alterada (Difícil)"
        }

        botaoSair.setOnAction {
            Platform.exit()
        }

        opcoesDificuldade.children.addAll(facil, medio, dificil)
        val menuVBox = VBox(15.0, botaoDificuldade, opcoesDificuldade)
        menuVBox.alignment = Pos.CENTER

        rootInicial.children.addAll(titulo, botaoComecar, menuVBox, botaoSair)

        val sceneInicial = Scene(rootInicial, 600.0, 400.0)
        sceneInicial.stylesheets.add(javaClass.getResource("/styles/style.css")!!.toExternalForm())
        stage.isFullScreen = true

        return sceneInicial
    }


    fun mostrarTelaJogo(stage: Stage) {
        jogo.iniciarJogo()
        jogo.primeiraJogada()

        val mensagem = Label("Começo de jogo!")
        val contadorMaquina = HBox(5.0)
        val contadorHumano = HBox(5.0)

        val mesaPane = FlowPane()
        val maoPane = HBox(10.0)
        val acoesPane = HBox(10.0)
        val infoPane = HBox(20.0, contadorHumano, contadorMaquina)
        infoPane.alignment = Pos.TOP_LEFT
        mesaPane.alignment = Pos.CENTER
        maoPane.alignment = Pos.CENTER
        acoesPane.alignment = Pos.CENTER
        infoPane.alignment = Pos.CENTER
        contadorHumano.alignment = Pos.CENTER
        contadorMaquina.alignment = Pos.CENTER
        VBox.setMargin(mesaPane, javafx.geometry.Insets(40.0, 0.0, 40.0, 0.0))
        VBox.setMargin(infoPane, javafx.geometry.Insets(0.0, 0.0, 50.0, 0.0))
        VBox.setMargin(maoPane, javafx.geometry.Insets(0.0, 0.0, 30.0, 0.0))

        root = VBox(10.0)

        contadorHumano.children.add(Label("JOGADOR: "))
        contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
        contadorMaquina.children.add(Label("CPU: "))
        contadorMaquina.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))

        // Função correspondente a tela do jogo em si (Gerencia o fluxo do jogo)
        fun atualizarInterface() {

            // Verificando o fim de jogo a cada atualização de interface
            if(jogo.verificarFimDeJogo()){
                mensagem.text = "O JOGO ACABOU!!!!!"
                stage.scene = telaFimDeJogo(stage)
                stage.isFullScreen = true
                return
            }

            // Peças da mesa
            mesaPane.children.clear()
            for (peca in jogo.getMesa()){
                val btn = Button()
                btn.isMouseTransparent = true
                btn.styleClass.add("botao-peca")
                btn.prefHeight = 40.0
                btn.minHeight = 40.0
                btn.maxHeight = 40.0
                btn.prefWidth = 80.0
                btn.minWidth = 80.0
                btn.maxWidth = 80.0

                // Carrega imagens dos lados da peça
                val ladoEsquerdoImage = ImageView(Image(javaClass.getResource("/images/${peca.getLadoEsquerdo()}.png")!!.toExternalForm()))
                val ladoDireitoImage = ImageView(Image(javaClass.getResource("/images/${peca.getLadoDireito()}.png")!!.toExternalForm()))

                // Ajusta tamanho das imagens
                val ladoWidth = 30.0
                val ladoHeight = 30.0
                ladoEsquerdoImage.fitWidth = ladoWidth
                ladoEsquerdoImage.fitHeight = ladoHeight
                ladoEsquerdoImage.isPreserveRatio = true
                ladoDireitoImage.fitWidth = ladoWidth
                ladoDireitoImage.fitHeight = ladoHeight
                ladoDireitoImage.isPreserveRatio = true

                // Linha divisória da peça
                val linhaDivisoria = Region()
                linhaDivisoria.style = "-fx-background-color: black;"
                linhaDivisoria.prefWidth = 2.0
                linhaDivisoria.prefHeight = ladoHeight
                linhaDivisoria.minWidth = 2.0
                linhaDivisoria.maxWidth = 2.0

                // HBox para juntar as imagens e a linha
                val hbox = HBox(2.0, ladoEsquerdoImage, linhaDivisoria, ladoDireitoImage)
                hbox.alignment = Pos.CENTER
                btn.graphic = hbox

                mesaPane.children.add(btn)
            }

            // Turno do jogador
            if (jogo.getTurno()){
                // Limpando a mão
                maoPane.children.clear()
                // Criando a mão novamente atualizada
                for (peca in jogo.getJogadorHumano().getMao()){
                    val btn = Button()
                    btn.styleClass.add("botao-peca-mao")
                    btn.prefHeight = 80.0
                    btn.minHeight = 80.0
                    btn.maxHeight = 80.0
                    btn.prefWidth = 40.0
                    btn.minWidth = 40.0
                    btn.maxWidth = 40.0

                    // Carrega imagens dos lados da peça
                    val ladoEsquerdoImage = ImageView(Image(javaClass.getResource("/imagesVertical/${peca.getLadoEsquerdo()}.png")!!.toExternalForm()))
                    val ladoDireitoImage = ImageView(Image(javaClass.getResource("/imagesVertical/${peca.getLadoDireito()}.png")!!.toExternalForm()))

                    // Ajusta tamanho das imagens
                    val ladoWidth = 30.0
                    val ladoHeight = 30.0
                    ladoEsquerdoImage.fitWidth = ladoWidth
                    ladoEsquerdoImage.fitHeight = ladoHeight
                    ladoEsquerdoImage.isPreserveRatio = true
                    ladoDireitoImage.fitWidth = ladoWidth
                    ladoDireitoImage.fitHeight = ladoHeight
                    ladoDireitoImage.isPreserveRatio = true

                    // Linha divisória
                    val linhaDivisoria = Region()
                    linhaDivisoria.style = "-fx-background-color: black;"
                    linhaDivisoria.prefWidth = 30.0
                    linhaDivisoria.prefHeight = 2.0
                    linhaDivisoria.minWidth = 30.0
                    linhaDivisoria.maxWidth = 30.0
                    linhaDivisoria.minHeight = 2.0
                    linhaDivisoria.maxHeight = 2.0

                    // VBox para juntar as imagens verticalmente
                    val vbox = VBox(2.0, ladoEsquerdoImage, linhaDivisoria, ladoDireitoImage)
                    vbox.alignment = Pos.CENTER

                    btn.graphic = vbox

                    // Efeito hover: aumenta o tamanho e adiciona sombra
                    btn.setOnMouseEntered {
                        btn.style = """
                            -fx-background-color: white;
                            -fx-border-color: black;
                            -fx-border-width: 2;
                            -fx-pref-width: 50px;
                            -fx-pref-height: 100px;
                            -fx-background-radius: 10px;
                            -fx-border-radius: 10px;
                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);
                            -fx-cursor: hand;
                        """.trimIndent()

                        val st = ScaleTransition(Duration.millis(150.0), btn)
                        st.toX = 1.1
                        st.toY = 1.1
                        st.play()
                    }

                    btn.setOnMouseExited {
                        btn.style = """
                            -fx-background-color: white;
                            -fx-border-color: black;
                            -fx-border-width: 2;
                            -fx-pref-width: 40px;
                            -fx-pref-height: 80px;
                            -fx-background-radius: 10px;
                            -fx-border-radius: 10px;
                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.0), 0, 0, 0, 0);
                        """.trimIndent()

                        val st = ScaleTransition(Duration.millis(150.0), btn)
                        st.toX = 1.0
                        st.toY = 1.0
                        st.play()
                    }

                    btn.setOnAction {
                        val opc = jogo.jogarPecaInterfaceLado(peca)
                        if (opc != -1) {
                            when (opc) {
                                0 -> {
                                    jogo.jogarNaMesa(peca, 'E')
                                    jogo.getJogadorHumano().getMao().remove(peca)

                                    mensagem.text = "Você jogou: ${peca.getLadoEsquerdo()} | ${peca.getLadoDireito()}"
                                    jogo.setTurno(false)
                                    jogo.setTurnoPassadoSemJogar(0)
                                    contadorHumano.children.clear()
                                    contadorHumano.children.add(Label("JOGADOR: "))
                                    contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
                                    atualizarInterface()
                                }
                                1 -> {
                                    jogo.jogarNaMesa(peca, 'D')
                                    jogo.getJogadorHumano().getMao().remove(peca)

                                    mensagem.text = "Você jogou: ${peca.getLadoEsquerdo()} | ${peca.getLadoDireito()}"
                                    jogo.setTurno(false)
                                    jogo.setTurnoPassadoSemJogar(0)
                                    contadorHumano.children.clear()
                                    contadorHumano.children.add(Label("JOGADOR: "))
                                    contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
                                    atualizarInterface()
                                }
                                2 -> {
                                    val btnDireita = Button()
                                    btnDireita.isMouseTransparent = false
                                    btnDireita.styleClass.add("botao-direita-mao")

                                    btnDireita.setOnMouseEntered {
                                        btnDireita.style = """
                                            -fx-background-color: #77e099;   
                                            -fx-border-color: #1b5e20;       
                                            -fx-border-width: 3;
                                            -fx-text-fill: white;
                                            -fx-font-size: 16px;
                                            -fx-font-weight: bold;
                                            -fx-background-radius: 12px;
                                            -fx-border-radius: 12px;
                                            -fx-pref-width: 95px;             
                                            -fx-pref-height: 55px;
                                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 4);
                                            -fx-cursor: hand;
                                        """.trimIndent()
                                        val st = ScaleTransition(Duration.millis(150.0), btnDireita)
                                        st.toX = 1.1  // aumenta 20% na largura
                                        st.toY = 1.1  // aumenta 20% na altura
                                        st.play()
                                    }

                                    btnDireita.setOnMouseExited {
                                        btnDireita.style = """
                                            -fx-background-color: #99ffbb;
                                            -fx-border-color: #2e7d32;
                                            -fx-border-width: 3;
                                            -fx-text-fill: white;
                                            -fx-font-size: 16px;
                                            -fx-font-weight: bold;
                                            -fx-background-radius: 12px;
                                            -fx-border-radius: 12px;
                                            -fx-pref-width: 90px;
                                            -fx-pref-height: 50px;
                                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);
                                            -fx-cursor: hand;
                                        """.trimIndent()
                                        val st = ScaleTransition(Duration.millis(150.0), btnDireita)
                                        st.toX = 1.0
                                        st.toY = 1.0
                                        st.play()
                                    }

                                    btnDireita.prefHeight = 40.0
                                    btnDireita.minHeight = 40.0
                                    btnDireita.maxHeight = 40.0
                                    btnDireita.prefWidth = 80.0
                                    btnDireita.minWidth = 80.0
                                    btnDireita.maxWidth = 80.0

                                    // Linha divisória
                                    val linhaDivisoria = Region()
                                    linhaDivisoria.style = "-fx-background-color: black;"
                                    linhaDivisoria.prefWidth = 2.0
                                    linhaDivisoria.prefHeight = ladoHeight
                                    linhaDivisoria.minWidth = 2.0
                                    linhaDivisoria.maxWidth = 2.0

                                    // HBox para juntar as imagens e a linha
                                    val hbox = HBox(2.0, linhaDivisoria)
                                    hbox.alignment = Pos.CENTER

                                    btnDireita.graphic = hbox
                                    mesaPane.children.add(btnDireita)

                                    btnDireita.setOnAction{
                                        jogo.jogarNaMesa(peca, 'D')
                                        jogo.getJogadorHumano().getMao().remove(peca)
                                        mesaPane.children.remove(btnDireita)

                                        mensagem.text = "Você jogou: ${peca.getLadoEsquerdo()} | ${peca.getLadoDireito()}"
                                        jogo.setTurno(false)
                                        jogo.setTurnoPassadoSemJogar(0)
                                        // contadorHumano.text = "Suas peças: ${jogo.getJogadorHumano().getMao().size}"
                                        contadorHumano.children.clear()
                                        contadorHumano.children.add(Label("JOGADOR: "))
                                        contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
                                        atualizarInterface()
                                    }

                                    val btnEsquerda = Button()
                                    btnEsquerda.isMouseTransparent = false
                                    btnEsquerda.styleClass.add("botao-direita-mao")

                                    btnEsquerda.setOnMouseEntered {
                                        btnEsquerda.style = """
                                            -fx-background-color: #77e099;   
                                            -fx-border-color: #1b5e20;       
                                            -fx-border-width: 3;
                                            -fx-text-fill: white;
                                            -fx-font-size: 16px;
                                            -fx-font-weight: bold;
                                            -fx-background-radius: 12px;
                                            -fx-border-radius: 12px;
                                            -fx-pref-width: 95px;             
                                            -fx-pref-height: 55px;
                                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 4);
                                            -fx-cursor: hand;
                                        """.trimIndent()
                                        val st = ScaleTransition(Duration.millis(150.0), btnEsquerda)
                                        st.toX = 1.1  // aumenta 20% na largura
                                        st.toY = 1.1  // aumenta 20% na altura
                                        st.play()
                                    }

                                    btnEsquerda.setOnMouseExited {
                                        btnEsquerda.style = """
                                            -fx-background-color: #99ffbb;
                                            -fx-border-color: #2e7d32;
                                            -fx-border-width: 3;
                                            -fx-text-fill: white;
                                            -fx-font-size: 16px;
                                            -fx-font-weight: bold;
                                            -fx-background-radius: 12px;
                                            -fx-border-radius: 12px;
                                            -fx-pref-width: 90px;
                                            -fx-pref-height: 50px;
                                            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);
                                            -fx-cursor: hand;
                                        """.trimIndent()
                                        val st = ScaleTransition(Duration.millis(150.0), btnEsquerda)
                                        st.toX = 1.0
                                        st.toY = 1.0
                                        st.play()
                                    }

                                    btnEsquerda.prefHeight = 40.0
                                    btnEsquerda.minHeight = 40.0
                                    btnEsquerda.maxHeight = 40.0
                                    btnEsquerda.prefWidth = 80.0
                                    btnEsquerda.minWidth = 80.0
                                    btnEsquerda.maxWidth = 80.0

                                    val hboxO = HBox(2.0, linhaDivisoria)
                                    hboxO.alignment = Pos.CENTER

                                    btnEsquerda.graphic = hboxO
                                    mesaPane.children.add(0, btnEsquerda)

                                    btnEsquerda.setOnAction{
                                        jogo.jogarNaMesa(peca, 'E')
                                        jogo.getJogadorHumano().getMao().remove(peca)
                                        mesaPane.children.remove(btnEsquerda)

                                        mensagem.text = "Você jogou: ${peca.getLadoEsquerdo()} | ${peca.getLadoDireito()}"
                                        jogo.setTurno(false)
                                        jogo.setTurnoPassadoSemJogar(0)
                                        // contadorHumano.text = "Suas peças: ${jogo.getJogadorHumano().getMao().size}"
                                        contadorHumano.children.clear()
                                        contadorHumano.children.add(Label("JOGADOR: "))
                                        contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
                                        atualizarInterface()
                                    }
                                }
                            }
                        }
                    }
                    // Verificando se a peça é válida para jogar (se não ela fica desabilitada)
                    btn.isDisable = !jogo.verificarPecaMaoJogador(peca)

                    maoPane.children.add(btn)
                }

                // Botão de comprar montante
                acoesPane.children.clear()
                val comprarBtn = Button("Comprar")
                comprarBtn.styleClass.add("botao-comprar")

                comprarBtn.setOnMouseEntered {
                    comprarBtn.style = """
                    -fx-background-color: #9a9a9a;  /* tom mais claro ao passar o mouse */
                    -fx-text-fill: white;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-padding: 5 10 5 10;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                    -fx-cursor: hand;
                """.trimIndent()

                    val st = ScaleTransition(Duration.millis(150.0), comprarBtn)
                    st.toX = 1.05
                    st.toY = 1.05
                    st.play()
                }

                comprarBtn.setOnMouseExited {
                    comprarBtn.style = """
                    -fx-background-color: #828181;  
                    -fx-text-fill: white;            
                    -fx-font-size: 12px;             
                    -fx-font-weight: bold;            
                    -fx-background-radius: 8;        
                    -fx-padding: 5 10 5 10;          
                    -fx-cursor: hand;
                    -fx-effect: none;
                """.trimIndent()

                    val st = ScaleTransition(Duration.millis(150.0), comprarBtn)
                    st.toX = 1.0
                    st.toY = 1.0
                    st.play()
                }

                comprarBtn.setOnAction {
                    if (jogo.maoValidaHumano()){
                        mensagem.text = "Você tem jogadas disponíveis! Não é permitido comprar peças!"
                    } else if (jogo.getMontante().isEmpty()) {
                        mensagem.text = "O montante está vazio!"
                        jogo.setTurno(false)
                        jogo.incrementarTurnoSemPassar()
                    } else {
                        val pecaComprada = jogo.comprarMontanteHumano()
                        mensagem.text = "Você comprou: ${pecaComprada.getLadoEsquerdo()} | ${pecaComprada.getLadoDireito()}"
                        contadorHumano.children.clear()
                        contadorHumano.children.add(Label("JOGADOR: "))
                        contadorHumano.children.add(gerarIconesRestantes(jogo.getJogadorHumano().getMao().size))
                    }
                    atualizarInterface()
                }

                val botaoSair = Button("Voltar")
                botaoSair.styleClass.add("bota-sair-mao")

                botaoSair.setOnMouseEntered {
                    botaoSair.style = """
                        -fx-background-color: #e06b6b;  /* tom mais claro ao passar o mouse */
                        -fx-text-fill: white;
                        -fx-font-size: 12px;
                        -fx-font-weight: bold;
                        -fx-background-radius: 8;
                        -fx-padding: 5 10 5 10;
                        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                        -fx-cursor: hand;
                    """.trimIndent()

                    val st = ScaleTransition(Duration.millis(150.0), botaoSair)
                    st.toX = 1.05
                    st.toY = 1.05
                    st.play()
                }

                botaoSair.setOnMouseExited {
                    botaoSair.style = """
                        -fx-background-color: #d9534f;  
                        -fx-text-fill: white;            
                        -fx-font-size: 12px;             
                        -fx-font-weight: bold;            
                        -fx-background-radius: 8;        
                        -fx-padding: 5 10 5 10;          
                        -fx-cursor: hand;
                        -fx-effect: none;
                    """.trimIndent()

                    val st = ScaleTransition(Duration.millis(150.0), botaoSair)
                    st.toX = 1.0
                    st.toY = 1.0
                    st.play()
                }

                botaoSair.setOnAction {
                    // Reiniciando o jogo
                    jogo = Jogo()

                    // Volta para a tela inicial
                    stage.scene = telaInicial(stage)
                    stage.isFullScreen = true
                }

                acoesPane.children.add(comprarBtn)
                acoesPane.children.add(botaoSair)
            } else {
                // Desabilita todos os botões durante a transição de turnos
                maoPane.children.forEach { it.isDisable = true }

                // Delay para o turno da máquina (troca de turno mais natural)
                executarComDelay(1.0) {
                    // Turno da máquina
                    maoPane.children.clear()
                    acoesPane.children.clear()

                    val pecaJogadaMaquina = jogo.jogarMaquina(GameSession.dificuldade)
                    if (pecaJogadaMaquina != null){
                        mensagem.text =
                            "A máquina jogou: ${pecaJogadaMaquina.getLadoEsquerdo()} | ${pecaJogadaMaquina.getLadoDireito()}"
                    }

                    // Mudando o turno
                    jogo.setTurno(true)

                    contadorMaquina.children.clear()
                    contadorMaquina.children.add(Label("CPU: "))
                    contadorMaquina.children.add(gerarIconesRestantes(jogo.getJogadorMaquina().getMao().size))

                    atualizarInterface()
                }
            }
        }

        root = VBox(10.0, infoPane, mensagem, mesaPane, maoPane, acoesPane)
        root.alignment = Pos.CENTER

        val scene = Scene(root, 600.0, 400.0)
        scene.stylesheets.add(javaClass.getResource("/styles/style.css")!!.toExternalForm())

        stage.title = "Dominó!"
        stage.scene = scene
        stage.isFullScreen = true
        stage.show()

        atualizarInterface()
    }

    // Tela de conclusão após o fim de jogo (Menu inicial, Sair)
    fun telaFimDeJogo(stage: Stage) : Scene {
        val vencedor = jogo.getVencedor()
        val mensagemFinal = if (vencedor != null) {
            if (vencedor.getNome() == "Maquina") "A máquina venceu!" else "Você venceu!"
        } else {
            "Empate"
        }

        val label = Label(mensagemFinal)
        val acoesPane = HBox(5.0)
        acoesPane.alignment = Pos.CENTER

        val telaFinal = VBox(20.0, label, acoesPane)
        telaFinal.alignment = Pos.CENTER

        val botaoVoltar = Button("Menu Inicial")
        botaoVoltar.styleClass.add("botao-voltar-menu")

        botaoVoltar.setOnMouseEntered {
            botaoVoltar.style = """
                -fx-background-color: #3bd46f;  
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 5 10 5 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                -fx-cursor: hand;
            """.trimIndent()

            val st = ScaleTransition(Duration.millis(150.0), botaoVoltar)
            st.toX = 1.05
            st.toY = 1.05
            st.play()
        }

        botaoVoltar.setOnMouseExited {
            botaoVoltar.style = """
                -fx-background-color: #26ff6f;  
                -fx-text-fill: white;            
                -fx-font-size: 12px;             
                -fx-font-weight: bold;            
                -fx-background-radius: 8;        
                -fx-padding: 5 10 5 10;          
                -fx-cursor: hand;
                -fx-effect: none;
            """.trimIndent()

            val st = ScaleTransition(Duration.millis(150.0), botaoVoltar)
            st.toX = 1.0
            st.toY = 1.0
            st.play()
        }

        botaoVoltar.setOnAction {
            // Reinicia o jogo
            jogo = Jogo()

            // Volta para a tela inicial
            stage.scene = telaInicial(stage)
            stage.isFullScreen = true
        }

        val botaoSair = Button("Sair")
        botaoSair.styleClass.add("botao-sair-tela-final")

        botaoSair.setOnMouseEntered {
            botaoSair.style = """
                -fx-background-color: #f7485c;  
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 5 10 5 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                -fx-cursor: hand;
            """.trimIndent()

            val st = ScaleTransition(Duration.millis(150.0), botaoSair)
            st.toX = 1.05
            st.toY = 1.05
            st.play()
        }

        botaoSair.setOnMouseExited {
            botaoSair.style = """
                -fx-background-color: #a30013;  
                -fx-text-fill: white;            
                -fx-font-size: 12px;             
                -fx-font-weight: bold;            
                -fx-background-radius: 8;        
                -fx-padding: 5 10 5 10;          
                -fx-cursor: hand;
                -fx-effect: none;
            """.trimIndent()

            val st = ScaleTransition(Duration.millis(150.0), botaoSair)
            st.toX = 1.0
            st.toY = 1.0
            st.play()
        }

        botaoSair.setOnAction {
            Platform.exit()
        }

        botaoVoltar.alignment = Pos.CENTER
        botaoSair.alignment = Pos.CENTER
        acoesPane.children.add(botaoVoltar)
        acoesPane.children.add(botaoSair)

        val scene = Scene(telaFinal, 800.0, 600.0)
        scene.stylesheets.add(javaClass.getResource("/styles/style.css")!!.toExternalForm())
        stage.isFullScreen = true
        return scene
    }
}

fun main(){
    Application.launch(DominóFX::class.java)
}