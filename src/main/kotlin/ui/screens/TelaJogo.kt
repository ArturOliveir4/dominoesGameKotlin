package ui.screens

import app.DominoApp
import app.GameSession
import app.executarComDelay
import domain.game.Jogo
import domain.game.Peca
import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.PauseTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import ui.game.ControladorHudJogo
import ui.game.RenderizadorMesaJogo
import java.util.IdentityHashMap

class TelaJogo(private val app: DominoApp) {

    @Suppress("AssignedValueIsNeverRead", "SpellCheckingInspection")
    /**
     * Monta a tela principal da partida e coordena o ciclo de turnos.
     */
    fun mostrar(stage: Stage) {
        app.jogo.iniciarJogo(GameSession.dificuldade)
        app.jogo.primeiraJogada()

        val mensagem = Label("Começo de jogo!")
        val rotuloTurno = Label("Sua vez")
        rotuloTurno.styleClass.addAll("status-turno", "status-humano")
        val contadorMaquina = HBox(5.0)
        val contadorHumano = HBox(5.0)
        val painelRaiz = VBox(10.0)

        val painelMesa = Pane()
        val painelMao = HBox(10.0)
        val painelAcoes = HBox(10.0)
        val painelVisualMontante = Pane()
        val rotuloMontante = Label()
        val rotuloTituloMontante = Label("MONTANTE")
        val painelMontante = VBox(4.0, rotuloTituloMontante, painelVisualMontante, rotuloMontante)
        contadorHumano.isVisible = false
        contadorHumano.isManaged = false
        val painelInfo = HBox(20.0, contadorMaquina)
        val painelTopo = Pane()
        painelTopo.children.addAll(painelInfo, painelMontante)
        configurarLayoutInicial(
            painelRaiz = painelRaiz,
            painelTopo = painelTopo,
            painelInfo = painelInfo,
            painelMontante = painelMontante,
            rotuloTituloMontante = rotuloTituloMontante,
            rotuloMontante = rotuloMontante,
            painelVisualMontante = painelVisualMontante,
            painelMesa = painelMesa,
            painelMao = painelMao,
            painelAcoes = painelAcoes,
            contadorHumano = contadorHumano,
            contadorMaquina = contadorMaquina
        )

        val controladorHud = ControladorHudJogo(app, rotuloTurno, contadorHumano, contadorMaquina)
        val renderizadorMesa = RenderizadorMesaJogo(app, painelMesa, painelRaiz) { app.jogo }

        var assinaturaMaoAnterior = ""
        val botoesMaoPorPeca = IdentityHashMap<Peca, Button>()
        var acoesMontadasHumano = false
        var turnoMaquinaAgendado = false
        var encerrandoPartida = false
        var sequenciaAberturaIAConcluida = false
        var reservaLateralEscolhaAtiva = false
        var aguardandoConclusaoZoomEscolha = false
        var montanteVisualOverride: Int? = null
        var contadorMaquinaVisualOverride: Int? = null
        lateinit var atualizarInterface: () -> Unit

        // Posiciona botoes de escolha de lado ao redor da mesa.
        fun posicionarBotoesEscolhaLado(btnEsquerda: Button, btnDireita: Button) {
            renderizadorMesa.posicionarBotoesEscolhaLado(btnEsquerda, btnDireita)
        }

        // Redesenha o montante visual com base no total atual de pecas.
        fun atualizarVisualMontante() {
            val quantidadeRestante = montanteVisualOverride ?: app.jogo.quantidadeMontante
            rotuloMontante.text = "$quantidadeRestante peça(s)"
            painelVisualMontante.children.clear()

            if (quantidadeRestante == 0) {
                rotuloMontante.text = "Montante vazio"
                return
            }

            val pilhaVisivel = quantidadeRestante.coerceAtMost(12)
            val larguraVerso = 30.0
            val alturaVerso = 45.0
            val passoX = 3.2
            val passoY = 1.9
            val larguraPilha = larguraVerso + (pilhaVisivel - 1) * passoX
            val alturaPilha = alturaVerso + (pilhaVisivel - 1) * passoY
            val inicioX = (painelVisualMontante.prefWidth - larguraPilha) / 2.0
            val inicioY = (painelVisualMontante.prefHeight - alturaPilha) / 2.0

            for (indice in 0 until pilhaVisivel) {
                val verso = app.servicoImagem.criarVisualImagem("/images/verso.png", larguraVerso, alturaVerso)
                verso.layoutX = inicioX + (indice * passoX)
                verso.layoutY = inicioY + ((pilhaVisivel - 1 - indice) * passoY)
                verso.rotate = if (indice % 2 == 0) -3.5 else 3.5
                verso.opacity = 0.96 - (indice * 0.03)
                painelVisualMontante.children.add(verso)
            }
        }

        // Atualiza contador da IA considerando overrides durante animacoes.
        fun atualizarContadorMaquinaVisual() {
            val quantidade = contadorMaquinaVisualOverride ?: app.jogo.jogadorMaquina.mao.size
            controladorHud.atualizarContadorMaquina(quantidade)
        }

        // Anima a retirada visual da peca do topo do montante.
        fun animarSaidaDoTopoDoMontante(onFinished: () -> Unit) {
            val topoAtual = painelVisualMontante.children.lastOrNull()
            val pecaAnimada = app.servicoImagem.criarVisualImagem("/images/verso.png", 30.0, 45.0)
            pecaAnimada.layoutX = topoAtual?.layoutX ?: ((painelVisualMontante.prefWidth - 30.0) / 2.0)
            pecaAnimada.layoutY = topoAtual?.layoutY ?: ((painelVisualMontante.prefHeight - 45.0) / 2.0)
            painelVisualMontante.children.add(pecaAnimada)

            val subir = TranslateTransition(Duration.millis(210.0), pecaAnimada)
            subir.byY = -16.0
            subir.byX = 12.0

            val reduzir = ScaleTransition(Duration.millis(210.0), pecaAnimada)
            reduzir.toX = 0.8
            reduzir.toY = 0.8

            val desaparecer = FadeTransition(Duration.millis(210.0), pecaAnimada)
            desaparecer.fromValue = 1.0
            desaparecer.toValue = 0.0

            val saida = ParallelTransition(subir, reduzir, desaparecer)
            saida.setOnFinished {
                painelVisualMontante.children.remove(pecaAnimada)
                onFinished()
            }
            saida.play()
        }

        // Executa turno completo da IA, incluindo compras e jogada final.
        fun executarTurnoDaMaquina() {
            reservaLateralEscolhaAtiva = false
            aguardandoConclusaoZoomEscolha = false
            assinaturaMaoAnterior = ""

            val montanteAntes = app.jogo.quantidadeMontante
            val maoMaquinaAntes = app.jogo.jogadorMaquina.mao.size
            val resultadoTurno = app.jogo.jogarMaquinaComHistorico(GameSession.dificuldade)
            val compras = resultadoTurno.pecasCompradas

            montanteVisualOverride = montanteAntes
            contadorMaquinaVisualOverride = maoMaquinaAntes
            atualizarVisualMontante()
            atualizarContadorMaquinaVisual()

            // Finaliza estado do turno da IA e devolve vez ao humano.
            fun finalizarTurnoMaquina() {
                montanteVisualOverride = null
                contadorMaquinaVisualOverride = null

                if (resultadoTurno.pecaJogada != null) {
                    mensagem.text =
                        "A máquina jogou: ${resultadoTurno.pecaJogada.ladoEsquerdo} | ${resultadoTurno.pecaJogada.ladoDireito}"
                } else if (compras.isNotEmpty()) {
                    mensagem.text = "A máquina comprou, mas não conseguiu jogar."
                } else {
                    mensagem.text = "A máquina passou a vez (sem jogadas e com montante vazio)."
                }

                app.jogo.definirTurnoHumano(true)
                turnoMaquinaAgendado = false
                controladorHud.atualizarIndicadorTurno(true)
                controladorHud.atualizarContadorMaquina(app.jogo.jogadorMaquina.mao.size)
                atualizarInterface()
            }

            // Reproduz compras sequenciais da IA para "feedback" visual.
            fun animarCompraIA(indice: Int) {
                if (indice >= compras.size) {
                    finalizarTurnoMaquina()
                    return
                }

                mensagem.text = "IA comprando do montante..."

                animarSaidaDoTopoDoMontante {
                    montanteVisualOverride = (montanteVisualOverride ?: montanteAntes) - 1
                    contadorMaquinaVisualOverride = (contadorMaquinaVisualOverride ?: maoMaquinaAntes) + 1
                    atualizarVisualMontante()
                    atualizarContadorMaquinaVisual()

                    executarComDelay(0.85) {
                        animarCompraIA(indice + 1)
                    }
                }
            }

            animarCompraIA(0)
        }

        // Aplica jogada do humano no lado escolhido e atualiza "interface".
        fun jogarPecaHumano(peca: Peca, lado: Char) {
            reservaLateralEscolhaAtiva = false
            aguardandoConclusaoZoomEscolha = false
            app.jogo.jogarNaMesa(peca, lado)
            app.jogo.removerPecaHumano(peca)

            painelMao.children.removeIf { no ->
                (no as? Button)?.userData === peca
            }

            mensagem.text = "Você jogou: ${peca.ladoEsquerdo} | ${peca.ladoDireito}"
            app.jogo.definirTurnoHumano(false)
            app.jogo.definirTurnosSemJogar(0)
            controladorHud.atualizarContadorHumano(app.jogo.jogadorHumano.mao.size)
            atualizarInterface()
        }

        // Cria botao temporario para escolha de lado quando ha dupla opcao.
        fun criarBotaoEscolhaLado(peca: Peca, lado: Char): Button {
            val botao = controladorHud.criarBotaoAcao("", "botao-direita-mao", 1.1)
            botao.isMouseTransparent = false
            botao.prefHeight = 40.0
            botao.minHeight = 40.0
            botao.maxHeight = 40.0
            botao.prefWidth = 80.0
            botao.minWidth = 80.0
            botao.maxWidth = 80.0
            botao.graphic = HBox(2.0, app.fabricaVisualPeca.criarDivisorVertical(30.0)).also { it.alignment = Pos.CENTER }

            botao.setOnAction {
                painelMesa.children.remove(botao)
                jogarPecaHumano(peca, lado)
            }

            return botao
        }

        // Sincroniza e renderiza mao do humano, habilitando apenas jogadas validas.
        fun renderizarMaoHumano(forcar: Boolean = false) {
            val maoHumano = app.jogo.jogadorHumano.mao
            val assinaturaMaoAtual = app.assinaturaPecas(maoHumano)
            if (!forcar && assinaturaMaoAtual == assinaturaMaoAnterior && painelMao.children.isNotEmpty()) {
                return
            }

            val maoAtualSet = maoHumano.toSet()
            val iteradorMapa = botoesMaoPorPeca.entries.iterator()
            while (iteradorMapa.hasNext()) {
                val entrada = iteradorMapa.next()
                if (entrada.key !in maoAtualSet) {
                    painelMao.children.remove(entrada.value)
                    iteradorMapa.remove()
                }
            }

            for (peca in maoHumano) {
                val btn = botoesMaoPorPeca.getOrPut(peca) {
                    val novoBtn = app.fabricaVisualPeca.criarBotaoPecaMao(peca)
                    novoBtn.userData = peca
                    app.servicoAnimacao.aplicarHoverComEscala(novoBtn, 1.1)

                    novoBtn.setOnAction {
                        if (aguardandoConclusaoZoomEscolha) {
                            return@setOnAction
                        }

                        val opc = app.jogo.jogarPecaInterfaceLado(peca)
                        if (opc != -1) {
                            when (opc) {
                                0 -> jogarPecaHumano(peca, 'E')
                                1 -> jogarPecaHumano(peca, 'D')
                                2 -> {
                                    reservaLateralEscolhaAtiva = true
                                    aguardandoConclusaoZoomEscolha = true
                                    renderizadorMesa.renderizar(forcar = true, reservaLateralEscolhaAtiva = true)

                                    val btnDireita = criarBotaoEscolhaLado(peca, 'D')
                                    val btnEsquerda = criarBotaoEscolhaLado(peca, 'E')

                                    btnDireita.isDisable = true
                                    btnEsquerda.isDisable = true
                                    btnDireita.userData = "ESCOLHA_LADO"
                                    btnEsquerda.userData = "ESCOLHA_LADO"

                                    posicionarBotoesEscolhaLado(btnEsquerda, btnDireita)

                                    painelMesa.children.add(btnDireita)
                                    painelMesa.children.add(btnEsquerda)

                                    val liberarEscolha = PauseTransition(Duration.millis(220.0))
                                    liberarEscolha.setOnFinished {
                                        aguardandoConclusaoZoomEscolha = false
                                        btnDireita.isDisable = false
                                        btnEsquerda.isDisable = false
                                    }
                                    liberarEscolha.play()
                                }
                            }
                        }
                    }

                    novoBtn
                }

                btn.scaleX = 1.0
                btn.scaleY = 1.0
                btn.translateX = 0.0
                btn.translateY = 0.0
                btn.isDisable = !app.jogo.verificarPecaMaoJogador(peca)
            }

            val ordemAtual = maoHumano.mapNotNull { botoesMaoPorPeca[it] }
            painelMao.children.setAll(ordemAtual)

            assinaturaMaoAnterior = assinaturaMaoAtual
        }

        // Monta a area de acoes do jogador (voltar e compra no montante).
        fun renderizarAcoesJogador() {
            if (acoesMontadasHumano) return

            painelAcoes.children.clear()

            // Executa regras de compra do humano e transicao de turno quando necessario.
            fun executarAcaoComprar() {
                if (encerrandoPartida) {
                    return
                }

                if (!app.jogo.turnoHumano) {
                    mensagem.text = "Aguarde sua vez para comprar."
                    return
                }

                if (app.jogo.maoValidaHumano()) {
                    mensagem.text = "Você tem jogadas disponíveis! Não é permitido comprar peças!"
                } else if (app.jogo.montanteVazio) {
                    mensagem.text = "O montante está vazio!"
                    app.jogo.definirTurnoHumano(false)
                    app.jogo.incrementarTurnosSemJogar()
                } else {
                    val pecaComprada = app.jogo.comprarMontanteHumano()
                    mensagem.text = "Você comprou: ${pecaComprada.ladoEsquerdo} | ${pecaComprada.ladoDireito}"
                    controladorHud.atualizarContadorHumano(app.jogo.jogadorHumano.mao.size)
                    animarSaidaDoTopoDoMontante {
                        atualizarInterface()
                    }
                    return
                }
                atualizarInterface()
            }

            painelMontante.setOnMouseClicked {
                executarAcaoComprar()
            }

            val botaoSair = controladorHud.criarBotaoAcao("Voltar", "bota-sair-mao")

            botaoSair.setOnAction {
                app.jogo = Jogo()
                stage.scene = app.telaInicial(stage)
                app.ativarTelaCheia(stage)
            }

            painelAcoes.children.add(botaoSair)
            acoesMontadasHumano = true
        }

        controladorHud.centralizarContadores()
        controladorHud.atualizarContadorHumano(app.jogo.jogadorHumano.mao.size)
        controladorHud.atualizarContadorMaquina(app.jogo.jogadorMaquina.mao.size)
        atualizarVisualMontante()

        // Funcao central de refresh: fim de jogo, turno humano e turno da IA.
        atualizarInterface = atualizar@{
            if (app.jogo.verificarFimDeJogo()) {
                if (encerrandoPartida) return@atualizar
                encerrandoPartida = true

                app.jogo.somarPontos()
                controladorHud.atualizarIndicadorFimRodada(app.jogo.vencedor?.nome)
                mensagem.text = "Fim da rodada! Clique em 'Ver resultado' para continuar."
                turnoMaquinaAgendado = false
                acoesMontadasHumano = false
                painelMao.children.forEach { it.isDisable = true }
                painelMesa.children.removeIf { no -> (no as? Button)?.userData == "ESCOLHA_LADO" }
                painelAcoes.children.clear()

                val botaoVerResultado = controladorHud.criarBotaoAcao("Ver resultado", "botao-sair-mao")
                botaoVerResultado.setOnAction {
                    stage.scene = app.telaFimDeJogo(stage)
                    app.ativarTelaCheia(stage)
                }
                painelAcoes.children.add(botaoVerResultado)
                atualizarVisualMontante()
                return@atualizar
            }

            atualizarVisualMontante()

            renderizadorMesa.renderizar(reservaLateralEscolhaAtiva = reservaLateralEscolhaAtiva)

            if (app.jogo.turnoHumano) {
                turnoMaquinaAgendado = false
                controladorHud.atualizarIndicadorTurno(true)

                if (!app.jogo.maoValidaHumano() && app.jogo.montanteVazio) {
                    mensagem.text = "Você não tem jogadas e o montante está vazio. Passando a vez..."
                    app.jogo.definirTurnoHumano(false)
                    app.jogo.incrementarTurnosSemJogar()
                    acoesMontadasHumano = false
                    executarComDelay(0.55) {
                        atualizarInterface()
                    }
                    return@atualizar
                }

                renderizarMaoHumano()
                renderizarAcoesJogador()
            } else {
                if (turnoMaquinaAgendado) return@atualizar
                turnoMaquinaAgendado = true
                controladorHud.atualizarIndicadorTurno(false)

                if (painelMao.children.isEmpty()) {
                    renderizarMaoHumano(forcar = true)
                }

                painelMao.children.forEach {
                    it.isDisable = true
                    it.scaleX = 1.0
                    it.scaleY = 1.0
                    it.translateX = 0.0
                    it.translateY = 0.0
                }
                acoesMontadasHumano = false

                if (!sequenciaAberturaIAConcluida) {
                    sequenciaAberturaIAConcluida = true
                    mensagem.text = "A IA começa esta rodada. Veja a jogada inicial..."

                    executarComDelay(1.8) {
                        mensagem.text = "IA pensando na jogada inicial..."
                        executarComDelay(1.8) {
                            executarTurnoDaMaquina()
                        }
                    }
                } else {
                    mensagem.text = "IA pensando..."
                    executarComDelay(1.8) {
                        executarTurnoDaMaquina()
                    }
                }
            }
        }

        painelRaiz.children.setAll(painelTopo, rotuloTurno, mensagem, painelMesa, painelMao, painelAcoes)
        painelRaiz.alignment = Pos.CENTER

        stage.title = "Dominó!"
        stage.scene = app.atualizarCena(painelRaiz, 600.0, 400.0)
        app.ativarTelaCheia(stage)
        stage.show()

        atualizarInterface()
        Platform.runLater {
            renderizadorMesa.renderizar(forcar = true, reservaLateralEscolhaAtiva = reservaLateralEscolhaAtiva)
        }
    }

