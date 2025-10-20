public class Peca {
    private var ladoEsquerdo : Int
    private var ladoDireito : Int

    constructor(lado_esquerdo : Int, lado_direito : Int){
        this.ladoEsquerdo = lado_esquerdo
        this.ladoDireito = lado_direito
    }

    fun getLadoEsquerdo() : Int{
        return this.ladoEsquerdo
    }

    fun getLadoDireito() : Int{
        return this.ladoDireito
    }

    override fun toString(): String {
        return "[$ladoEsquerdo|$ladoDireito]"
    }

    fun girar(){
        val temp = this.ladoEsquerdo
        this.ladoEsquerdo = this.ladoDireito
        this.ladoDireito = temp
    }

}