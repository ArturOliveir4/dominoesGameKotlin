# Documentacao de Metodos do Projeto

Este documento descreve, de forma intuitiva, a funcao de cada metodo declarado no projeto.
A organizacao esta por arquivo para facilitar a navegacao.

## src/main/kotlin/Main.kt

### GameSession
- `dificuldade`: guarda a dificuldade atual escolhida pelo jogador.
- `nomeJogador`: guarda o nome atual do jogador para uso nas telas e ranking.

### Funcoes de topo
- `executarComDelay(delaySegundos, acao)`: agenda uma acao para executar depois de um atraso, muito usado para dar ritmo aos turnos da IA.
- `main()`: ponto de entrada da aplicacao JavaFX.

### DominoApp
- `atualizarCena(novoRoot, largura, altura)`: cria a cena principal na primeira vez e, depois disso, so troca o conteudo da tela, mantendo o CSS aplicado.
- `ativarTelaCheia(stage)`: forca modo tela cheia e remove atalho/mensagem de saida.
- `assinaturaPecas(pecas)`: gera uma assinatura textual da lista de pecas para detectar mudancas de estado.
- `snapshotMesa(pecas)`: gera um snapshot por identidade de objeto para identificar qual peca acabou de entrar na mesa.
- `start(stage)`: inicializa banco, abre tela inicial e mostra a janela.
- `telaInicial(stage)`: retorna a cena da tela inicial.
- `mostrarTelaJogo(stage)`: abre a tela principal de partida.
- `telaFimDeJogo(stage)`: retorna a cena de fim de partida.
- `telaRanking(stage)`: retorna a cena do historico de partidas.
- `telaRankingGeral(stage)`: retorna a cena do ranking agregado.

## src/main/kotlin/domain/game/Jogo.kt

- `turnoHumano` (propriedade): indica se o turno atual e do jogador humano.
- `mesa` (propriedade): exposicao de leitura da lista de pecas atualmente na mesa.
- `jogadorHumano` (propriedade): referencia do jogador humano da partida.
- `jogadorMaquina` (propriedade): referencia do jogador controlado pela IA.
- `quantidadeMontante` (propriedade): quantidade de pecas restantes para compra.
- `montanteVazio` (propriedade): informa se o montante esta vazio.
- `vencedor` (propriedade): vencedor da rodada, ou null em empate.
- `definirTurnoHumano(turnoHumano)`: altera explicitamente de quem e o turno.
- `removerPecaHumano(peca)`: remove uma peca especifica da mao do humano.
- `definirTurnosSemJogar(valor)`: ajusta contador de turnos consecutivos sem jogada.
- `incrementarTurnosSemJogar()`: incrementa contador usado para detectar bloqueio da rodada.
- `definirDificuldadeHumano(dificuldade)`: converte o enum de dificuldade para texto e registra no jogador humano.
- `resetarPartida()`: limpa estado da rodada (mesa, montante, maos, turno, vencedor e flags).
- `iniciarPartida(dificuldade)`: monta pecas, embaralha montante e distribui 7 pecas para cada jogador.
- `iniciarJogo(dificuldade)`: fachada publica para iniciar a partida.
- `primeiraJogadaComLimite()`: decide a jogada inicial pela regra de maior dupla; se nao houver, usa maior soma.
- `primeiraJogada()`: fachada publica para executar a primeira jogada.
- `jogarPeca(peca)`: adiciona peca na mesa (caso inicial) e atualiza pontas esquerda/direita.
- `ladosValidosParaPeca(peca)`: calcula se a peca pode entrar na esquerda, direita ou ambos os lados.
- `existeJogadaValida(lados)`: retorna se existe ao menos um lado jogavel.
- `opcoesJogadaMaquina()`: gera todas as opcoes validas da IA com dados para estrategia.
- `contextoAtualIA()`: cria o contexto da IA (mao atual e pontas da mesa).
- `comprarDoMontante()`: retira uma peca do montante; retorna null se vazio.
- `maoValidaHumano()`: verifica se o humano possui alguma jogada valida.
- `comprarMontanteHumano()`: compra peca para o humano e adiciona na mao (falha se montante vazio).
- `jogarPecaInterfaceLado(pecaEscolhida)`: informa para a UI se a peca joga na esquerda, direita, ambos ou nenhum.
- `pecaValidaInterface(pecaEscolhida)`: valida rapidamente se a peca escolhida e jogavel.
- `verificarPecaMaoJogador(peca)`: usado pela UI para habilitar/desabilitar botoes de pecas da mao.
- `jogarNaMesa(peca, lado)`: posiciona a peca no lado escolhido, girando quando necessario.
- `verificarFimDeJogo()`: encerra partida por mao vazia ou por bloqueio (duas passagens), incluindo desempate por soma.
- `jogarTurnoMaquinaComHistorico(seletor)`: loop completo do turno da IA, incluindo compras e eventual jogada, retornando historico de compras.
- `jogarMaquinaComHistorico(dificuldade)`: escolhe estrategia (facil/medio/dificil) e executa turno da IA.
- `somarPontos()`: atribui pontos da rodada (3 vitoria, 1 empate).
- `houveDesempatePorSomaDaMao()`: informa se o resultado saiu por regra de desempate de soma.