    /**
     * Configura propriedades de layout e vínculos do topo da tela.
     */
    private fun configurarLayoutInicial(
        painelRaiz: VBox,
        painelTopo: Pane,
        painelInfo: HBox,
        painelMontante: VBox,
        rotuloTituloMontante: Label,
        rotuloMontante: Label,
        painelVisualMontante: Pane,
        painelMesa: Pane,
        painelMao: HBox,
        painelAcoes: HBox,
        contadorHumano: HBox,
        contadorMaquina: HBox
    ) {
        painelMesa.prefWidth = 1000.0
        painelMesa.minWidth = 600.0
        painelMesa.prefHeight = 220.0
        painelMesa.minHeight = 220.0
        painelMesa.maxHeight = 220.0

        painelMao.alignment = Pos.CENTER
        painelMao.prefHeight = 96.0
        painelMao.minHeight = 96.0
        painelMao.maxHeight = 96.0

        painelAcoes.alignment = Pos.CENTER
        painelAcoes.prefHeight = 42.0
        painelAcoes.minHeight = 42.0
        painelAcoes.maxHeight = 42.0

        painelMontante.alignment = Pos.CENTER
        painelMontante.styleClass.add("painel-montante-clickavel")
        rotuloTituloMontante.styleClass.add("montante-titulo")
        rotuloMontante.styleClass.add("montante-legenda")
        painelVisualMontante.prefWidth = 165.0
        painelVisualMontante.prefHeight = 94.0
        painelVisualMontante.minWidth = 165.0
        painelVisualMontante.minHeight = 94.0
        painelMontante.maxWidth = Region.USE_PREF_SIZE
        painelMontante.maxHeight = Region.USE_PREF_SIZE

        painelInfo.maxWidth = Region.USE_PREF_SIZE
        painelInfo.alignment = Pos.CENTER
        contadorHumano.alignment = Pos.CENTER
        contadorMaquina.alignment = Pos.CENTER

        painelTopo.prefHeight = 120.0
        painelTopo.minHeight = 120.0
        painelTopo.maxWidth = Double.MAX_VALUE
        painelInfo.layoutY = 12.0
        painelMontante.layoutY = 0.0

        painelInfo.layoutXProperty().bind(
            Bindings.createDoubleBinding(
                { (painelRaiz.width - painelInfo.width) / 2.0 },
                painelRaiz.widthProperty(),
                painelInfo.widthProperty()
            )
        )
        painelMontante.layoutXProperty().bind(
            Bindings.createDoubleBinding(
                { painelRaiz.width - painelMontante.width - 14.0 },
                painelRaiz.widthProperty(),
                painelMontante.widthProperty()
            )
        )

        VBox.setMargin(painelMesa, javafx.geometry.Insets(40.0, 0.0, 40.0, 0.0))
        VBox.setMargin(painelTopo, javafx.geometry.Insets(0.0, 0.0, 40.0, 0.0))
        VBox.setMargin(painelMao, javafx.geometry.Insets(0.0, 0.0, 30.0, 0.0))
    }
}
