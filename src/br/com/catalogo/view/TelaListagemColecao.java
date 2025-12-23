package br.com.catalogo.view;

import br.com.catalogo.dao.RevistaDAO;
import br.com.catalogo.dao.EditoraDAO;
import br.com.catalogo.dao.EdicaoDAO;
import br.com.catalogo.model.Edicao;
import br.com.catalogo.model.Editora;
import br.com.catalogo.model.Revista;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaListagemColecao extends JFrame {

    private JTable tabela;
    private DefaultTableModel tableModel;
    private JRadioButton radioNumero;
    private JRadioButton radioData;
    private ButtonGroup grupoOrdenacao;
    private JComboBox<Editora> comboFiltroEditora;
    private JComboBox<Revista> comboFiltroRevista; // <--- NOVO

    public TelaListagemColecao() {
        super("CONSULTA GERAL DA COLEÇÃO");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        criarPainelSuperior();
        criarTabela();

        // Carrega os dados iniciais
        atualizarTabela();

        setVisible(true);
    }

    private void criarPainelSuperior() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder("Filtros e Ordenação"));

        // --- 1. Filtro por Editora ---
        painel.add(new JLabel("Editora: "));
        comboFiltroEditora = new JComboBox<>();
        carregarComboEditoras();
        painel.add(comboFiltroEditora);

        painel.add(Box.createHorizontalStrut(10)); // Espaço

        // --- 2. NOVO: Filtro por Revista ---
        painel.add(new JLabel("Revista: "));
        comboFiltroRevista = new JComboBox<>();
        carregarComboRevistas(); // Vamos criar esse método abaixo
        painel.add(comboFiltroRevista);

        painel.add(Box.createHorizontalStrut(20)); // Espaço maior

        // --- 3. Ordenação (Igual antes) ---
        painel.add(new JLabel("Ordenar: "));
        radioNumero = new JRadioButton("Nº");
        radioData = new JRadioButton("Data");
        radioNumero.setSelected(true);

        grupoOrdenacao = new ButtonGroup();
        grupoOrdenacao.add(radioNumero);
        grupoOrdenacao.add(radioData);

        painel.add(radioNumero);
        painel.add(radioData);

        JButton btnAtualizar = new JButton("Aplicar");
        painel.add(btnAtualizar);

        btnAtualizar.addActionListener(e -> atualizarTabela());

        add(painel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"Revista", "Edição Nº", "Data", "Editora", "ID"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarComboRevistas() {
        RevistaDAO dao = new RevistaDAO();
        List<Revista> lista = dao.listar();

        comboFiltroRevista.removeAllItems();

        // Opção para ver tudo
        Revista todas = new Revista();
        todas.setId_revista(0);
        todas.setNome("--- TODAS ---");
        comboFiltroRevista.addItem(todas);

        for (Revista r : lista) {
            comboFiltroRevista.addItem(r);
        }
    }

    private void carregarComboEditoras() {
        EditoraDAO dao = new EditoraDAO();
        List<Editora> lista = dao.listar();

        comboFiltroEditora.removeAllItems();
        // Item falso para "Todas" (ID 0)
        Editora todas = new Editora();
        todas.setId_editora(0);
        todas.setNome("--- TODAS ---");
        comboFiltroEditora.addItem(todas);

        for (Editora e : lista) {
            comboFiltroEditora.addItem(e);
        }
    }

    // --- O CÉREBRO DA TELA ---
    // ATENÇÃO: Verifique se este nome bate com a chamada no botão (linha 69)
    private void atualizarTabela() {
        tableModel.setRowCount(0);

        EdicaoDAO dao = new EdicaoDAO();
        List<Edicao> listaDeResultados;

        Editora editoraSel = (Editora) comboFiltroEditora.getSelectedItem();
        Revista revistaSel = (Revista) comboFiltroRevista.getSelectedItem();

        // LÓGICA DE DECISÃO (A Cascata de Filtros)

        if (revistaSel != null && revistaSel.getId_revista() != 0) {
            // 1. Prioridade Máxima: Se escolheu REVISTA, filtra por ela
            listaDeResultados = dao.listarPorRevista(revistaSel.getId_revista());

        } else if (editoraSel != null && editoraSel.getId_editora() != 0) {
            // 2. Prioridade Média: Se escolheu EDITORA (e revista é 'Todas'), filtra por editora
            listaDeResultados = dao.listarPorEditora(editoraSel.getId_editora());

        } else {
            // 3. Padrão: Se tudo é 'TODAS', lista tudo ordenado
            String criterio = radioData.isSelected() ? "data" : "numero";
            listaDeResultados = dao.listarOrdenado(criterio);
        }

        // Preenchimento da Tabela (Igualzinho antes)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Edicao e : listaDeResultados) {
            String dataBonita = "";
            if (e.getData_publicacao() != null) {
                dataBonita = sdf.format(e.getData_publicacao());
            }

            // Verifica nulos para não quebrar
            String nomeRevista = (e.getRevista() != null) ? e.getRevista().getNome() : "?";
            String nomeEditora = (e.getRevista() != null && e.getRevista().getEditora() != null) ?
                    e.getRevista().getEditora().getNome() : "?";

            Object[] linha = {
                    nomeRevista,
                    e.getNumero(),
                    dataBonita,
                    nomeEditora,
                    e.getId_edicao()
            };
            tableModel.addRow(linha);
        }
    }
}