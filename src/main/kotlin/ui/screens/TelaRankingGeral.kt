package ui.screens

import app.DominoApp
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ranking.model.RankingGeralEntry
import ranking.repository.RankingRepository

class TelaRankingGeral(private val app: DominoApp) {
    /**
     * Monta a tela com ranking agregado por jogador.
     */
    fun criar(stage: Stage): Scene {
        val titulo = javafx.scene.control.Label("Ranking Geral")
        titulo.styleClass.add("status-turno")

        val tabela = TableView<RankingGeralEntry>()
        tabela.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        tabela.prefWidth = 920.0
        tabela.prefHeight = 540.0

        val colJogador = TableColumn<RankingGeralEntry, String>("Jogador")
        colJogador.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.nomeJogador)
        }

        val colPontos = TableColumn<RankingGeralEntry, Number>("Pontos Totais")
        colPontos.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.pontosTotais)
        }

        val colPartidas = TableColumn<RankingGeralEntry, Number>("Partidas")
        colPartidas.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.partidas)
        }

        val colVitorias = TableColumn<RankingGeralEntry, Number>("Vitórias")
        colVitorias.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.vitorias)
        }

        val colEmpates = TableColumn<RankingGeralEntry, Number>("Empates")
        colEmpates.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.empates)
        }

        val colDerrotas = TableColumn<RankingGeralEntry, Number>("Derrotas")
        colDerrotas.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.derrotas)
        }

        tabela.columns.setAll(colJogador, colPontos, colPartidas, colVitorias, colEmpates, colDerrotas)
        tabela.items = FXCollections.observableArrayList(RankingRepository.rankingGeral())

        val botaoVoltar = BotaoNavegacaoFactory.criarBotaoVoltarParaInicio(app, stage)

        val root = VBox(16.0, titulo, tabela, botaoVoltar)
        root.alignment = Pos.CENTER
        root.padding = Insets(24.0)

        return app.atualizarCena(root, 1040.0, 700.0)
    }
}
