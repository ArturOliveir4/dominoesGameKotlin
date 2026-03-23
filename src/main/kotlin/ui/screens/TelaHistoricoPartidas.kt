package ui.screens

import app.DominoApp
import domain.game.paraTextoResultado
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
import ranking.model.RankingEntry
import ranking.repository.RankingRepository
import java.time.format.DateTimeFormatter

class TelaHistoricoPartidas(private val app: DominoApp) {
    /**
     * Monta a tela com tabela completa de partidas registradas.
     */
    fun criar(stage: Stage): Scene {
        val titulo = javafx.scene.control.Label("Histórico de Partidas")
        titulo.styleClass.add("status-turno")

        val tabela = TableView<RankingEntry>()
        tabela.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        tabela.prefWidth = 900.0
        tabela.prefHeight = 520.0

        val colNome = TableColumn<RankingEntry, String>("Jogador")
        colNome.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.nomeJogador)
        }

        val colModo = TableColumn<RankingEntry, String>("Modo")
        colModo.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.modoJogo)
        }

        val colDificuldade = TableColumn<RankingEntry, String>("Dificuldade")
        colDificuldade.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.dificuldade)
        }

        val colPontuacao = TableColumn<RankingEntry, Number>("Pontuação")
        colPontuacao.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.pontuacao)
        }

        val colPontosMao = TableColumn<RankingEntry, Number>("Pontos da Mão")
        colPontosMao.setCellValueFactory { cellData ->
            SimpleIntegerProperty(cellData.value.pontosMao)
        }

        val colResultado = TableColumn<RankingEntry, String>("Resultado")
        colResultado.setCellValueFactory { cellData ->
            val texto = cellData.value.resultado.paraTextoResultado()
            SimpleStringProperty(texto)
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val colData = TableColumn<RankingEntry, String>("Data/Hora")
        colData.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.dataHora.format(formatter))
        }

        tabela.columns.setAll(colNome, colModo, colDificuldade, colPontuacao, colPontosMao, colResultado, colData)
        tabela.items = FXCollections.observableArrayList(RankingRepository.historico())

        val botaoVoltar = BotaoNavegacaoFactory.criarBotaoVoltarParaInicio(app, stage)

        val root = VBox(16.0, titulo, tabela, botaoVoltar)
        root.alignment = Pos.CENTER
        root.padding = Insets(24.0)

        return app.atualizarCena(root, 1000.0, 680.0)
    }
}
