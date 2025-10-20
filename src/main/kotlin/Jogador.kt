class Jogador {
    private var nome: String
    private var mao: MutableList<Peca>

    constructor(nome: String, mao: MutableList<Peca>) {
        this.nome = nome
        this.mao = mao
    }

    fun getMao() : MutableList<Peca>{
        return mao
    }

    fun getNome() : String {
        return this.nome
    }
}