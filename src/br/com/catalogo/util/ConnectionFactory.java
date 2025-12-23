package br.com.catalogo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Se estiver usando MySQL 8 ou superior, o timezone as vezes dá erro, essa URL previne isso
    private static final String URL = "jdbc:mysql://localhost:3306/catalogo_hqs?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "NoahSQL1007@@@";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão: " + e.getMessage());
        }
    }

}