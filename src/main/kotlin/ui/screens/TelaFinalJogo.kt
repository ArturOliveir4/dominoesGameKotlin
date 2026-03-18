package ui.screens

import app.DominoApp
import domain.game.Jogo
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class TelaFinalJogo(private val app: DominoApp) {
    /**
     * Monta a tela de encerramento com resultado, ranking e navegacao.
     */
    fun criar(stage: Stage): Scene {
        app.servicoRankingUi.registrarResultadoNoRanking(app.jogo)

        val vencedor = app.jogo.vencedor
        val mensagemFinal = if (vencedor != null) {
            if (vencedor.nome == "Maquina") "A máquina venceu!" else "Você venceu!"
        } else {
            "Empate"
        }

        val label = Label(mensagemFinal)
        val acoesPane = HBox(5.0)
        acoesPane.alignment = Pos.CENTER

        val painelRanking = app.servicoRankingUi.criarPainelTop5Ranking()
        val conteudoTelaFinal = mutableListOf<Node>(label, painelRanking)

        if (app.jogo.houveDesempatePorSomaDaMao()) {
            val maoIa = app.jogo.jogadorMaquina.mao
            val maoHumano = app.jogo.jogadorHumano.mao
            val somaIa = maoIa.sumOf { it.ladoEsquerdo + it.ladoDireito }
            val somaHumano = maoHumano.sumOf { it.ladoEsquerdo + it.ladoDireito }
            val textoMaoIa = if (maoIa.isEmpty()) {
                "(sem peças)"
            } else {
                maoIa.joinToString("   ") { "[${it.ladoEsquerdo}|${it.ladoDireito}]" }
            }

            val painelConferencia = VBox(6.0)
            painelConferencia.alignment = Pos.CENTER
            painelConferencia.children.addAll(
                Label("Desempate por soma da mão aplicado."),
                Label("Mão da IA: $textoMaoIa"),
                Label("Soma IA: $somaIa | Sua soma: $somaHumano")
            )

            conteudoTelaFinal.add(painelConferencia)
        }

        conteudoTelaFinal.add(acoesPane)
        val telaFinal = VBox(20.0, *conteudoTelaFinal.toTypedArray())
        telaFinal.alignment = Pos.CENTER

        val botaoVoltar = Button("Menu Inicial")
        botaoVoltar.styleClass.add("botao-voltar-menu")
        app.servicoAnimacao.aplicarHoverComEscala(botaoVoltar, 1.05)

        botaoVoltar.setOnAction {
            app.jogo = Jogo()
            stage.scene = app.telaInicial(stage)
            app.ativarTelaCheia(stage)
        }

        val botaoSair = Button("Sair")
        botaoSair.styleClass.add("botao-sair-tela-final")
        app.servicoAnimacao.aplicarHoverComEscala(botaoSair, 1.05)

        botaoSair.setOnAction {
            Platform.exit()
        }

        botaoVoltar.alignment = Pos.CENTER
        botaoSair.alignment = Pos.CENTER
        acoesPane.children.add(botaoVoltar)
        acoesPane.children.add(botaoSair)

        app.ativarTelaCheia(stage)
        return app.atualizarCena(telaFinal, 800.0, 600.0)
    }
}
