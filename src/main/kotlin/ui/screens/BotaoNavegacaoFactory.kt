package ui.screens

import app.DominoApp
import javafx.scene.control.Button
import javafx.stage.Stage

object BotaoNavegacaoFactory {
    /**
     * Cria botao padrão para voltar ao menu inicial.
     */
    fun criarBotaoVoltarParaInicio(app: DominoApp, stage: Stage): Button {
        val botaoVoltar = Button("Voltar")
        botaoVoltar.styleClass.add("botao-comecar")
        app.servicoAnimacao.aplicarHoverComEscala(botaoVoltar, 1.05)
        botaoVoltar.setOnAction {
            stage.scene = app.telaInicial(stage)
            app.ativarTelaCheia(stage)
        }
        return botaoVoltar
    }
}