## src/main/kotlin/domain/game/Jogador.kt

- `nome` (propriedade): nome fixo do jogador.
- `mao` (propriedade): exposicao de leitura da mao atual.
- `dificuldade` (propriedade): dificuldade associada ao jogador para registro.
- `pontuacao` (propriedade): pontuacao acumulada na rodada.
- `adicionarPeca(peca)`: adiciona peca na mao.
- `removerPeca(peca)`: remove peca da mao e informa se removeu.
- `limparMao()`: esvazia a mao.
- `definirDificuldade(dificuldade)`: registra dificuldade (texto) associada ao jogador.
- `adicionarPontuacao(valor)`: soma pontos ao total atual.
- `zerarPontuacao()`: zera pontuacao acumulada.

## src/main/kotlin/domain/game/Peca.kt

- `ladoEsquerdo` (propriedade): valor atual do lado esquerdo.
- `ladoDireito` (propriedade): valor atual do lado direito.
- `toString()`: retorna representacao textual da peca (`[a|b]`).
- `girar()`: inverte os lados da peca (esquerdo vira direito e vice-versa).

## src/main/kotlin/domain/ai/EstrategiasIA.kt

### Interface
- `EstrategiaIA.escolher(opcoes, contexto)`: contrato base para selecionar peca e lado da jogada da IA.

### EstrategiaFacil
- `escolher(opcoes, contexto)`: escolhe a primeira opcao valida; simples e previsivel.

### EstrategiaMedia
- `escolher(opcoes, contexto)`: prioriza maior soma da peca e, em empate de lado, pode alternar aleatoriamente.

### EstrategiaDificil
- `escolher(opcoes, contexto)`: usa frequencia de numeros na propria mao para maximizar continuidade de jogo.

## src/main/kotlin/app/services/ServicoAnimacao.kt

- `aplicarHoverComEscala(botao, escalaHover, duracaoMs)`: adiciona animacao de zoom ao passar o mouse no botao.
- `animarEntradaNaMesa(no, entrouNaEsquerda, atrasoMs)`: anima entrada de uma nova peca na mesa com fade, slide e zoom.

## src/main/kotlin/app/services/ServicoImagem.kt

- `obterImagem(caminho)`: carrega e reutiliza imagem com cache para evitar recarregamentos repetidos.
- `criarVisualImagem(caminho, largura, altura)`: cria um ImageView pronto para uso com dimensoes configuradas.

## src/main/kotlin/app/services/FabricaVisualPeca.kt

- `gerarIconesRestantes(quantidade)`: cria uma linha de miniaturas de verso para representar quantidade de pecas.
- `criarDivisorVertical(altura)`: cria linha vertical usada no meio da peca horizontal.
- `criarDivisorHorizontal(largura)`: cria linha horizontal usada no meio da peca vertical.
- `criarBotaoPecaMesa(peca)`: monta o botao visual da peca para exibicao na mesa.
- `criarBotaoPecaMao(peca)`: monta o botao visual da peca para exibicao na mao do jogador.

## src/main/kotlin/app/services/ServicoRankingUi.kt

- `registrarResultadoNoRanking(jogo)`: converte resultado da rodada em registro e persiste no ranking.
- `criarPainelTop5Ranking()`: constroi painel resumido com os 5 melhores do ranking geral.
- `criarPainelUltimas5Partidas()`: constroi painel resumido com as 5 partidas mais recentes.

## src/main/kotlin/ranking/db/Database.kt

- `getConnection()`: garante pasta de dados e abre conexao SQLite para o arquivo local.
- `init()`: cria tabela de ranking se necessario e aplica migracao da coluna `pontos_mao` para bancos antigos.

## src/main/kotlin/ranking/repository/RankingRepository.kt

- `mapRankingEntry(rs)`: converte uma linha SQL em objeto de historico de partida.
- `mapRankingGeralEntry(rs)`: converte uma linha SQL agregada em objeto de ranking geral.
- `salvarOuAtualizar(entry)`: grava uma partida no banco.
- `historico()`: busca todas as partidas do mais novo para o mais antigo.
- `ultimasPartidas(limit)`: busca as partidas mais recentes com limite configuravel.
- `rankingGeral()`: gera ranking consolidado por jogador (pontos, partidas, vitorias, empates e derrotas).
- `top5Geral()`: retorna os 5 melhores do ranking consolidado.

