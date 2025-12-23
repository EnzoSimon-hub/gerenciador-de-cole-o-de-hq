package br.com.catalogo.dao;

import br.com.catalogo.model.Editora;
import br.com.catalogo.util.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class EditoraDAO {

    // Método para GRAVAR (INSERT) uma nova editora no banco
    public void salvar(Editora editora) {

        // 1. O Molde SQL
        // Note que NÃO mandamos o ID. O banco cria o ID sozinho (Auto Increment).
        String sql = "INSERT INTO editora (nome) VALUES (?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            // Pegamos o nome de dentro do objeto Editora que recebemos
            stmt.setString(1, editora.getNome());

            // 3. Executar
            // Como é uma gravação, usamos .execute() e não .executeQuery()
            stmt.execute();

            System.out.println("Editora gravada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao salvar editora: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Método para LER (SELECT) todas as editoras do banco
    // O "r" do "CRUD"
    public List<Editora> listar() {
        // 1. A Lista vazia que vamos preencher e retornar
        List<Editora> editoras = new ArrayList<>();

        // 2. O Molde SQL (queremos todos os campos de todas as editoras)
        String sql = "SELECT id_editora, nome FROM editora";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             // 3. Executamos a consulta. Não há "?" para preencher.
             // O ResultSet (rs) vai receber todas as linhas encontradas.
             ResultSet rs = stmt.executeQuery()) {

            // 4. Lendo a "Gaveta" (ResultSet)
            // rs.next() agora será usado para iterar, linha por linha
            while (rs.next()) {
                // A. Criamos um novo objeto Editora vazio
                Editora editora = new Editora();

                // B. Pegamos os dados da LINHA ATUAL do banco e preenchemos o objeto
                editora.setId_editora(rs.getInt("id_editora"));
                editora.setNome(rs.getString("nome"));

                // C. Adicionamos o objeto Editora à nossa lista
                editoras.add(editora);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar editoras: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Retorna a lista (vazia se deu erro, ou cheia se deu certo)
        return editoras;
    }

    //U do "CRUD"



    // Método para ALTERAR (UPDATE) o nome de uma editora
    public void atualizar(Editora editora) {

        // 1. O Molde SQL: Atualiza o nome ONDE o ID for o certo
        String sql = "UPDATE editora SET nome = ? WHERE id_editora = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setString(1, editora.getNome());          // O primeiro '?' é o NOVO NOME
            stmt.setInt(2, editora.getId_editora());       // O segundo '?' é o ID (quem será alterado)

            // 3. Executar. Retorna o número de linhas afetadas.
            stmt.executeUpdate();

            System.out.println("Editora ID " + editora.getId_editora() + " alterada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao alterar editora: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // D do "CRUD
    }
    // D do "CRUD
// Método para EXCLUIR (DELETE) uma editora
    public void excluir(int id_editora) {

        // 1. O Molde SQL: Apaga ONDE o ID for o certo
        String sql = "DELETE FROM editora WHERE id_editora = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setInt(1, id_editora); // O único '?' é o ID de quem será apagado

            // 3. Executar
            stmt.executeUpdate();

            System.out.println("Editora ID " + id_editora + " excluída com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir editora: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}