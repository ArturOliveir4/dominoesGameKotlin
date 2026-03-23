package app.services

import javafx.scene.image.Image
import javafx.scene.image.ImageView

class ServicoImagem {
    private val cacheImagens = mutableMapOf<String, Image>()

    /**
     * Busca imagem no cache e carrega do recurso apenas quando necessário.
     */
    private fun obterImagem(caminho: String): Image {
        return cacheImagens.getOrPut(caminho) {
            Image(javaClass.getResource(caminho)!!.toExternalForm())
        }
    }

    /**
     * Cria um ImageView configurado para uso nos componentes de UI.
     */
    fun criarVisualImagem(caminho: String, largura: Double, altura: Double): ImageView {
        val visualImagem = ImageView(obterImagem(caminho))
        visualImagem.fitWidth = largura
        visualImagem.fitHeight = altura
        visualImagem.isPreserveRatio = true
        return visualImagem
    }
}
