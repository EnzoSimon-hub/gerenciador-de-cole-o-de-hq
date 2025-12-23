package br.com.catalogo.dao;

import br.com.catalogo.model.Edicao;
import br.com.catalogo.model.Revista;
import br.com.catalogo.util.ConnectionFactory;
import br.com.catalogo.model.Editora;


import java.util.Calendar; // Adicione este import no topo para criar a data
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Adicionamos o import ESPECÍFICO para o tipo Date do SQL
import java.sql.Date;

public class EdicaoDAO {

    // Método para GRAVAR (INSERT) uma nova edição no banco
    public void salvar(Edicao edicao) {

        // 1. O Molde SQL
        String sql = "INSERT INTO edicao (numero, data_publicacao, id_revista) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 2. Preencher o Molde
            stmt.setInt(1, edicao.getNumero());

            // 💡 A MÁGICA DA DATA: Conversão de java.util.Date para java.sql.Date
            // Pegamos o tempo (em milissegundos) da data Java e criamos um objeto Date do SQL
            stmt.setDate(2, new Date(edicao.getData_publicacao().getTime()));

            // A Chave Estrangeira (FK)
            stmt.setInt(3, edicao.getRevista().getId_revista());

            // 3. Executar
            stmt.execute();
            System.out.println("Edição " + edicao.getNumero() + " gravada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao salvar edição: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Método para LER (SELECT) todas as edições
    public List<Edicao> listar() {
        List<Edicao> edicoes = new ArrayList<>();

        // 1. O Molde SQL: JOIN com a Revista para pegar o nome da Revista (r.nome)
        String sql = "SELECT e.*, r.nome AS nome_revista FROM edicao e " +
                "JOIN revista r ON e.id_revista = r.id_revista";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // 2. Lendo a Gaveta (ResultSet)
            while (rs.next()) {

                // A. Cria o Objeto Revista (o pai)
                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                // Pega o nome que veio do JOIN (apelidado de 'nome_revista')
                revista.setNome(rs.getString("nome_revista"));

                // B. Cria o Objeto Edicao (o filho)
                Edicao edicao = new Edicao();
                edicao.setId_edicao(rs.getInt("id_edicao"));
                edicao.setNumero(rs.getInt("numero"));

                // 💡 A MÁGICA DA DATA INVERSA: Converte de java.sql.Date (do banco) para java.util.Date (do Java)
                // O método .getDate() já faz a maior parte do trabalho por nós.
                edicao.setData_publicacao(rs.getDate("data_publicacao"));

                // C. Liga os Objetos (POO)
                edicao.setRevista(revista);

                // D. Adiciona à lista
                edicoes.add(edicao);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar edições: " + e.getMessage());
            e.printStackTrace();
        }
        return edicoes;
    }

    // Método para ALTERAR (UPDATE) uma edição
    public void atualizar(Edicao edicao) {

        // Atualiza três campos (número, data, FK) ONDE o ID for o certo
        String sql = "UPDATE edicao SET numero = ?, data_publicacao = ?, id_revista = ? WHERE id_edicao = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // 1. Preenche os novos valores
            stmt.setInt(1, edicao.getNumero());
            stmt.setDate(2, new Date(edicao.getData_publicacao().getTime())); // Conversão da data
            stmt.setInt(3, edicao.getRevista().getId_revista()); // A nova FK

            // 2. O filtro de segurança (PK)
            stmt.setInt(4, edicao.getId_edicao());

            stmt.executeUpdate();
            System.out.println("Edição ID " + edicao.getId_edicao() + " alterada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao alterar edição: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Método para EXCLUIR (DELETE) uma edição
    public void excluir(int id_edicao) {

        String sql = "DELETE FROM edicao WHERE id_edicao = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id_edicao);
            stmt.executeUpdate();

            System.out.println("Edição ID " + id_edicao + " excluída com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir edição: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    // Método Novo: Listar com ORDENAÇÃO (Requisito 05)
    // criterio: pode ser "numero" ou "data"
    public List<Edicao> listarOrdenado(String criterio) {
        List<Edicao> edicoes = new ArrayList<>();

        // MUDANÇA: Trocamos JOIN por LEFT JOIN para garantir que tudo apareça
        String sql = "SELECT e.*, r.nome AS nome_revista, r.id_revista, r.id_editora, ed.nome AS nome_editora " +
                "FROM edicao e " +
                "LEFT JOIN revista r ON e.id_revista = r.id_revista " +
                "LEFT JOIN editora ed ON r.id_editora = ed.id_editora "; // JOIN duplo para pegar a Editora também!

        if (criterio.equals("data")) {
            sql += "ORDER BY e.data_publicacao DESC";
        } else {
            sql += "ORDER BY e.numero ASC";
        }

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Monta a Editora (Agora pegamos o nome dela direto do SQL)
                Editora editora = new Editora();
                editora.setId_editora(rs.getInt("id_editora"));
                editora.setNome(rs.getString("nome_editora"));

                // Monta a Revista
                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                revista.setNome(rs.getString("nome_revista"));
                revista.setEditora(editora); // Liga a editora na revista

                // Monta a Edição
                Edicao edicao = new Edicao();
                edicao.setId_edicao(rs.getInt("id_edicao"));
                edicao.setNumero(rs.getInt("numero"));
                edicao.setData_publicacao(rs.getDate("data_publicacao"));
                edicao.setRevista(revista);

                edicoes.add(edicao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return edicoes;
    }

    // Método Novo: Filtrar edições por Editora
    public List<Edicao> listarPorEditora(int idEditora) {
        List<Edicao> edicoes = new ArrayList<>();

        // SQL Corrigido com LEFT JOIN e busca completa
        String sql = "SELECT e.*, r.nome AS nome_revista, r.id_revista, r.id_editora, ed.nome AS nome_editora " +
                "FROM edicao e " +
                "LEFT JOIN revista r ON e.id_revista = r.id_revista " +
                "LEFT JOIN editora ed ON r.id_editora = ed.id_editora " +
                "WHERE r.id_editora = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idEditora);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // (Mesma lógica de montagem do método acima)
                Editora editora = new Editora();
                editora.setId_editora(rs.getInt("id_editora"));
                editora.setNome(rs.getString("nome_editora"));

                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                revista.setNome(rs.getString("nome_revista"));
                revista.setEditora(editora);

                Edicao edicao = new Edicao();
                edicao.setId_edicao(rs.getInt("id_edicao"));
                edicao.setNumero(rs.getInt("numero"));
                edicao.setData_publicacao(rs.getDate("data_publicacao"));
                edicao.setRevista(revista);

                edicoes.add(edicao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return edicoes;
    }
    // Método Novo: Filtrar edições por Revista (Título)
    public List<Edicao> listarPorRevista(int idRevista) {
        List<Edicao> edicoes = new ArrayList<>();

        // SQL: O filtro agora é direto na tabela edicao (e.id_revista)
        String sql = "SELECT e.*, r.nome AS nome_revista, r.id_revista, r.id_editora, ed.nome AS nome_editora " +
                "FROM edicao e " +
                "JOIN revista r ON e.id_revista = r.id_revista " +
                "JOIN editora ed ON r.id_editora = ed.id_editora " +
                "WHERE e.id_revista = ?"; // <--- ONDE MUDOU

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idRevista);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Montagem do objeto (igual aos outros)
                Editora editora = new Editora();
                editora.setId_editora(rs.getInt("id_editora"));
                editora.setNome(rs.getString("nome_editora"));

                Revista revista = new Revista();
                revista.setId_revista(rs.getInt("id_revista"));
                revista.setNome(rs.getString("nome_revista"));
                revista.setEditora(editora);

                Edicao edicao = new Edicao();
                edicao.setId_edicao(rs.getInt("id_edicao"));
                edicao.setNumero(rs.getInt("numero"));
                edicao.setData_publicacao(rs.getDate("data_publicacao"));
                edicao.setRevista(revista);

                edicoes.add(edicao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return edicoes;
    }



}