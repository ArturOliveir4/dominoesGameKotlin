import java.time.LocalDate

class Jogador {
    private var nome: String
    private var mao: MutableList<Peca> = mutableListOf()
    private var dificuldade: String? = null
    private var modo : String? = null
    private var pontuacao : Int = 0
    private var dataJogo : LocalDate = LocalDate.now()

    constructor(nome: String) {
        this.nome = nome
    }

    fun getMao() : MutableList<Peca>{
        return mao
    }

    fun getNome() : String {
        return this.nome
    }

    fun setDificuldade(dificuldade : String) {
        this.dificuldade = dificuldade
    }

    fun setPontuacao(pontuacao : Int){
        this.pontuacao += pontuacao
    }

    fun getPontuacao() : Int {
        return this.pontuacao
    }
}