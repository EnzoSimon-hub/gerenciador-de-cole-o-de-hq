package br.com.catalogo.view;

import br.com.catalogo.dao.EditoraDAO;
import br.com.catalogo.model.Editora;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; // NOVO IMPORT
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaCadastroEditora extends JFrame {

    private JTabbedPane tabbedPane;

    // Adicione nos componentes da classe
    private JTextField campoId;

    // --- Componentes da Aba CADASTRO ---
    private JPanel painelCadastro;
    private JTextField campoNome;
    private JButton btnSalvar;
    private JButton btnLimpar;

    // --- Componentes da Aba PESQUISA ---
    private JPanel painelPesquisa;
    private JTable tabelaEditoras;
    private DefaultTableModel tableModel; // O modelo que armazena os dados
    private JScrollPane scrollPane; // Barra de rolagem para a tabela

    // Construtor
    public TelaCadastroEditora() {
        super("CADASTRO DE EDITORAS");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        criarAbaCadastro();
        criarAbaPesquisa();

        this.add(tabbedPane);

        carregarTabela();

        setVisible(true);
    }

    // --- Aba Cadastro (Código anterior, mas com o ajuste no Salvar) ---
    private void criarAbaCadastro() {
        painelCadastro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Adicione esta linha no final do seu método criarAbaCadastro()
        campoId = new JTextField(5);
        campoId.setVisible(false); // Torna o campo invisível
        painelCadastro.add(campoId);


        // --- Componentes ---
        painelCadastro.add(new JLabel("Nome da Editora:"));
        campoNome = new JTextField(30);
        painelCadastro.add(campoNome);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 30));
        painelCadastro.add(btnSalvar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setPreferredSize(new Dimension(100, 30));
        painelCadastro.add(btnLimpar);

        // --- LIGAÇÃO COM O DAO ---
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarEditora();
            }
        });

        btnLimpar.addActionListener(e -> campoNome.setText(""));

        tabbedPane.addTab("1. Cadastro", painelCadastro);
    }

    // --- Aba Pesquisa (Implementação do JTable) ---
    private void criarAbaPesquisa() {
        painelPesquisa = new JPanel(new BorderLayout());

        // 1. Cria o JTable (igual ao anterior)
        String[] colunas = {"ID", "Nome"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEditoras = new JTable(tableModel);
        scrollPane = new JScrollPane(tabelaEditoras);

        painelPesquisa.add(scrollPane, BorderLayout.CENTER);

        // --- NOVO PAINEL PARA OS BOTÕES ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // 2. Cria os Botões de Ação
        JButton btnEditar = new JButton("Editar Selecionado");
        JButton btnExcluir = new JButton("Excluir Selecionado");

        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // 3. Adiciona o painel de botões ao Sul
        painelPesquisa.add(painelBotoes, BorderLayout.SOUTH);

        // --- LÓGICA DE AÇÃO ---
        btnEditar.addActionListener(e -> editarEditora());
        btnExcluir.addActionListener(e -> excluirEditora()); // Excluir já está corrigido para single click

        // 4. Adiciona o painel de pesquisa à aba
        tabbedPane.addTab("2. Pesquisa", painelPesquisa);
    }

    // --- O CÓDIGO MAIS IMPORTANTE: LER DO DAO E PREENCHER O JTable ---
    private void carregarTabela() {
        // Limpa as linhas atuais da tabela
        tableModel.setRowCount(0);

        // Instancia o DAO e chama o método listar() que você criou
        EditoraDAO dao = new EditoraDAO();
        List<Editora> editoras = dao.listar();

        // Itera sobre a lista de objetos Editora que veio do banco
        for (Editora editora : editoras) {
            // Cria um array de objeto com os dados da linha
            Object[] linha = {
                    editora.getId_editora(),
                    editora.getNome()
            };

            // Adiciona a linha ao modelo da tabela
            tableModel.addRow(linha);
        }
    }

    private void salvarEditora() {
        String nome = campoNome.getText();
        String idTexto = campoId.getText();

        // 1. Validação básica (nome)
        if (nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da editora não pode ser vazio.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EditoraDAO dao = new EditoraDAO();

        if (idTexto.isEmpty()) {
            // A. INSERT (Criação - ID VAZIO)
            Editora novaEditora = new Editora(nome);
            dao.salvar(novaEditora);
            JOptionPane.showMessageDialog(this, "Editora " + nome + " SALVA com sucesso!");

        } else {
            // B. UPDATE (Alteração - ID PRESENTE)
            int id = Integer.parseInt(idTexto);
            Editora editoraParaAlterar = new Editora(id, nome);
            dao.atualizar(editoraParaAlterar);
            JOptionPane.showMessageDialog(this, "Editora ID " + id + " ALTERADA com sucesso!");
        }

        // 3. Limpeza e Atualização
        campoNome.setText("");
        campoId.setText(""); // Limpa o ID Oculto
        carregarTabela();
    }

    private void excluirEditora() {

        // 1. Tenta pegar a linha que está selecionada na tabela (com um clique simples)
        int linhaSelecionada = tabelaEditoras.getSelectedRow();

        // 2. VERIFICAÇÃO: Se for -1, significa que o usuário não clicou em nada
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma editora na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Pega o ID da Coluna 0 (que é a coluna ID) da linha que foi clicada
        String idTexto = tabelaEditoras.getValueAt(linhaSelecionada, 0).toString();
        String nome = tabelaEditoras.getValueAt(linhaSelecionada, 1).toString();

        // 4. Pergunta de Confirmação (Boas Práticas de UX)
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a Editora '" + nome + "' (ID " + idTexto + ")?",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idTexto);
                EditoraDAO dao = new EditoraDAO();
                dao.excluir(id);

                JOptionPane.showMessageDialog(this, "Editora " + nome + " excluída com sucesso!");
            } catch (Exception e) {
                // Captura a exceção de Chave Estrangeira se a Editora tiver Revistas ligadas
                JOptionPane.showMessageDialog(this, "Erro ao excluir! Pode haver Revistas ligadas a esta Editora.", "Erro de Chave Estrangeira", JOptionPane.ERROR_MESSAGE);
            }

            // 5. Limpeza e Atualização
            campoNome.setText("");
            campoId.setText(""); // Limpa o ID oculto (que não usamos, mas por segurança)
            carregarTabela();
        }

    }

    private void editarEditora() {

        // 1. Pega a linha selecionada (funciona com clique simples)
        int linhaSelecionada = tabelaEditoras.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma editora na tabela para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Pega os dados diretamente da tabela
        String id = tabelaEditoras.getValueAt(linhaSelecionada, 0).toString();
        String nome = tabelaEditoras.getValueAt(linhaSelecionada, 1).toString();

        // 3. Move os dados para os campos do formulário
        campoId.setText(id);
        campoNome.setText(nome);

        // 4. Muda a aba para que o usuário possa editar
        tabbedPane.setSelectedIndex(0);

        // 5. Opcional: Coloca o foco no campo de nome
        campoNome.requestFocusInWindow();
    }

}