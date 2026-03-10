import java.time.LocalDateTime

class Jogador(private var nome: String) {
    private var mao: MutableList<Peca> = mutableListOf()
    private var dificuldade: String = ""
    private var modo: String = ""
    private var pontuacao: Int = 0
    private var dataJogo: LocalDateTime = LocalDateTime.now()

    fun getMao(): MutableList<Peca> {
        return mao
    }

    fun getNome(): String {
        return nome
    }

    fun setNome(nome: String) {
        this.nome = nome
    }

    fun setDificuldade(dificuldade: String) {
        this.dificuldade = dificuldade
    }

    fun getDificuldade(): String {
        return dificuldade
    }

    fun setModo(modo: String) {
        this.modo = modo
    }

    fun getModo(): String {
        return modo
    }

    fun setPontuacao(pontuacao: Int) {
        this.pontuacao += pontuacao
    }

    fun getPontuacao(): Int {
        return pontuacao
    }

    fun atualizarDataJogo() {
        this.dataJogo = LocalDateTime.now()
    }

    fun getDataJogo(): LocalDateTime {
        return dataJogo
    }
}