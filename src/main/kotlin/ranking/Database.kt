package ranking

import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

object Database {
    // Salva o .db em uma pasta local do projeto (pode mudar se quiser)
    private val dbDir = Paths.get("data")
    private val dbPath = dbDir.resolve("ranking.db").toString()
    private val jdbcUrl = "jdbc:sqlite:$dbPath"

    fun getConnection(): Connection {
        if (!Files.exists(dbDir)) Files.createDirectories(dbDir)
        return DriverManager.getConnection(jdbcUrl)
    }

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
                        data_hora TEXT NOT NULL
                    );
                    """.trimIndent()
                )
            }
        }
    }
}