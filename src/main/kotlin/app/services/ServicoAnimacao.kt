package app.services

import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.util.Duration

class ServicoAnimacao {
    /**
     * Aplica animacao de hover com escala em botoes.
     */
    fun aplicarHoverComEscala(
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

    /**
     * Anima entrada de no na mesa com fade, slide e zoom.
     */
    fun animarEntradaNaMesa(no: Node, entrouNaEsquerda: Boolean, atrasoMs: Double = 200.0) {
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
}