## src/main/kotlin/ui/game/ControladorHudJogo.kt

- `atualizarIndicadorTurno(turnoHumano)`: atualiza texto/estilo do indicador de turno (humano ou IA).
- `atualizarIndicadorFimRodada(vencedorNome)`: mostra status final da rodada sem conflitar com texto de turno.
- `atualizarContadorHumano(quantidade)`: atualiza contador visual de pecas do humano.
- `atualizarContadorMaquina(quantidade)`: atualiza contador visual de pecas da IA.
- `centralizarContadores()`: padroniza alinhamento dos contadores no HUD.
- `criarBotaoAcao(texto, cssClass, escalaHover)`: fabrica botoes de acao com estilo e hover consistentes.
- `atualizarContador(container, titulo, quantidade)`: rotina interna para desenhar contador com titulo e icones.

## src/main/kotlin/ui/game/RenderizadorMesaJogo.kt

- `renderizar(forcar, reservaLateralEscolhaAtiva)`: desenha e atualiza a mesa, detectando entrada de nova peca para animacao.
- `posicionarBotoesEscolhaLado(btnEsquerda, btnDireita)`: posiciona botoes temporarios de escolha de lado ao redor da mesa.
- `larguraMesaDisponivel()`: calcula largura util da area da mesa.
- `atualizarLayoutMesa(quantidadePecas, margemExtraLateral)`: calcula escala e posicao da fileira de pecas para caber na tela.
- `xMesaPorIndice(indice)`: converte indice da peca em coordenada X na mesa.
- `removerBotoesDaMesaNaoUtilizados(pecasAtuais)`: remove botoes de pecas que nao pertencem mais ao estado atual da mesa.

## src/main/kotlin/ui/screens/BotaoNavegacaoFactory.kt

- `criarBotaoVoltarParaInicio(app, stage)`: cria botao padrao de voltar ao menu inicial com animacao.

## src/main/kotlin/ui/screens/TelaInicial.kt

- `criar(stage)`: monta toda a tela inicial (etapas de comecar, nome e dificuldade), incluindo painois de ranking/historico.
- `atualizarResumo()` (local em `criar`): atualiza texto de resumo com nome e dificuldade selecionados.
- `renderizarEtapa()` (local em `criar`): renderiza os controles corretos para cada etapa do fluxo inicial.

## src/main/kotlin/ui/screens/TelaJogo.kt

- `mostrar(stage)`: monta a tela principal da partida e orquestra todo o fluxo de jogo em tempo real.
- `posicionarBotoesEscolhaLado(btnEsquerda, btnDireita)` (local em `mostrar`): delega o posicionamento dos botoes de escolha para o renderizador da mesa.
- `atualizarVisualMontante()` (local em `mostrar`): redesenha o montante visual com base na quantidade atual.
- `atualizarContadorMaquinaVisual()` (local em `mostrar`): atualiza contador da IA considerando override de animacao.
- `animarSaidaDoTopoDoMontante(onFinished)` (local em `mostrar`): anima a retirada de uma peca do topo do montante.
- `executarTurnoDaMaquina()` (local em `mostrar`): executa o turno da IA completo, incluindo compras e jogada.
- `finalizarTurnoMaquina()` (local em `executarTurnoDaMaquina`): conclui estado/feedback apos a IA terminar o turno.
- `animarCompraIA(indice)` (local em `executarTurnoDaMaquina`): executa animacoes sequenciais de cada compra da IA.
- `jogarPecaHumano(peca, lado)` (local em `mostrar`): aplica jogada do humano e atualiza estado da interface.
- `criarBotaoEscolhaLado(peca, lado)` (local em `mostrar`): cria botao temporario para escolher em qual ponta jogar.
- `renderizarMaoHumano(forcar)` (local em `mostrar`): sincroniza botoes da mao com o estado atual, habilitando apenas pecas jogaveis.
- `renderizarAcoesJogador()` (local em `mostrar`): monta area de acoes (voltar e compra via montante clicavel).
- `executarAcaoComprar()` (local em `renderizarAcoesJogador`): valida regras de compra do humano e executa compra/passagem de turno.
- `atualizarInterface()` (lambda local em `mostrar`): funcao central de refresh, troca de turno, fim de jogo e agendamento do turno da IA.

## src/main/kotlin/ui/screens/TelaFinalJogo.kt

- `criar(stage)`: monta tela de encerramento com resultado, ranking resumido e botoes de navegacao.

## src/main/kotlin/ui/screens/TelaHistoricoPartidas.kt

- `criar(stage)`: monta a tela com tabela de historico completo das partidas.

## src/main/kotlin/ui/screens/TelaRankingGeral.kt

- `criar(stage)`: monta a tela com ranking agregado por jogador.
