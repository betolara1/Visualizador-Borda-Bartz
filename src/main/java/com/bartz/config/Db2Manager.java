package com.bartz.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db2Manager {

    // Busca o arquivo .env na raiz do projeto
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String HOST = dotenv.get("DB2_HOST");
    private static final String PORTA = dotenv.get("DB2_PORT");
    private static final String BANCO = dotenv.get("DB2_DATABASE");
    private static final String USUARIO = dotenv.get("DB2_USER");
    private static final String SENHA = dotenv.get("DB2_PASSWORD");

    // URL do DB2
    private static final String URL = "jdbc:db2://" + HOST + ":" + PORTA + "/" + BANCO;


    // Retorna uma conexão ativa com o DB2
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }


    // testa a conexão
    public static void conexao(){
        try(Connection conn = getConnection()){
            if(conn != null && !conn.isClosed()){
                System.out.println("Conectado ao DB2 com sucesso!");
            }
        } 
        catch (SQLException e) {
            System.err.println("Falha na conexão com DB2: " + e.getMessage());
        }
    }
}
