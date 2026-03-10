import java.time.LocalDate

// Falta organizar essa classe, existem algun métodos que não foram utilizados

class Jogo {
    private var pontaEsquerda: Int = -1
    private var pontaDireita: Int = -1
    private var mesa : MutableList<Peca> = mutableListOf()
    private var montante : MutableList<Peca> = mutableListOf()
    private var jogadorHumano : Jogador = Jogador("Humano")
    private var jogadorMaquina : Jogador = Jogador("Maquina")
    private var turno : Boolean = true
    private var fimDeJogo : Boolean = false
    private var turnoPassadoSemJogar : Int = 0
    private var vencedor : Jogador? = null
    private var modo : Int = 0

    constructor()

    fun iniciarJogo(dificuldade: Int, modo: Int) {
        // limpar estado caso um novo jogo seja iniciado
        mesa.clear()
        montante.clear()
        jogadorHumano.getMao().clear()
        jogadorMaquina.getMao().clear()
        pontaEsquerda = -1
        pontaDireita = -1
        turno = true
        fimDeJogo = false
        turnoPassadoSemJogar = 0
        vencedor = null

        if (dificuldade == 0) {
            jogadorHumano.setDificuldade("Fácil")
        } else if (dificuldade == 1) {
            jogadorHumano.setDificuldade("Médio")
        } else {
            jogadorHumano.setDificuldade("Difícil")
        }

        if (modo == 0) {
            this.modo = 0
            jogadorHumano.setModo("Clássico")
        } else {
            this.modo = 1
            jogadorHumano.setModo("Pontos")
        }

        jogadorHumano.atualizarDataJogo()

        // dominó tradicional = 28 peças
        for (i in 0..6) {
            for (j in i..6) {
                montante.add(Peca(i, j))
            }
        }

        montante.shuffle()

        for (i in 0..6) {
            jogadorMaquina.getMao().add(montante.removeAt(0))
            jogadorHumano.getMao().add(montante.removeAt(0))
        }
    }

