package ui.game

import app.DominoApp
import domain.game.Jogo
import domain.game.Peca
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.util.IdentityHashMap

class RenderizadorMesaJogo(
    private val app: DominoApp,
    private val mesaPane: Pane,
    private val painelRaiz: VBox,
    private val fornecedorJogo: () -> Jogo
) {
    private var assinaturaMesaAnterior = ""
    private var instantaneoMesaAnterior: List<Int> = emptyList()
    private val botoesMesaPorPeca = IdentityHashMap<Peca, Button>()

    private var escalaMesaAtual = 1.0
    private var inicioXMesaAtual = 0.0
    private var yMesaAtual = 0.0

    private val larguraPecaMesa = 80.0
    private val alturaPecaMesa = 40.0
    private val passoPecaMesa = 86.0

    /**
     * Renderiza mesa atual e anima apenas peca nova quando detectada.
     */
    fun renderizar(forcar: Boolean = false, reservaLateralEscolhaAtiva: Boolean = false) {
        val mesaAtual = fornecedorJogo().mesa
        val assinaturaMesaAtual = app.assinaturaPecas(mesaAtual)
        if (!forcar && assinaturaMesaAtual == assinaturaMesaAnterior && mesaPane.children.isNotEmpty()) {
            return
        }

        val instantaneoAtual = app.snapshotMesa(mesaAtual)
        var indiceNovaPeca = -1
        var entrouNaEsquerda = false

        if (!forcar && instantaneoMesaAnterior.isNotEmpty() && instantaneoAtual.size == instantaneoMesaAnterior.size + 1) {
            if (instantaneoAtual.drop(1) == instantaneoMesaAnterior) {
                indiceNovaPeca = 0
                entrouNaEsquerda = true
            } else if (instantaneoAtual.dropLast(1) == instantaneoMesaAnterior) {
                indiceNovaPeca = instantaneoAtual.lastIndex
                entrouNaEsquerda = false
            }
        }

        if (mesaAtual.isEmpty()) {
            mesaPane.children.clear()
            botoesMesaPorPeca.clear()
            instantaneoMesaAnterior = emptyList()
            assinaturaMesaAnterior = assinaturaMesaAtual
            return
        }

        val reservaLateral = if (reservaLateralEscolhaAtiva) 2.0 * (larguraPecaMesa + 10.0) else 0.0
        atualizarLayoutMesa(mesaAtual.size, reservaLateral)

        val pecasAtuais = mesaAtual.toSet()
        removerBotoesDaMesaNaoUtilizados(pecasAtuais)

        mesaPane.children.removeIf { no -> no.userData == "ESCOLHA_LADO" }

        mesaAtual.forEachIndexed { index, peca ->
            val botaoMesa = botoesMesaPorPeca.getOrPut(peca) {
                val botaoNovo = app.fabricaVisualPeca.criarBotaoPecaMesa(peca)
                mesaPane.children.add(botaoNovo)
                botaoNovo
            }

            botaoMesa.layoutX = xMesaPorIndice(index)
            botaoMesa.layoutY = yMesaAtual
            botaoMesa.scaleX = escalaMesaAtual
            botaoMesa.scaleY = escalaMesaAtual

            if (index == indiceNovaPeca) {
                app.servicoAnimacao.animarEntradaNaMesa(botaoMesa, entrouNaEsquerda, 220.0)
            }
        }

        instantaneoMesaAnterior = instantaneoAtual
        assinaturaMesaAnterior = assinaturaMesaAtual
    }

    /**
     * Posiciona botoes temporários para escolha do lado quando peca encaixa nos dois lados.
     */
    fun posicionarBotoesEscolhaLado(btnEsquerda: Button, btnDireita: Button) {
        val mesaAtual = fornecedorJogo().mesa
        val reservaLateral = 2.0 * (larguraPecaMesa + 10.0)
        atualizarLayoutMesa(mesaAtual.size, reservaLateral)

        if (mesaAtual.isEmpty()) {
            val centro = larguraMesaDisponivel() / 2.0
            btnEsquerda.layoutX = centro - 90.0
            btnDireita.layoutX = centro + 10.0
            btnEsquerda.layoutY = 160.0
            btnDireita.layoutY = 160.0
            return
        }

        val espacamento = 10.0
        val xPrimeira = xMesaPorIndice(0)
        val xUltima = xMesaPorIndice(mesaAtual.lastIndex)
        val larguraVisualPeca = larguraPecaMesa * escalaMesaAtual

        btnEsquerda.layoutX = xPrimeira - (larguraPecaMesa + espacamento)
        btnEsquerda.layoutY = yMesaAtual
        btnDireita.layoutX = xUltima + larguraVisualPeca + espacamento
        btnDireita.layoutY = yMesaAtual
    }

    /**
     * Retorna largura util da area da mesa.
     */
    private fun larguraMesaDisponivel(): Double {
        return when {
            mesaPane.width > 0.0 -> mesaPane.width
            painelRaiz.width > 0.0 -> painelRaiz.width
            else -> 1000.0
        }
    }

    /**
     * Recalcula escala e posição horizontal da fileira de pecas.
     */
    private fun atualizarLayoutMesa(quantidadePecas: Int, margemExtraLateral: Double = 0.0) {
        if (quantidadePecas <= 0) {
            escalaMesaAtual = 1.0
            inicioXMesaAtual = larguraMesaDisponivel() / 2.0 - (larguraPecaMesa / 2.0)
            yMesaAtual = (mesaPane.prefHeight - alturaPecaMesa) / 2.0
            return
        }

        val larguraDisponivel = (larguraMesaDisponivel() - 24.0 - margemExtraLateral).coerceAtLeast(120.0)
        val larguraNatural = (quantidadePecas - 1) * passoPecaMesa + larguraPecaMesa
        escalaMesaAtual = minOf(1.0, larguraDisponivel / larguraNatural).coerceAtLeast(0.42)

        val larguraEscalada = larguraNatural * escalaMesaAtual
        inicioXMesaAtual = (larguraMesaDisponivel() - larguraEscalada) / 2.0
        yMesaAtual = (mesaPane.prefHeight - (alturaPecaMesa * escalaMesaAtual)) / 2.0
    }

    /**
     * Converte indice de peca em coordenada X da mesa.
     */
    private fun xMesaPorIndice(indice: Int): Double {
        return inicioXMesaAtual + indice * (passoPecaMesa * escalaMesaAtual)
    }

    /**
     * Remove botoes que não pertencem mais ao estado atual da mesa.
     */
    private fun removerBotoesDaMesaNaoUtilizados(pecasAtuais: Set<Peca>) {
        botoesMesaPorPeca.entries.removeIf { entrada ->
            val remover = entrada.key !in pecasAtuais
            if (remover) {
                mesaPane.children.remove(entrada.value)
            }
            remover
        }
    }
}
