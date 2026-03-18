package app.services

import domain.game.Peca
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class FabricaVisualPeca(private val servicoImagem: ServicoImagem) {
    private companion object {
        const val LARGURA_PECA_MESA = 80.0
        const val ALTURA_PECA_MESA = 40.0
        const val LARGURA_PECA_MAO = 40.0
        const val ALTURA_PECA_MAO = 80.0
        const val LARGURA_ICONE_PECA = 30.0
        const val ALTURA_ICONE_PECA = 30.0
    }

    /**
     * Gera icones de verso para exibir quantidade de pecas restantes.
     */
    fun gerarIconesRestantes(quantidade: Int): HBox {
        val contenedor = HBox(5.0)
        contenedor.alignment = Pos.CENTER
        repeat(quantidade) {
            val moldura = StackPane()
            moldura.alignment = Pos.CENTER
            moldura.prefWidth = 20.0
            moldura.prefHeight = 30.0
            moldura.minWidth = 20.0
            moldura.minHeight = 30.0
            moldura.maxWidth = 20.0
            moldura.maxHeight = 30.0
            moldura.children.add(servicoImagem.criarVisualImagem("/images/verso.png", 20.0, 30.0))
            contenedor.children.add(moldura)
        }
        return contenedor
    }

    /**
     * Cria divisoria vertical usada em pecas horizontais.
     */
    fun criarDivisorVertical(altura: Double): Region {
        val linhaDivisoria = Region()
        linhaDivisoria.style = "-fx-background-color: black;"
        linhaDivisoria.prefWidth = 2.0
        linhaDivisoria.prefHeight = altura
        linhaDivisoria.minWidth = 2.0
        linhaDivisoria.maxWidth = 2.0
        return linhaDivisoria
    }

    /**
     * Cria divisoria horizontal usada em pecas verticais.
     */
    fun criarDivisorHorizontal(largura: Double): Region {
        val linhaDivisoria = Region()
        linhaDivisoria.style = "-fx-background-color: black;"
        linhaDivisoria.prefWidth = largura
        linhaDivisoria.prefHeight = 2.0
        linhaDivisoria.minWidth = largura
        linhaDivisoria.maxWidth = largura
        linhaDivisoria.minHeight = 2.0
        linhaDivisoria.maxHeight = 2.0
        return linhaDivisoria
    }

    /**
     * Monta o botao visual de peca para a mesa.
     */
    fun criarBotaoPecaMesa(peca: Peca): Button {
        val btn = Button()
        btn.isMouseTransparent = true
        btn.styleClass.add("botao-peca")
        btn.prefHeight = ALTURA_PECA_MESA
        btn.minHeight = ALTURA_PECA_MESA
        btn.maxHeight = ALTURA_PECA_MESA
        btn.prefWidth = LARGURA_PECA_MESA
        btn.minWidth = LARGURA_PECA_MESA
        btn.maxWidth = LARGURA_PECA_MESA
        btn.alignment = Pos.CENTER
        btn.contentDisplay = ContentDisplay.GRAPHIC_ONLY

        val ladoEsquerdoImage = servicoImagem.criarVisualImagem(
            "/images/${peca.ladoEsquerdo}.png",
            LARGURA_ICONE_PECA,
            ALTURA_ICONE_PECA
        )
        val ladoDireitoImage = servicoImagem.criarVisualImagem(
            "/images/${peca.ladoDireito}.png",
            LARGURA_ICONE_PECA,
            ALTURA_ICONE_PECA
        )
        val caixaHorizontal = HBox(2.0, ladoEsquerdoImage, criarDivisorVertical(30.0), ladoDireitoImage)
        caixaHorizontal.alignment = Pos.CENTER
        btn.graphic = caixaHorizontal
        return btn
    }

    /**
     * Monta o botao visual de peca para a mao do jogador.
     */
    fun criarBotaoPecaMao(peca: Peca): Button {
        val btn = Button()
        btn.styleClass.add("botao-peca-mao")
        btn.prefHeight = ALTURA_PECA_MAO
        btn.minHeight = ALTURA_PECA_MAO
        btn.maxHeight = ALTURA_PECA_MAO
        btn.prefWidth = LARGURA_PECA_MAO
        btn.minWidth = LARGURA_PECA_MAO
        btn.maxWidth = LARGURA_PECA_MAO
        btn.alignment = Pos.CENTER
        btn.contentDisplay = ContentDisplay.GRAPHIC_ONLY

        val ladoEsquerdoImage = servicoImagem.criarVisualImagem(
            "/imagesVertical/${peca.ladoEsquerdo}.png",
            LARGURA_ICONE_PECA,
            ALTURA_ICONE_PECA
        )
        val ladoDireitoImage = servicoImagem.criarVisualImagem(
            "/imagesVertical/${peca.ladoDireito}.png",
            LARGURA_ICONE_PECA,
            ALTURA_ICONE_PECA
        )
        val caixaVertical = VBox(2.0, ladoEsquerdoImage, criarDivisorHorizontal(30.0), ladoDireitoImage)
        caixaVertical.alignment = Pos.CENTER
        btn.graphic = caixaVertical
        return btn
    }
}
