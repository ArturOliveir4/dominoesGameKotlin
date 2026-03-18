@file:Suppress("SqlNoDataSourceInspection", "SqlDialectInspection", "SpellCheckingInspection")

package ranking.repository

import ranking.db.Database
import ranking.model.RankingEntry
import ranking.model.RankingGeralEntry
import java.sql.ResultSet
import java.time.LocalDateTime

object RankingRepository {
    /**
     * Mapeia uma linha do ResultSet para RankingEntry.
     */
    private fun mapRankingEntry(rs: ResultSet): RankingEntry {
        return RankingEntry(
            id = rs.getLong("id"),
            nomeJogador = rs.getString("nome_jogador"),
            modoJogo = rs.getString("modo_jogo"),
            dificuldade = rs.getString("dificuldade"),
            pontuacao = rs.getInt("pontuacao"),
            resultado = rs.getInt("resultado"),
            pontosMao = rs.getInt("pontos_mao"),
            dataHora = LocalDateTime.parse(rs.getString("data_hora"))
        )
    }

    /**
     * Mapeia uma linha agregada do ResultSet para RankingGeralEntry.
     */
    private fun mapRankingGeralEntry(rs: ResultSet): RankingGeralEntry {
        return RankingGeralEntry(
            nomeJogador = rs.getString("nome_jogador"),
            pontosTotais = rs.getInt("pontos_totais"),
            partidas = rs.getInt("partidas"),
            vitorias = rs.getInt("vitorias"),
            empates = rs.getInt("empates"),
            derrotas = rs.getInt("derrotas")
        )
    }

    /**
     * Salva uma partida como novo registro no ranking.
     */
    fun salvarOuAtualizar(entry: RankingEntry) {
        Database.getConnection().use { conn ->
            val sql = """
                INSERT INTO ranking (nome_jogador, modo_jogo, dificuldade, pontuacao, resultado, pontos_mao, data_hora)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, entry.nomeJogador)
                ps.setString(2, entry.modoJogo)
                ps.setString(3, entry.dificuldade)
                ps.setInt(4, entry.pontuacao)
                ps.setInt(5, entry.resultado)
                ps.setInt(6, entry.pontosMao)
                ps.setString(7, entry.dataHora.toString())
                ps.executeUpdate()
            }
        }
    }

    /**
     * Retorna historico completo ordenado da mais recente para a mais antiga.
     */
    fun historico(): List<RankingEntry> {
        Database.getConnection().use { conn ->
            val sql = """
                SELECT id, nome_jogador, modo_jogo, dificuldade, pontuacao, resultado, pontos_mao, data_hora
                FROM ranking
                ORDER BY data_hora DESC, id DESC
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.executeQuery().use { rs ->
                    val lista = mutableListOf<RankingEntry>()
                    while (rs.next()) {
                        lista.add(mapRankingEntry(rs))
                    }
                    return lista
                }
            }
        }
    }

    /**
     * Retorna as ultimas partidas, respeitando o limite informado.
     */
    fun ultimasPartidas(limit: Int = 5): List<RankingEntry> {
        Database.getConnection().use { conn ->
            val sql = """
                SELECT id, nome_jogador, modo_jogo, dificuldade, pontuacao, resultado, pontos_mao, data_hora
                FROM ranking
                ORDER BY data_hora DESC, id DESC
                LIMIT ?
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.setInt(1, limit)
                ps.executeQuery().use { rs ->
                    val lista = mutableListOf<RankingEntry>()
                    while (rs.next()) {
                        lista.add(mapRankingEntry(rs))
                    }
                    return lista
                }
            }
        }
    }

    /**
     * Gera ranking geral por jogador (pontos e estatisticas).
     */
    fun rankingGeral(): List<RankingGeralEntry> {
        Database.getConnection().use { conn ->
            val sql = """
                SELECT
                    nome_jogador,
                    SUM(pontuacao) AS pontos_totais,
                    COUNT(*) AS partidas,
                    SUM(CASE WHEN resultado = 3 THEN 1 ELSE 0 END) AS vitorias,
                    SUM(CASE WHEN resultado = 1 THEN 1 ELSE 0 END) AS empates,
                    SUM(CASE WHEN resultado = 0 THEN 1 ELSE 0 END) AS derrotas
                FROM ranking
                GROUP BY nome_jogador
                ORDER BY pontos_totais DESC, vitorias DESC, nome_jogador ASC
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.executeQuery().use { rs ->
                    val lista = mutableListOf<RankingGeralEntry>()
                    while (rs.next()) {
                        lista.add(mapRankingGeralEntry(rs))
                    }
                    return lista
                }
            }
        }
    }

    /**
     * Retorna apenas o top 5 do ranking geral.
     */
    fun top5Geral(): List<RankingGeralEntry> {
        Database.getConnection().use { conn ->
            val sql = """
                SELECT
                    nome_jogador,
                    SUM(pontuacao) AS pontos_totais,
                    COUNT(*) AS partidas,
                    SUM(CASE WHEN resultado = 3 THEN 1 ELSE 0 END) AS vitorias,
                    SUM(CASE WHEN resultado = 1 THEN 1 ELSE 0 END) AS empates,
                    SUM(CASE WHEN resultado = 0 THEN 1 ELSE 0 END) AS derrotas
                FROM ranking
                GROUP BY nome_jogador
                ORDER BY pontos_totais DESC, vitorias DESC, nome_jogador ASC
                LIMIT 5
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.executeQuery().use { rs ->
                    val lista = mutableListOf<RankingGeralEntry>()
                    while (rs.next()) {
                        lista.add(mapRankingGeralEntry(rs))
                    }
                    return lista
                }
            }
        }
    }
}