    fun primeiraJogada() {
        // Verificando qual jogador possua a peça de dupla mais alta e joga essa peça
        for(i in 6 downTo 0){
            val peca = jogadorMaquina.getMao().find { it.getLadoEsquerdo() == i && it.getLadoDireito() == i }
            if(peca != null) {
                jogarPeca(peca)
                jogadorMaquina.getMao().remove(peca)
                this.turno = true
                return
            } else {
                val peca = jogadorHumano.getMao().find { it.getLadoEsquerdo() == i && it.getLadoDireito() == i }
                if(peca != null) {
                    jogarPeca(peca)
                    jogadorHumano.getMao().remove(peca)
                    this.turno = false
                    return
                }
            }
        }

        // Caso não haja duplas: pegar peça com maior soma
        val pecaMaquina = jogadorMaquina.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }
        val pecaHumano = jogadorHumano.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }

        // Decide quem joga a peça inicial
        if((pecaHumano?.getLadoEsquerdo()!! + pecaHumano.getLadoDireito()) >=
            (pecaMaquina?.getLadoEsquerdo()!! + pecaMaquina.getLadoDireito())) {
            mesa.add(pecaHumano)
            jogadorHumano.getMao().remove(pecaHumano)
            this.turno = true
        } else {
            mesa.add(pecaMaquina!!)
            jogadorMaquina.getMao().remove(pecaMaquina)
            this.turno = false
        }
    }

    fun iniciarJogoRapido(dificuldade: Int, modo: Int) {
        mesa.clear()
        montante.clear()
        jogadorHumano.getMao().clear()
        jogadorMaquina.getMao().clear()
        pontaEsquerda = -1
        pontaDireita = -1
        turno = true
        fimDeJogo = false
        turnoPassadoSemJogar = 0
        vencedor = null

        if (dificuldade == 0) {
            jogadorHumano.setDificuldade("Fácil")
        } else if (dificuldade == 1) {
            jogadorHumano.setDificuldade("Médio")
        } else {
            jogadorHumano.setDificuldade("Difícil")
        }

        if (modo == 0) {
            this.modo = 0
            jogadorHumano.setModo("Clássico")
        } else {
            this.modo = 1
            jogadorHumano.setModo("Pontos")
        }

        jogadorHumano.atualizarDataJogo()

        // conjunto reduzido sem duplicar espelhadas
        for (i in 0..2) {
            for (j in i..2) {
                montante.add(Peca(i, j))
            }
        }

        montante.shuffle()

        for (i in 0..2) {
            jogadorMaquina.getMao().add(montante.removeAt(0))
            jogadorHumano.getMao().add(montante.removeAt(0))
        }
    }

    fun primeiraJogadaRapido() {
        // Verificando qual jogador possua a peça de dupla mais alta e joga essa peça
        for(i in 2 downTo 0){
            val peca = jogadorMaquina.getMao().find { it.getLadoEsquerdo() == i && it.getLadoDireito() == i }
            if(peca != null) {
                jogarPeca(peca)
                jogadorMaquina.getMao().remove(peca)
                this.turno = true
                return
            } else {
                val peca = jogadorHumano.getMao().find { it.getLadoEsquerdo() == i && it.getLadoDireito() == i }
                if(peca != null) {
                    jogarPeca(peca)
                    jogadorHumano.getMao().remove(peca)
                    this.turno = false
                    return
                }
            }
        }

        // Caso não haja duplas: pegar peça com maior soma
        val pecaMaquina = jogadorMaquina.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }
        val pecaHumano = jogadorHumano.getMao().maxByOrNull { it.getLadoEsquerdo() + it.getLadoDireito() }

        // Decide quem joga a peça inicial
        if((pecaHumano?.getLadoEsquerdo()!! + pecaHumano.getLadoDireito()) >=
            (pecaMaquina?.getLadoEsquerdo()!! + pecaMaquina.getLadoDireito())) {
            mesa.add(pecaHumano)
            jogadorHumano.getMao().remove(pecaHumano)
            this.turno = true
        } else {
            mesa.add(pecaMaquina!!)
            jogadorMaquina.getMao().remove(pecaMaquina)
            this.turno = false
        }
    }


    fun jogarPeca(peca : Peca){
        mesa.add(peca)
        pontaEsquerda = mesa.first().getLadoEsquerdo()
        pontaDireita = mesa.last().getLadoDireito()
    }

    fun maoValidaHumano() : Boolean {
        for(peca in jogadorHumano.getMao()){
            if(peca.getLadoEsquerdo() == pontaEsquerda || peca.getLadoDireito() == pontaEsquerda || peca.getLadoEsquerdo() == pontaDireita
                || peca.getLadoDireito() == pontaDireita) {
                return true
            }
        }
        return false
    }

    fun maoValidaMaquina() : Pair<Boolean, Int> {
        for (i in 0 until jogadorMaquina.getMao().size) {
            if(jogadorMaquina.getMao()[i].getLadoEsquerdo() == pontaEsquerda || jogadorMaquina.getMao()[i].getLadoDireito() == pontaEsquerda || jogadorMaquina.getMao()[i].getLadoEsquerdo() == pontaDireita
                || jogadorMaquina.getMao()[i].getLadoDireito() == pontaDireita) {
                return Pair(true, i)
            }
        }
        return Pair(false, -1)
    }

    fun comprarMontanteHumano() : Peca {
        val pecaComprada = montante.first()
        jogadorHumano.getMao().add(pecaComprada)
        montante.removeAt(0)
        return pecaComprada
    }

    fun jogarPecaInterfaceLado(pecaEscolhida : Peca) : Int{
        if(pecaValidaInterface(pecaEscolhida)){
            // Verificando as possibilidades de jogada da peça brevemente
            val podeEsquerda =
                pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
            val podeDireita =
                pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita

            // Analisando espeficamente as possibilidades
            when {
                podeEsquerda && !podeDireita -> {
                    return 0
                }

                !podeEsquerda && podeDireita -> {
                    return 1
                }

                podeEsquerda && podeDireita -> {
                    return 2
                }
            }


            // Removendo a peça da mão do jogador
            jogadorHumano.getMao().remove(pecaEscolhida)

            // Alterando o turno
            this.turno = false
            turnoPassadoSemJogar = 0
        }

        return -1
    }

    fun pecaValidaInterface(pecaEscolhida : Peca) : Boolean {
        val podeEsquerda = pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
        val podeDireita = pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita
        return podeEsquerda || podeDireita
    }

    fun pecaValida(): Peca{
        // Listando as peças da mão do jogador
        while(true) {
            println("Escolha uma peça para jogar:")
            for(i in jogadorHumano.getMao().indices) {
                println("$i - ${jogadorHumano.getMao()[i]}")
            }

            val opcao = readLine()?.toIntOrNull()
            if(opcao == null || opcao < 0 || opcao >= jogadorHumano.getMao().size) {
                println("Opção inválida! Digite um número válido!!!")
                continue
            }

            val pecaEscolhida = jogadorHumano.getMao()[opcao]
            val podeEsquerda = pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
            val podeDireita = pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita

            if(podeEsquerda || podeDireita) {
                return pecaEscolhida
            } else {
                println("Peça não pode ser jogada! Escolha outra!!")
            }
        }
    }

    fun verificarPecaMaoJogador(peca : Peca) : Boolean {
        val podeEsquerda = peca.getLadoEsquerdo() == mesa.first().getLadoEsquerdo() || peca.getLadoDireito() == mesa.first().getLadoEsquerdo()
        val podeDireita = peca.getLadoEsquerdo() == mesa.last().getLadoDireito() || peca.getLadoDireito() == mesa.last().getLadoDireito()

        if(podeEsquerda || podeDireita) {
            return true
        } else {
            return false
        }
    }

    fun jogarNaMesa(peca: Peca, lado: Char) {
        when (lado){
            'E', 'e' -> {
                if(peca.getLadoDireito() != pontaEsquerda) {
                    peca.girar()
                }
                mesa.add(0, peca)
                pontaEsquerda = mesa.first().getLadoEsquerdo()
            }
            'D', 'd' -> {
                if(peca.getLadoEsquerdo() != pontaDireita) {
                    peca.girar()
                }
                mesa.add(peca)
                pontaDireita = mesa.last().getLadoDireito()
            }
            else -> println("Opção inválida!!")
        }
    }

    fun verificarFimDeJogo() : Boolean {
        if (jogadorHumano.getMao().isEmpty()){
            this.vencedor = jogadorHumano
            return true
        } else if (jogadorMaquina.getMao().isEmpty()){
            this.vencedor = jogadorMaquina
            return true
        }

        if (turnoPassadoSemJogar == 2){
            if (jogadorHumano.getMao().size < jogadorMaquina.getMao().size){
                this.vencedor = jogadorHumano
            } else if (jogadorMaquina.getMao().size < jogadorHumano.getMao().size){
                this.vencedor = jogadorMaquina
            } else if (jogadorHumano.getMao().sumOf { it.getLadoEsquerdo() + it.getLadoDireito() } < jogadorMaquina.getMao().sumOf { it.getLadoEsquerdo() +
                        it.getLadoDireito() }){
                this.vencedor = jogadorHumano
            } else if (jogadorHumano.getMao().sumOf { it.getLadoEsquerdo() + it.getLadoDireito() } >
                jogadorMaquina.getMao().sumOf { it.getLadoEsquerdo() + it.getLadoDireito() }){
                this.vencedor = jogadorMaquina
            }
            return true
        }

        return false
    }

    fun mostrarResultado(){
        println("  --RESULTADO--")
        println("- Jogo escerrado - ")
        if (vencedor == null) {
            println("O jogo terminou empatado! UAU!!")
        } else if (vencedor == jogadorHumano) {
            println("PARABÉNS!! Você venceu o jogo!!!")
        } else if (vencedor == jogadorMaquina) {
            println("Você perdeu!!!")
        }

        println("\nPeças restantes:")
        println("Sua mão: ${jogadorHumano.getMao()}")
        println("Mão da máquina: ${jogadorMaquina.getMao()}")
    }

    fun jogarMaquina(dificuldade : Int) : Peca? {
        var pecaEscolhida : Peca?
        when (dificuldade) {
            0 -> pecaEscolhida = jogadaFacil()
            1 -> pecaEscolhida = jogadaMedia()
            2 -> pecaEscolhida = jogadaDificil()
            else -> pecaEscolhida = jogadaFacil()
        }
        return pecaEscolhida
    }

    fun jogadaFacil() : Peca? {
        println("dificuldade fácil")
        // Exibindo a mesa do jogo
        println("Mesa:")
        println(mesa)
        println()

        var cond = true
        while(cond) {
            // Verificando se existe alguma jogada possível na mão da máquina
            val (maoValida, indicePecaValida) = maoValidaMaquina()
            if (maoValida) {
                val pecaEscolhida = jogadorMaquina.getMao()[indicePecaValida]

                // Verificando as possibilidades de jogada da peça brevemente
                val podeEsquerda =
                    pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
                val podeDireita =
                    pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita

                // Analisando espeficamente as possibilidades e realizando a jogada
                when {
                    podeEsquerda && !podeDireita -> {
                        jogarNaMesa(pecaEscolhida, 'E')
                    }

                    !podeEsquerda && podeDireita -> {
                        jogarNaMesa(pecaEscolhida, 'D')
                    }

                    podeEsquerda && podeDireita -> {
                        jogarNaMesa(pecaEscolhida, 'D')
                    }
                }

                println()
                println("Peça jogada: $pecaEscolhida")
                println()

                // Removendo a peça da mão do jogador
                jogadorMaquina.getMao().remove(pecaEscolhida)

                // Alterando o turno
                cond = false
                turnoPassadoSemJogar = 0
                return pecaEscolhida

            } else { // compra do montante caso não exista jogadas possíveis na mão
                if(!montante.isEmpty()) {
                    println("Comprando do montante!")
                    jogadorMaquina.getMao().add(montante.first())
                    montante.removeAt(0)
                } else {
                    turno = true
                    cond = false
                    turnoPassadoSemJogar++
                }
            }
            // Verificando se o jogo acabou
            if(verificarFimDeJogo()){
                this.fimDeJogo = true
            }

        }
        return null
    }

    fun jogadaMedia(): Peca? {
        println("dificuldade media")
        println("Mesa:")
        println(mesa)
        println()

        var cond = true
        while (cond) {
            // Verifica se há alguma jogada possível
            val jogadasPossiveis = jogadorMaquina.getMao().filter { peca ->
                peca.getLadoEsquerdo() == pontaEsquerda ||
                        peca.getLadoDireito() == pontaEsquerda ||
                        peca.getLadoEsquerdo() == pontaDireita ||
                        peca.getLadoDireito() == pontaDireita
            }

            if (jogadasPossiveis.isNotEmpty()) {
                // Estratégia da dificuldade MÉDIA:
                // Escolher a peça com maior soma dos lados
                val pecaEscolhida = jogadasPossiveis.maxByOrNull {
                    it.getLadoEsquerdo() + it.getLadoDireito()
                }!!

                val podeEsquerda =
                    pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
                val podeDireita =
                    pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita

                when {
                    podeEsquerda && !podeDireita -> jogarNaMesa(pecaEscolhida, 'E')
                    !podeEsquerda && podeDireita -> jogarNaMesa(pecaEscolhida, 'D')
                    podeEsquerda && podeDireita -> {
                        // escolha aleatória entre os dois lados válidos
                        val lado = if ((0..1).random() == 0) 'E' else 'D'
                        jogarNaMesa(pecaEscolhida, lado)
                    }
                }

                println()
                println("Peça jogada: $pecaEscolhida")
                println()

                jogadorMaquina.getMao().remove(pecaEscolhida)
                cond = false
                turnoPassadoSemJogar = 0
                return pecaEscolhida

            } else {
                // Sem jogada possível → compra do montante
                if (montante.isNotEmpty()) {
                    println("Comprando do montante!")
                    jogadorMaquina.getMao().add(montante.first())
                    montante.removeAt(0)
                } else {
                    turno = true
                    cond = false
                    turnoPassadoSemJogar++
                }
            }

            if (verificarFimDeJogo()) {
                this.fimDeJogo = true
            }
        }
        return null
    }

    fun jogadaDificil(): Peca? {
        println("dificuldade dificil")
        println("Mesa:")
        println(mesa)
        println()

        var cond = true
        while (cond) {
            // Todas as peças que podem ser jogadas
            val jogadasPossiveis = jogadorMaquina.getMao().filter { peca ->
                peca.getLadoEsquerdo() == pontaEsquerda ||
                        peca.getLadoDireito() == pontaEsquerda ||
                        peca.getLadoEsquerdo() == pontaDireita ||
                        peca.getLadoDireito() == pontaDireita
            }

            if (jogadasPossiveis.isNotEmpty()) {
                // Conta quantas vezes cada número aparece na mão da máquina
                val frequenciaNumeros = mutableMapOf<Int, Int>()
                for (peca in jogadorMaquina.getMao()) {
                    frequenciaNumeros[peca.getLadoEsquerdo()] = (frequenciaNumeros[peca.getLadoEsquerdo()] ?: 0) + 1
                    frequenciaNumeros[peca.getLadoDireito()] = (frequenciaNumeros[peca.getLadoDireito()] ?: 0) + 1
                }

                // Estratégia da dificuldade difícil:
                // Escolhe a peça que:
                // (1) Possui um número mais repetido na mão (mantém controle)
                // (2) Se empatar, escolhe a de maior soma de lados
                val pecaEscolhida = jogadasPossiveis.maxWithOrNull(
                    compareBy<Peca> {
                        val freqA = (frequenciaNumeros[it.getLadoEsquerdo()] ?: 0) +
                                (frequenciaNumeros[it.getLadoDireito()] ?: 0)
                        freqA
                    }.thenBy {
                        it.getLadoEsquerdo() + it.getLadoDireito()
                    }
                )!!

                val podeEsquerda =
                    pecaEscolhida.getLadoEsquerdo() == pontaEsquerda || pecaEscolhida.getLadoDireito() == pontaEsquerda
                val podeDireita =
                    pecaEscolhida.getLadoEsquerdo() == pontaDireita || pecaEscolhida.getLadoDireito() == pontaDireita

                // Tenta jogar no lado que mais favorece sua mão (número mais frequente)
                val ladoPreferido = if ((frequenciaNumeros[pontaEsquerda] ?: 0) >= (frequenciaNumeros[pontaDireita] ?: 0)) 'E' else 'D'

                when {
                    podeEsquerda && !podeDireita -> jogarNaMesa(pecaEscolhida, 'E')
                    !podeEsquerda && podeDireita -> jogarNaMesa(pecaEscolhida, 'D')
                    podeEsquerda && podeDireita -> jogarNaMesa(pecaEscolhida, ladoPreferido)
                }

                println()
                println("Peça jogada (difícil): $pecaEscolhida")
                println()

                jogadorMaquina.getMao().remove(pecaEscolhida)
                cond = false
                turnoPassadoSemJogar = 0
                return pecaEscolhida

            } else {
                // Sem jogada possível → compra do montante
                if (montante.isNotEmpty()) {
                    println("Comprando do montante!")
                    jogadorMaquina.getMao().add(montante.first())
                    montante.removeAt(0)
                } else {
                    turno = true
                    cond = false
                    turnoPassadoSemJogar++
                }
            }

            if (verificarFimDeJogo()) {
                this.fimDeJogo = true
            }
        }
        return null
    }

    fun somarPontos() {
        var totalPontos : Int = 0
        this.jogadorHumano.getMao().forEach { peca ->
            totalPontos += peca.getLadoEsquerdo() + peca.getLadoDireito()
        }
        this.getJogadorMaquina().setPontuacao(totalPontos)
        totalPontos = 0
        this.jogadorMaquina.getMao().forEach { peca ->
            totalPontos += peca.getLadoEsquerdo() + peca.getLadoDireito()
        }
        this.getJogadorHumano().setPontuacao(totalPontos)
    }

    fun getFimDeJogo() : Boolean {
        return this.fimDeJogo
    }

    fun getTurno() : Boolean {
        return this.turno
    }

    fun getMesa() : MutableList<Peca> {
        return this.mesa
    }

    fun getJogadorHumano() : Jogador {
        return this.jogadorHumano
    }

    fun getJogadorMaquina() : Jogador {
        return this.jogadorMaquina
    }

    fun getMontante() : MutableList<Peca> {
        return this.montante
    }

    fun setTurno(turno : Boolean){
        this.turno = turno
    }

    fun getVencedor() : Jogador? {
        return this.vencedor
    }

    fun setTurnoPassadoSemJogar(turnoPassadoSemJogar: Int){
        this.turnoPassadoSemJogar = turnoPassadoSemJogar
    }

    fun incrementarTurnoSemPassar() {
        this.turnoPassadoSemJogar++
    }

    fun setModo(modo : Int){
        this.modo = modo
    }

    fun getModo() : Int {
        return this.modo
    }
}