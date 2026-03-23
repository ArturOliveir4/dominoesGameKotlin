package ui.game

import app.DominoApp
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class ControladorHudJogo(
    private val app: DominoApp,
    private val turnoLabel: Label,
    private val contadorHumano: HBox,
    private val contadorMaquina: HBox
) {
    /**
     * Atualiza indicador visual de turno entre humano e IA.
     */
    fun atualizarIndicadorTurno(turnoHumano: Boolean) {
        turnoLabel.styleClass.removeAll("status-humano", "status-maquina")
        if (turnoHumano) {
            turnoLabel.styleClass.add("status-humano")
            turnoLabel.text = "Sua vez"
        } else {
            turnoLabel.styleClass.add("status-maquina")
            turnoLabel.text = "Vez da máquina..."
        }
    }

    /**
     * Atualiza indicador para estado de fim de rodada.
     */
    fun atualizarIndicadorFimRodada(vencedorNome: String?) {
        turnoLabel.styleClass.removeAll("status-humano", "status-maquina")
        when (vencedorNome) {
            "Humano" -> {
                turnoLabel.styleClass.add("status-humano")
                turnoLabel.text = "Você venceu a rodada"
            }

            "Maquina" -> {
                turnoLabel.styleClass.add("status-maquina")
                turnoLabel.text = "IA venceu a rodada"
            }

            else -> {
                turnoLabel.text = "Rodada encerrada"
            }
        }
    }

    /**
     * Atualiza contador visual de pecas do humano.
     */
    fun atualizarContadorHumano(quantidade: Int) {
        atualizarContador(contadorHumano, "JOGADOR: ", quantidade)
    }

    /**
     * Atualiza contador visual de pecas da IA.
     */
    fun atualizarContadorMaquina(quantidade: Int) {
        atualizarContador(contadorMaquina, "CPU: ", quantidade)
    }

    /**
     * Garante alinhamento central dos contadores no HUD.
     */
    fun centralizarContadores() {
        contadorHumano.alignment = Pos.CENTER
        contadorMaquina.alignment = Pos.CENTER
    }

    /**
     * Fabrica botao de ação com classe CSS e animação de hover.
     */
    fun criarBotaoAcao(texto: String, cssClass: String, escalaHover: Double = 1.05): Button {
        val botao = Button(texto)
        botao.styleClass.add(cssClass)
        app.servicoAnimacao.aplicarHoverComEscala(botao, escalaHover)
        return botao
    }

    /**
     * Rotina interna para renderizar contador com titulo e icones.
     */
    private fun atualizarContador(container: HBox, titulo: String, quantidade: Int) {
        container.children.clear()
        val tituloLabel = Label(titulo)
        val icones = app.fabricaVisualPeca.gerarIconesRestantes(quantidade)
        icones.alignment = Pos.CENTER
        container.spacing = 8.0
        container.alignment = Pos.CENTER
        container.children.addAll(tituloLabel, icones)
    }
}
