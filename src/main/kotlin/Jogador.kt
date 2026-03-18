class Jogador(private val nome: String) {
    private val mao: MutableList<Peca> = mutableListOf()
    private var dificuldade: String? = null
    private var pontuacao: Int = 0

    fun getMao(): MutableList<Peca> {
        return mao
    }

    fun getNome(): String {
        return nome
    }

    fun setDificuldade(dificuldade: String) {
        this.dificuldade = dificuldade
    }

    fun setPontuacao(pontuacao: Int) {
        this.pontuacao += pontuacao
    }

    fun getPontuacao(): Int {
        return pontuacao
    }
}
