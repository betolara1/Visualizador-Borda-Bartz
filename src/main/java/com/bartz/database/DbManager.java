package com.bartz.database;

import com.bartz.model.Dados;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbManager {
    
    // URL de conexão do SQLite. 
    // "jdbc:sqlite:bipagens.db" criará um arquivo chamado 'bipagens.db' na pasta do programa.
    private static final String DB_URL = "jdbc:sqlite:bipagens.db";

    // formator de data e hora padrão ISO
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    
    /**
     * BLOCO 1: Inicialização do Banco
     * Cria a tabela 'historico_bipagens' se for a primeira vez que o programa é aberto.
    */
    public static void iniciarBanco(){
        // Comando SQL
        String sql = """
                CREATE TABLE IF NOT EXISTS dados(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cod_barras TEXT NOT NULL,
                    nome_arquivo TEXT NOT NULL,
                    data TEXT NOT NULL,
                    qtd_bordas_total INTEGER NOT NULL,
                    qtd_bordas_bipada INTEGER NOT NULL
                );
                """;

        // Fecha a conexão quando terminar
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()){
            
            stmt.execute(sql);
            System.out.println("SQLite pronto.");
        }
        catch(SQLException e){
            System.err.println("Erro ao iniciar o DB: " + e.getMessage());
        }
    }


    /**
     * BLOCO 2: Inserção de Registro
     * Salva uma nova bipagem no banco de dados.
     */
    public static void salvarDados(Dados dados){
        String sql = """
                    INSERT INTO dados (
                        cod_barras, nome_arquivo, data, qtd_bordas_total, qtd_bordas_bipada
                    ) VALUES (?, ?, ?, ?, ?);
                """;

        // PreparedStatement: Previne erros com aspas e protege o banco (boa prática obrigatória)
        try(Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            //substitui o '?' pelos valores abaixo 
            pstmt.setString(1, dados.codBarras());
            pstmt.setString(2, dados.nomeArquivo());
            pstmt.setString(3, dados.data().format(FORMATTER));
            pstmt.setInt(4, dados.qtdBordaTotal());
            pstmt.setInt(5, dados.qtdBordabipada());

            pstmt.executeUpdate();
            System.out.println("Dados salvo com sucesso: " + dados.codBarras());
        }
        catch(SQLException e){
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }


    /**
     * BLOCO 3: Busca de Registro
     * Busca se há registros bipados no banco de dados.
    */
    public static Dados buscarDados(String desenho){
        String sql = 
                """
                    SELECT id, cod_barras, nome_arquivo, data, qtd_bordas_total, qtd_bordas_bipada 
                    FROM dados 
                    WHERE cod_barras = ? 
                    ORDER BY id 
                    DESC LIMIT 1
                """;
        
        try(Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            //substitui o '?' pelo valor abaixo 
            pstmt.setString(1, desenho);

            // Comandos SELECT precisam de pstmt.executeQuery(), que devolve um ResultSet
            try(ResultSet result = pstmt.executeQuery()){
                if(result.next()){
                    return new Dados(
                        result.getInt("id"),
                        result.getString("cod_barras"),
                        result.getString("nome_arquivo"),
                        result.getInt("qtd_bordas_total"),
                        result.getInt("qtd_bordas_bipada"),
                        LocalDateTime.parse(result.getString("data"), FORMATTER)
                    );
                }
            }
            catch(SQLException e){
                System.err.println("Erro ao buscar os dados: "+ e.getMessage());
            }
        }
        catch(SQLException e){
            System.err.println("Erro ao buscar desenho: " + e.getMessage());
        }

        // Se não houver nenhum registro retorna null.
        return null;
    }
}
