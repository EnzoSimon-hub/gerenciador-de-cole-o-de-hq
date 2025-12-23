package br.com.catalogo.dao;

import br.com.catalogo.model.Revista;
import br.com.catalogo.model.Editora; // Precisamos do modelo da Editora
import br.com.catalogo.util.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RevistaDAO {

    // Método para GRAVAR (INSERT) uma nova revista no banco
    public void salvar(Revista revista) {

        // 1. O Molde SQL. Incluímos o FK id_editora
        String sql = "INSERT INTO revista (nome, ano_inicio, id_editora) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setString(1, revista.getNome());       // 1º ? é o Nome da Revista
            stmt.setInt(2, revista.getAno_inicio());    // 2º ? é o Ano de Início

            // 💡 A MÁGICA DO RELACIONAMENTO (FK):
            // Pegamos o ID da Editora que está dentro do objeto Revista
            stmt.setInt(3, revista.getEditora().getId_editora());

            // 3. Executar
            stmt.execute();
            System.out.println("Revista gravada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao salvar revista: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Revista> listar() {
        List<Revista> revistas = new ArrayList<>();

        // 1. O Molde SQL: Usamos JOIN para pegar o NOME da Editora
        String sql = "SELECT r.*, e.nome AS nome_editora FROM revista r " +
                "JOIN editora e ON r.id_editora = e.id_editora";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // 2. Lendo a "Gaveta" (ResultSet)
            while (rs.next()) {

                // A. Cria o Objeto Editora (a parte dependente)
                Editora editora = new Editora();
                editora.setId_editora(rs.getInt("id_editora"));
                // Pegamos o nome que veio do JOIN (que apelidamos de 'nome_editora')
                editora.setNome(rs.getString("nome_editora"));

                // B. Cria o Objeto Revista (o objeto principal)
                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                revista.setNome(rs.getString("nome"));
                revista.setAno_inicio(rs.getInt("ano_inicio"));

                // C. Ligamos os Objetos (POO)
                revista.setEditora(editora);

                // D. Adicionamos a Revista à lista
                revistas.add(revista);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar revistas: " + e.getMessage());
            e.printStackTrace();
        }
        return revistas;
    }



    // Método para ALTERAR (UPDATE) uma revista
    public void atualizar(Revista revista) {

        // 1. O Molde SQL: Atualiza todos os campos de Revista (incluindo o FK) ONDE o ID for o certo
        String sql = "UPDATE revista SET nome = ?, ano_inicio = ?, id_editora = ? WHERE id_revista = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setString(1, revista.getNome());
            stmt.setInt(2, revista.getAno_inicio());

            // Puxamos o ID da Editora do objeto aninhado
            stmt.setInt(3, revista.getEditora().getId_editora());

            // O 4º '?' é o ID da Revista que será alterada (a Chave Primária)
            stmt.setInt(4, revista.getId_revista());

            // 3. Executar a alteração
            stmt.executeUpdate();

            System.out.println("Revista ID " + revista.getId_revista() + " alterada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao alterar revista: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Método para EXCLUIR (DELETE) uma revista
    public void excluir(int id_revista) {

        // 1. O Molde SQL: Apaga ONDE o ID for o certo
        String sql = "DELETE FROM revista WHERE id_revista = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setInt(1, id_revista); // O único '?' é o ID que será apagado

            // 3. Executar
            stmt.executeUpdate();

            System.out.println("Revista ID " + id_revista + " excluída com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir revista: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    // tipoFiltro: pode ser "Nome" ou "Editora"
    public List<Revista> pesquisar(String termo, String tipoFiltro) {
        List<Revista> revistas = new ArrayList<>();

        String sql = "";

        // Monta o SQL dinamicamente baseado no filtro escolhido
        if (tipoFiltro.equals("Nome")) {
            // Busca por parte do nome da Revista
            sql = "SELECT r.*, e.nome AS nome_editora FROM revista r " +
                    "JOIN editora e ON r.id_editora = e.id_editora " +
                    "WHERE r.nome LIKE ?";
        } else if (tipoFiltro.equals("Editora")) {
            // Busca por parte do nome da Editora
            sql = "SELECT r.*, e.nome AS nome_editora FROM revista r " +
                    "JOIN editora e ON r.id_editora = e.id_editora " +
                    "WHERE e.nome LIKE ?";
        } else {
            // Se vier algo errado, retorna lista vazia ou busca tudo
            return listar();
        }

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, termo + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // (Mesma lógica de montagem do listar)
                Editora editora = new Editora();
                editora.setId_editora(rs.getInt("id_editora"));
                editora.setNome(rs.getString("nome_editora"));

                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                revista.setNome(rs.getString("nome"));
                revista.setAno_inicio(rs.getInt("ano_inicio"));
                revista.setEditora(editora);

                revistas.add(revista);
            }

        } catch (SQLException e) {
            System.out.println("Erro na pesquisa: " + e.getMessage());
            e.printStackTrace(); //RevistaDAO.java:45
        }
        return revistas;
    }
}