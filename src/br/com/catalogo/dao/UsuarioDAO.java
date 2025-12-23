package br.com.catalogo.dao;

import br.com.catalogo.model.Usuario;
import br.com.catalogo.util.ConnectionFactory; // Importa nossa classe de conexão

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean verificarLogin(String login, String senha) {



        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?";


        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, login); // O primeiro "?" vira o login
            stmt.setString(2, senha); // O segundo "?" vira a senha

            //  Executa o comando SQL
            try (ResultSet rs = stmt.executeQuery()) {

                //  Verifica se o banco retornou algum resultado
                // rs.next() tenta ir para a "próxima linha" do resultado.
                // Se ele conseguir, é porque achou o usuário.
                return rs.next();
            }

        } catch (SQLException e) {
            // Se der erro de SQL, mostra o problema e retorna false
            System.out.println("Erro ao verificar login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}