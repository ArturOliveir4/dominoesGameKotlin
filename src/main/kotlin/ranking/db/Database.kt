@file:Suppress("SqlNoDataSourceInspection", "SqlDialectInspection")

package ranking.db

import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

object Database {
    // Salva o .db em uma pasta local do projeto (pode mudar se quiser)
    private val dbDir = Paths.get("data")
    private val dbPath = dbDir.resolve("ranking.db").toString()
    private val jdbcUrl = "jdbc:sqlite:$dbPath"

    /**
     * Garante pasta de dados e abre conexao SQLite para o arquivo local.
     */
    fun getConnection(): Connection {
        if (!Files.exists(dbDir)) Files.createDirectories(dbDir) //se a pasta data/ não existir, cria
        return DriverManager.getConnection(jdbcUrl) //conexão com o SQLite (arquivo .db)
    }

    /**
     * Inicializa o schema do banco e aplica migração para bases antigas. (prepara o banco para uso)
     */
    fun init() {
        getConnection().use { conn ->
            conn.createStatement().use { st ->
                st.execute(
                    """
                    CREATE TABLE IF NOT EXISTS ranking (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome_jogador TEXT NOT NULL,
                        modo_jogo TEXT NOT NULL,
                        dificuldade TEXT NOT NULL,
                        pontuacao INTEGER NOT NULL,
                        resultado INTEGER NOT NULL, -- resultado se foi vitoria, empate ou perdeu
                        pontos_mao INTEGER NOT NULL DEFAULT 0,
                        data_hora TEXT NOT NULL
                    );
                    """.trimIndent()
                )

                // Migração para bancos antigos que ainda não possuem a coluna pontos_mao.
                try {
                    st.execute("ALTER TABLE ranking ADD COLUMN pontos_mao INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {
                    // Ignora quando a coluna já existe.
                }
            }
        }
    }
}