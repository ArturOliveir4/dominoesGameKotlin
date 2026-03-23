package app.services

import app.GameSession
import domain.game.Dificuldade
import domain.game.Jogo
import domain.game.paraPontuacaoResultadoHumano
import domain.game.paraTexto
import domain.game.paraTextoResultado
import domain.game.somarPontosMao
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import ranking.model.RankingEntry
import ranking.repository.RankingRepository
import java.time.LocalDateTime

class ServicoRankingUi {
    /**
     * Converte resultado da rodada atual e grava no banco de "ranking".
     */
    fun registrarResultadoNoRanking(jogo: Jogo) {
        val vencedor = jogo.vencedor
        val resultado = vencedor.paraPontuacaoResultadoHumano()
        val pontosMaoJogador = jogo.jogadorHumano.mao
            .somarPontosMao()

        val dificuldadeTexto = GameSession.dificuldade.paraTexto()

        RankingRepository.salvarOuAtualizar(
            RankingEntry(
                nomeJogador = GameSession.nomeJogador,
                modoJogo = "Classico",
                dificuldade = dificuldadeTexto,
                pontuacao = resultado,
                resultado = resultado,
                pontosMao = pontosMaoJogador,
                dataHora = LocalDateTime.now()
            )
        )
    }

    /**
     * Cria painel resumido com os 5 melhores jogadores do "ranking" geral.
     */
    fun criarPainelTop5Ranking(): VBox {
        val painel = VBox(6.0)
        painel.alignment = Pos.CENTER

        val titulo = Label("Top 5")
        titulo.styleClass.add("status-turno")
        painel.children.add(titulo)

        val top5 = RankingRepository.top5Geral()
        if (top5.isEmpty()) {
            painel.children.add(Label("Sem registros no ranking."))
            return painel
        }

        top5.forEachIndexed { index, item ->
            val linha = Label(
                "${index + 1}. ${item.nomeJogador} | ${item.pontosTotais} pts | ${item.partidas} partidas"
            )
            painel.children.add(linha)
        }

        return painel
    }

    /**
     * Cria painel com as 5 partidas mais recentes.
     */
    fun criarPainelUltimas5Partidas(): VBox {
        val painel = VBox(6.0)
        painel.alignment = Pos.CENTER

        val titulo = Label("Últimas 5 partidas")
        titulo.styleClass.add("status-turno")
        painel.children.add(titulo)

        val ultimas = RankingRepository.ultimasPartidas(5)
        if (ultimas.isEmpty()) {
            painel.children.add(Label("Sem partidas registradas."))
            return painel
        }

        ultimas.forEachIndexed { index, item ->
            val resultadoTexto = item.resultado.paraTextoResultado()
            val linha = Label(
                "${index + 1}. ${item.nomeJogador} | mão: ${item.pontosMao} pts | $resultadoTexto"
            )
            painel.children.add(linha)
        }

        return painel
    }
}
