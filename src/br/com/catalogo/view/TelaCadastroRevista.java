package br.com.catalogo.view;

import br.com.catalogo.dao.EditoraDAO;
import br.com.catalogo.dao.RevistaDAO;
import br.com.catalogo.model.Editora;
import br.com.catalogo.model.Revista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class TelaCadastroRevista extends JFrame {

    private JTabbedPane tabbedPane; // Componente de abas para separar Cadastro de Pesquisa
    private JTextField campoId; // Campo oculto para armazenar o ID durante a edição

    // --- Componentes da Aba de Cadastro ---
    private JTextField campoNome;
    private JTextField campoAno;

    // JComboBox tipado com <Editora>: Armazena objetos inteiros, não apenas Strings.
    private JComboBox<Editora> comboEditora;
    private JButton btnSalvar;
    private JButton btnLimpar;

    // --- Componentes da Aba de Pesquisa ---
    private JTable tabelaRevistas;
    private DefaultTableModel tableModel;

    public TelaCadastroRevista() {
        super("CADASTRO DE REVISTAS");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setSize(600, 450);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        criarAbaCadastro();
        criarAbaPesquisa();

        this.add(tabbedPane);

        // Carregamento Inicial de Dados
        carregarComboEditoras(); // Busca editoras no banco para preencher a caixa de seleção
        carregarTabela();        // Busca revistas para preencher a listagem

        setVisible(true);
    }


    private void criarAbaCadastro() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ID Oculto (Necessário para saber se é Insert ou Update)
        campoId = new JTextField();
        campoId.setVisible(false);
        this.add(campoId);

        // Linha 1: Nome
        painel.add(new JLabel("Nome da Revista:"));
        campoNome = new JTextField();
        painel.add(campoNome);

        // Linha 2: Ano
        painel.add(new JLabel("Ano de Início:"));
        campoAno = new JTextField();
        painel.add(campoAno);
        // Linha 3: Editora (Seleção de Chave Estrangeira)
        painel.add(new JLabel("Editora:"));
        comboEditora = new JComboBox<>(); // O modelo será carregado via banco
        painel.add(comboEditora);

        // Linha 4: Botões
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        painel.add(btnSalvar);
        painel.add(btnLimpar);

        // Listeners
        btnSalvar.addActionListener(e -> salvarRevista());

        btnLimpar.addActionListener(e -> {
            campoNome.setText("");
            campoAno.setText("");
            campoId.setText("");
            comboEditora.setSelectedIndex(-1); // Reseta a seleção da combo
        });

        tabbedPane.addTab("1. Cadastro", painel);
    }


    private void criarAbaPesquisa() {
        JPanel painel = new JPanel(new BorderLayout());

        // --- PAINEL DE FILTRO ---
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltro.add(new JLabel("Filtrar por:"));

        // Opções de filtro para a pesquisa avançada
        String[] opcoes = {"Nome", "Editora"};
        JComboBox<String> comboFiltro = new JComboBox<>(opcoes);
        painelFiltro.add(comboFiltro);

        JTextField campoPesquisa = new JTextField(20);
        painelFiltro.add(campoPesquisa);

        JButton btnPesquisar = new JButton("Pesquisar");
        painelFiltro.add(btnPesquisar);

        JButton btnLimparPesquisa = new JButton("X");
        painelFiltro.add(btnLimparPesquisa);

        painel.add(painelFiltro, BorderLayout.NORTH);

        // --- Tabela de Resultados ---
        String[] colunas = {"ID", "Nome", "Ano", "Editora"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaRevistas = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tabelaRevistas);
        painel.add(scroll, BorderLayout.CENTER);

        // --- Botões de Ação ---
        JPanel painelBotoes = new JPanel();
        JButton btnEditar = new JButton("Editar Selecionado");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        // Lógica dos Botões
        btnEditar.addActionListener(e -> editarRevista());
        btnExcluir.addActionListener(e -> excluirRevista());

        btnPesquisar.addActionListener(e -> {
            String termo = campoPesquisa.getText();
            String tipo = (String) comboFiltro.getSelectedItem();
            filtrarTabela(termo, tipo); // Chama pesquisa com filtro específico
        });

        btnLimparPesquisa.addActionListener(e -> {
            campoPesquisa.setText("");
            carregarTabela(); // Recarrega a lista completa
        });

        tabbedPane.addTab("2. Pesquisa", painel);
    }

    // --- MÉTODOS AUXILIARES E DE LÓGICA DE NEGÓCIO ---

    /**
     * Executa a busca no banco baseada no filtro escolhido (Nome da Revista ou Nome da Editora).
     */
    private void filtrarTabela(String termo, String tipo) {
        tableModel.setRowCount(0);
        RevistaDAO dao = new RevistaDAO();
        // O DAO decide qual SQL usar baseado no 'tipo'
        List<Revista> lista = dao.pesquisar(termo, tipo);

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum registro encontrado.");
        } else {
            for (Revista r : lista) {
                Object[] linha = {
                        r.getId_revista(),
                        r.getNome(),
                        r.getAno_inicio(),
                        r.getEditora().getNome() // Navegação no objeto para exibir nome da Editora
                };
                tableModel.addRow(linha);
            }
        }
    }


    private void carregarComboEditoras() {
        EditoraDAO dao = new EditoraDAO();
        List<Editora> lista = dao.listar();

        comboEditora.removeAllItems();

        for (Editora e : lista) {
            // O JComboBox usa o método toString() da classe Editora para exibir o nome na tela,
            // mas guarda o Objeto inteiro (com ID) na memória.
            comboEditora.addItem(e);
        }
        comboEditora.setSelectedIndex(-1);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        RevistaDAO dao = new RevistaDAO();
        List<Revista> lista = dao.listar();

        for (Revista r : lista) {
            Object[] linha = {
                    r.getId_revista(),
                    r.getNome(),
                    r.getAno_inicio(),
                    r.getEditora().getNome()
            };
            tableModel.addRow(linha);
        }
    }

    private void salvarRevista() {
        String nome = campoNome.getText();
        String anoTexto = campoAno.getText();
        // Recupera o Objeto Editora selecionado na Combo
        Editora editoraSelecionada = (Editora) comboEditora.getSelectedItem();
        String idTexto = campoId.getText();

        if (nome.isEmpty() || anoTexto.isEmpty() || editoraSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }

        try {
            int ano = Integer.parseInt(anoTexto);

            Revista revista = new Revista();
            revista.setNome(nome);
            revista.setAno_inicio(ano);
            revista.setEditora(editoraSelecionada); // Define a FK (Chave Estrangeira)

            RevistaDAO dao = new RevistaDAO();

            if (idTexto.isEmpty()) {
                dao.salvar(revista);
                JOptionPane.showMessageDialog(this, "Revista salva com sucesso!");
            } else {
                revista.setId_revista(Integer.parseInt(idTexto));
                dao.atualizar(revista);
                JOptionPane.showMessageDialog(this, "Revista atualizada com sucesso!");
            }

            // Limpeza do formulário
            campoNome.setText("");
            campoAno.setText("");
            campoId.setText("");
            comboEditora.setSelectedIndex(-1);
            carregarTabela();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O Ano deve ser um número válido.");
        }
    }

    /**
     * Carrega os dados da linha selecionada na tabela para a aba de cadastro.
     * Inclui a lógica para selecionar a Editora correta no ComboBox.
     */
    private void editarRevista() {
        int linha = tabelaRevistas.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma revista para editar.");
            return;
        }

        String id = tabelaRevistas.getValueAt(linha, 0).toString();
        String nome = tabelaRevistas.getValueAt(linha, 1).toString();
        String ano = tabelaRevistas.getValueAt(linha, 2).toString();
        String nomeEditora = tabelaRevistas.getValueAt(linha, 3).toString(); // Nome que está na tabela

        campoId.setText(id);
        campoNome.setText(nome);
        campoAno.setText(ano);

        // Lógica de Seleção da ComboBox:
        // Varre os itens da Combo para encontrar qual objeto Editora possui o mesmo nome
        // daquele que veio da tabela.
        for (int i = 0; i < comboEditora.getItemCount(); i++) {
            Editora e = comboEditora.getItemAt(i);
            if (e.getNome().equals(nomeEditora)) {
                comboEditora.setSelectedIndex(i);
                break;
            }
        }

        tabbedPane.setSelectedIndex(0); // Muda o foco para a aba de cadastro
    }

    private void excluirRevista() {
        int linha = tabelaRevistas.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma revista para excluir.");
            return;
        }

        String idTexto = tabelaRevistas.getValueAt(linha, 0).toString();
        String nome = tabelaRevistas.getValueAt(linha, 1).toString();

        int op = JOptionPane.showConfirmDialog(this, "Excluir a revista '" + nome + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) {
            RevistaDAO dao = new RevistaDAO();
            dao.excluir(Integer.parseInt(idTexto));
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Excluído com sucesso!");
        }
    }
}