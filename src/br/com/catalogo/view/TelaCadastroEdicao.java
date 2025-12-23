package br.com.catalogo.view;

import br.com.catalogo.dao.EdicaoDAO;
import br.com.catalogo.dao.RevistaDAO;
import br.com.catalogo.model.Edicao;
import br.com.catalogo.model.Revista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaCadastroEdicao extends JFrame {

    private JTabbedPane tabbedPane;
    private JTextField campoId;

    // --- Componentes Aba Cadastro ---
    private JTextField campoNumero;
    private JFormattedTextField campoData; // Campo especial para data
    private JComboBox<Revista> comboRevista; // Lista de Revistas
    private JButton btnSalvar;
    private JButton btnLimpar;

    // --- Componentes Aba Pesquisa ---
    private JTable tabelaEdicoes;
    private DefaultTableModel tableModel;

    public TelaCadastroEdicao() {
        super("CADASTRO DE EDIÇÕES");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        criarAbaCadastro();
        criarAbaPesquisa();

        this.add(tabbedPane);

        carregarComboRevistas();
        carregarTabela();

        setVisible(true);
    }

    private void criarAbaCadastro() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ID Oculto
        campoId = new JTextField();
        campoId.setVisible(false);
        painel.add(campoId); // Adicionando no painel para evitar bugs, mas invisível
        painel.add(new JLabel("")); // Label vazio para preencher o grid

        // Linha 1: Número
        painel.add(new JLabel("Número da Edição:"));
        campoNumero = new JTextField();
        painel.add(campoNumero);

        // Linha 2: Data (Com Máscara)
        painel.add(new JLabel("Data (dd/MM/yyyy):"));
        try {
            // Cria a máscara ##/##/####
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            campoData = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            campoData = new JFormattedTextField(); // Se der erro, cria normal
        }
        painel.add(campoData);

        // Linha 3: Revista (Combobox)
        painel.add(new JLabel("Revista (Título):"));
        comboRevista = new JComboBox<>();
        painel.add(comboRevista);

        // Linha 4: Botões
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        painel.add(btnSalvar);
        painel.add(btnLimpar);

        // Ações
        btnSalvar.addActionListener(e -> salvarEdicao());

        btnLimpar.addActionListener(e -> {
            campoNumero.setText("");
            campoData.setValue(null);
            campoId.setText("");
            comboRevista.setSelectedIndex(-1);
        });

        tabbedPane.addTab("1. Cadastro", painel);
    }

    private void criarAbaPesquisa() {
        JPanel painel = new JPanel(new BorderLayout());

        // Colunas da Tabela
        String[] colunas = {"ID", "Número", "Data", "Revista"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEdicoes = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tabelaEdicoes);

        painel.add(scroll, BorderLayout.CENTER);

        // Botões de Ação
        JPanel painelBotoes = new JPanel();
        JButton btnEditar = new JButton("Editar Selecionado");
        JButton btnExcluir = new JButton("Excluir Selecionado");

        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarEdicao());
        btnExcluir.addActionListener(e -> excluirEdicao());

        tabbedPane.addTab("2. Pesquisa", painel);
    }

    // --- MÉTODOS DE DADOS ---

    private void carregarComboRevistas() {
        RevistaDAO dao = new RevistaDAO();
        List<Revista> lista = dao.listar();
        comboRevista.removeAllItems();
        for (Revista r : lista) {
            comboRevista.addItem(r); // Precisa do toString() na classe Revista!
        }
        comboRevista.setSelectedIndex(-1);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        EdicaoDAO dao = new EdicaoDAO();
        List<Edicao> lista = dao.listar();

        // Formatador para mostrar a data bonitinha na tabela
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Edicao e : lista) {
            String dataFormatada = "";
            if (e.getData_publicacao() != null) {
                dataFormatada = sdf.format(e.getData_publicacao());
            }

            Object[] linha = {
                    e.getId_edicao(),
                    e.getNumero(),
                    dataFormatada,
                    e.getRevista().getNome()
            };
            tableModel.addRow(linha);
        }
    }

    private void salvarEdicao() {
        String numTexto = campoNumero.getText();
        String dataTexto = campoData.getText();
        Revista revistaSelecionada = (Revista) comboRevista.getSelectedItem();
        String idTexto = campoId.getText();

        if (numTexto.isEmpty() || dataTexto.equals("__/__/____") || revistaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }

        try {
            int numero = Integer.parseInt(numTexto);

            // Converte String (dd/MM/yyyy) para Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data = sdf.parse(dataTexto);

            Edicao edicao = new Edicao();
            edicao.setNumero(numero);
            edicao.setData_publicacao(data);
            edicao.setRevista(revistaSelecionada);

            EdicaoDAO dao = new EdicaoDAO();

            if (idTexto.isEmpty()) {
                dao.salvar(edicao);
                JOptionPane.showMessageDialog(this, "Edição salva!");
            } else {
                edicao.setId_edicao(Integer.parseInt(idTexto));
                dao.atualizar(edicao);
                JOptionPane.showMessageDialog(this, "Edição atualizada!");
            }

            // Limpeza
            campoNumero.setText("");
            campoData.setValue(null);
            campoId.setText("");
            comboRevista.setSelectedIndex(-1);
            carregarTabela();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido.");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida.");
        }
    }

    private void editarEdicao() {
        int linha = tabelaEdicoes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma edição.");
            return;
        }

        String id = tabelaEdicoes.getValueAt(linha, 0).toString();
        String numero = tabelaEdicoes.getValueAt(linha, 1).toString();
        String data = tabelaEdicoes.getValueAt(linha, 2).toString();
        String nomeRevista = tabelaEdicoes.getValueAt(linha, 3).toString();

        campoId.setText(id);
        campoNumero.setText(numero);
        campoData.setText(data); // O JFormattedTextField aceita a String direto se bater com a máscara

        // Selecionar na combo
        for (int i = 0; i < comboRevista.getItemCount(); i++) {
            Revista r = comboRevista.getItemAt(i);
            if (r.getNome().equals(nomeRevista)) {
                comboRevista.setSelectedIndex(i);
                break;
            }
        }

        tabbedPane.setSelectedIndex(0);
    }

    private void excluirEdicao() {
        int linha = tabelaEdicoes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione para excluir.");
            return;
        }

        String idTexto = tabelaEdicoes.getValueAt(linha, 0).toString();

        int op = JOptionPane.showConfirmDialog(this, "Excluir edição?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            new EdicaoDAO().excluir(Integer.parseInt(idTexto));
            carregarTabela();
        }
    }
}