package ui.screens

import app.DominoApp
import app.GameSession
import domain.game.Dificuldade
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class TelaInicial(private val app: DominoApp) {
    /**
     * Monta fluxo de entrada: inicio, nome e dificuldade.
     */
    fun criar(stage: Stage): Scene {
        val painelRaiz = AnchorPane()
        painelRaiz.padding = Insets(20.0)

        val centro = VBox(14.0)
        centro.alignment = Pos.CENTER

        val camadaCentral = StackPane(centro)
        camadaCentral.alignment = Pos.CENTER
        AnchorPane.setTopAnchor(camadaCentral, 0.0)
        AnchorPane.setBottomAnchor(camadaCentral, 0.0)
        AnchorPane.setLeftAnchor(camadaCentral, 0.0)
        AnchorPane.setRightAnchor(camadaCentral, 0.0)

        val painelRanking = app.servicoRankingUi.criarPainelTop5Ranking()
        val botaoRankingGeral = Button("Ranking Geral")
        botaoRankingGeral.styleClass.add("botao-topo")
        app.servicoAnimacao.aplicarHoverComEscala(botaoRankingGeral, 1.05)
        botaoRankingGeral.setOnAction {
            stage.scene = app.telaRankingGeral(stage)
            app.ativarTelaCheia(stage)
        }

        val painelHistorico5 = app.servicoRankingUi.criarPainelUltimas5Partidas()
        val botaoTodasPartidas = Button("Mostrar todas as partidas")
        botaoTodasPartidas.styleClass.add("botao-topo")
        app.servicoAnimacao.aplicarHoverComEscala(botaoTodasPartidas, 1.05)
        botaoTodasPartidas.setOnAction {
            stage.scene = app.telaRanking(stage)
            app.ativarTelaCheia(stage)
        }

        val linhaBotaoHistorico = HBox(botaoTodasPartidas)
        linhaBotaoHistorico.alignment = Pos.CENTER

        val linhaBotaoRanking = HBox(botaoRankingGeral)
        linhaBotaoRanking.alignment = Pos.CENTER

        val blocoHistorico = VBox(8.0, painelHistorico5, linhaBotaoHistorico)
        blocoHistorico.alignment = Pos.TOP_LEFT
        blocoHistorico.styleClass.add("painel-topo")

        val blocoRanking = VBox(8.0, painelRanking, linhaBotaoRanking)
        blocoRanking.alignment = Pos.TOP_RIGHT
        blocoRanking.styleClass.add("painel-topo")

        AnchorPane.setTopAnchor(blocoHistorico, 16.0)
        AnchorPane.setLeftAnchor(blocoHistorico, 16.0)
        AnchorPane.setTopAnchor(blocoRanking, 16.0)
        AnchorPane.setRightAnchor(blocoRanking, 16.0)

        val titulo = Label("Bem-vindo ao Dominó!")
        val subtitulo = Label("")

        val inputNome = TextField(GameSession.nomeJogador)
        inputNome.promptText = "Digite seu nome"
        inputNome.maxWidth = 260.0

        val botaoComecar = Button("Começar Jogo")
        botaoComecar.styleClass.add("botao-comecar")
        val botaoAvancar = Button("Avançar")
        botaoAvancar.styleClass.add("botao-comecar")
        val botaoIniciarDomino = Button("Iniciar Dominó")
        botaoIniciarDomino.styleClass.add("botao-comecar")
        val botaoVoltar = Button("Voltar")
        botaoVoltar.styleClass.add("botao-modo")
        val botaoSair = Button("Sair")
        botaoSair.styleClass.add("botao-sair")

        val facil = Button("Fácil")
        val medio = Button("Médio")
        val dificil = Button("Difícil")
        facil.styleClass.add("botao-facil")
        medio.styleClass.add("botao-facil")
        dificil.styleClass.add("botao-facil")

        app.servicoAnimacao.aplicarHoverComEscala(botaoComecar, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(botaoAvancar, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(botaoIniciarDomino, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(botaoVoltar, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(botaoSair, 1.05)

        app.servicoAnimacao.aplicarHoverComEscala(facil, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(medio, 1.05)
        app.servicoAnimacao.aplicarHoverComEscala(dificil, 1.05)

        var etapa = 0

        val linhaDificuldade = HBox(10.0, facil, medio, dificil)
        linhaDificuldade.alignment = Pos.CENTER
        val linhaAcoes = HBox(10.0, botaoVoltar, botaoSair)
        linhaAcoes.alignment = Pos.CENTER

        // Atualiza o resumo visivel com nome e dificuldade atuais.
        fun atualizarResumo() {
            val dificuldadeTxt = when (GameSession.dificuldade) {
                Dificuldade.FACIL -> "Fácil"
                Dificuldade.MEDIO -> "Médio"
                Dificuldade.DIFICIL -> "Difícil"
            }
            subtitulo.text = "Nome: ${GameSession.nomeJogador} | Dificuldade: $dificuldadeTxt"
        }

        // Renderiza os controles corretos para cada etapa do fluxo inicial.
        fun renderizarEtapa() {
            centro.children.clear()
            when (etapa) {
                0 -> {
                    subtitulo.text = ""
                    centro.children.addAll(titulo, botaoComecar, botaoSair)
                }

                1 -> {
                    subtitulo.text = "Digite seu nome para continuar"
                    centro.children.addAll(titulo, subtitulo, inputNome, botaoAvancar, linhaAcoes)
                }

                else -> {
                    atualizarResumo()
                    centro.children.addAll(
                        titulo,
                        Label("Escolha a dificuldade:"),
                        linhaDificuldade,
                        subtitulo,
                        botaoIniciarDomino,
                        linhaAcoes
                    )
                }
            }
        }

        botaoComecar.setOnAction {
            etapa = 1
            renderizarEtapa()
            inputNome.requestFocus()
        }

        botaoAvancar.setOnAction {
            val nome = inputNome.text.trim()
            if (nome.isNotEmpty()) {
                GameSession.nomeJogador = nome
                etapa = 2
                renderizarEtapa()
            } else {
                subtitulo.text = "Informe um nome válido para continuar"
            }
        }

        botaoVoltar.setOnAction {
            etapa = (etapa - 1).coerceAtLeast(0)
            renderizarEtapa()
        }

        facil.setOnAction {
            GameSession.dificuldade = Dificuldade.FACIL
            atualizarResumo()
        }
        medio.setOnAction {
            GameSession.dificuldade = Dificuldade.MEDIO
            atualizarResumo()
        }
        dificil.setOnAction {
            GameSession.dificuldade = Dificuldade.DIFICIL
            atualizarResumo()
        }

        botaoIniciarDomino.setOnAction {
            app.mostrarTelaJogo(stage)
        }

        botaoSair.setOnAction {
            Platform.exit()
        }

        painelRaiz.children.addAll(camadaCentral, blocoHistorico, blocoRanking)
        renderizarEtapa()

        return app.atualizarCena(painelRaiz, 900.0, 650.0)
    }
}
