package domain.game

class Jogador(val nome: String) {
    private val maoInterna: MutableList<Peca> = mutableListOf()
    var dificuldade: String? = null
        private set
    var pontuacao: Int = 0
        private set
    val mao: List<Peca>
        get() = maoInterna

    /**
     * Adiciona uma peca a mao do jogador.
     */
    fun adicionarPeca(peca: Peca) {
        maoInterna.add(peca)
    }

    /**
     * Remove a peca da mao, se existir.
     */
    fun removerPeca(peca: Peca): Boolean {
        return maoInterna.remove(peca)
    }

    /**
     * Limpa todas as pecas da mao.
     */
    fun limparMao() {
        maoInterna.clear()
    }

    /**
     * Define dificuldade em formato texto para registro/ranking.
     */
    fun definirDificuldade(dificuldade: String) {
        this.dificuldade = dificuldade
    }

    /**
     * Soma pontuacao ao total atual.
     */
    fun adicionarPontuacao(valor: Int) {
        pontuacao += valor
    }

    /**
     * Zera a pontuacao acumulada.
     */
    fun zerarPontuacao() {
        pontuacao = 0
    }
}
