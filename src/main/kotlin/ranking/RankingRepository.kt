package ranking

import java.sql.PreparedStatement
import java.time.LocalDateTime

object RankingRepository {
    // Recebe os dados da partida
    fun salvarOuAtualizar(entry: RankingEntry) {
        val idExistente = buscarIdPorNomeModoDificuldade(entry.nomeJogador, entry.modoJogo, entry.dificuldade)

        Database.getConnection().use { conn ->
            if (idExistente == null) {
                // INSERT normal (faz um INSERT na tabela ranking)
                val sql = """
                INSERT INTO ranking (nome_jogador, modo_jogo, dificuldade, pontuacao, data_hora)
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()

                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, entry.nomeJogador)
                    ps.setString(2, entry.modoJogo)
                    ps.setString(3, entry.dificuldade)
                    ps.setInt(4, entry.pontuacao)
                    ps.setString(5, entry.dataHora.toString())
                    ps.executeUpdate()
                }
            } else {
                // Checa pontuação atual antes de atualizar
                val sqlPont = "SELECT pontuacao FROM ranking WHERE id = ?"
                val pontAtual = conn.prepareStatement(sqlPont).use { ps ->
                    ps.setLong(1, idExistente)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt("pontuacao") else Int.MIN_VALUE
                    }
                }

                // Só atualiza se a nova pontuação for melhor
                if (entry.pontuacao > pontAtual) {
                    val sqlUpdate = """
                    UPDATE ranking
                    SET pontuacao = ?, data_hora = ?
                    WHERE id = ?
                """.trimIndent()

                    conn.prepareStatement(sqlUpdate).use { ps ->
                        ps.setInt(1, entry.pontuacao)
                        ps.setString(2, entry.dataHora.toString())
                        ps.setLong(3, idExistente)
                        ps.executeUpdate()
                    }
                }
            }
        }
    }

    //Procura no banco se já existe algum registro do mesmo jogador - Se existir devolve o id desse registro.
    fun buscarIdPorNomeModoDificuldade(nome: String, modo: String, dificuldade: String): Long? {
        Database.getConnection().use { conn ->
            val sql = """
            SELECT id
            FROM ranking
            WHERE nome_jogador = ?
              AND modo_jogo = ?
              AND dificuldade = ?
            ORDER BY pontuacao DESC, data_hora DESC 
            LIMIT 1
        """.trimIndent()
            //O order vai servir para caso exista mais de um registro do mesmo jogador, pegando o de maior pontuação, e se empatar, deixa o mais recente

            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, nome)
                ps.setString(2, modo)
                ps.setString(3, dificuldade)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) rs.getLong("id") else null
                }
            }
        }
    }

    // Retorna o Top 5 da tabela - recebe List<RankingEntry> com no máximo 5 itens, já na ordem certa.
    // garrante que se alguém entrar no Top 5, o antigo 5º vira 6º e some
    fun top5(): List<RankingEntry> {
        Database.getConnection().use { conn ->
            val sql = """
                SELECT id, nome_jogador, modo_jogo, dificuldade, pontuacao, data_hora
                FROM ranking
                ORDER BY pontuacao DESC, data_hora DESC --ordenado por pontuação, desempate por data/hora
                LIMIT 5
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.executeQuery().use { rs ->
                    val lista = mutableListOf<RankingEntry>()
                    while (rs.next()) {
                        lista.add(
                            RankingEntry(
                                id = rs.getLong("id"),
                                nomeJogador = rs.getString("nome_jogador"),
                                modoJogo = rs.getString("modo_jogo"),
                                dificuldade = rs.getString("dificuldade"),
                                pontuacao = rs.getInt("pontuacao"),
                                dataHora = LocalDateTime.parse(rs.getString("data_hora"))
                            )
                        )
                    }
                    return lista
                }
            }
        }
    }
}