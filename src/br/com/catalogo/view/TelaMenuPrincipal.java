package br.com.catalogo.view;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * TelaMenuPrincipal
 * Responsável por ser o hub central de navegação do sistema.
 * Implementa a Internacionalização (i18n) permitindo troca dinâmica de idioma.
 */
public class TelaMenuPrincipal extends JFrame {

    //componentes da interface
    private JButton btnEditora;
    private JButton btnRevista;
    private JButton btnEdicao;
    private JButton btnListagem;
    private JButton btnSair;
    private JLabel labelIdioma;

    // gaurda o idioma, padrao sendo pt
    private Locale localeAtual = new Locale("pt");

    public TelaMenuPrincipal() {
        super("Catálogo de HQs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null); // Centraliza a janela

        //corrigir o bug do rodape e do menu
        setLayout(new BorderLayout());

        // --- CONSTRUÇÃO DO PAINEL CENTRAL ---
        //Garante que os botões tenham o mesmo tamanho.
        JPanel painelCentral = new JPanel(new GridLayout(4, 1, 10, 10));

        //  Evita de que os botoes fiquem colados nas bordas
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        btnEditora = new JButton();
        btnRevista = new JButton();
        btnEdicao = new JButton();
        btnListagem = new JButton();

        painelCentral.add(btnEditora);
        painelCentral.add(btnRevista);
        painelCentral.add(btnEdicao);
        painelCentral.add(btnListagem);

        add(painelCentral, BorderLayout.CENTER);

        // --- CONSTRUÇÃO DO PAINEL INFERIOR ---
        // FlowLayout(RIGHT): Alinha os componentes à direita
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        labelIdioma = new JLabel();
        painelInferior.add(labelIdioma);

        JButton btnPT = new JButton("PT-BR");
        JButton btnEN = new JButton("EN-US");
        btnSair = new JButton();

        painelInferior.add(btnPT);
        painelInferior.add(btnEN);
        // Cria um espaçamento invisível de 20px antes do botão Sair
        painelInferior.add(Box.createHorizontalStrut(20));
        painelInferior.add(btnSair);

        add(painelInferior, BorderLayout.SOUTH);

        // --- EVENTOS E LISTENERS ---

        // Navegação
        btnEditora.addActionListener(e -> new TelaCadastroEditora());
        btnRevista.addActionListener(e -> new TelaCadastroRevista());
        btnEdicao.addActionListener(e -> new TelaCadastroEdicao());
        btnListagem.addActionListener(e -> new TelaListagemColecao());
        btnSair.addActionListener(e -> System.exit(0));

        // Troca de Idioma
        btnPT.addActionListener(e -> {
            localeAtual = new Locale("pt"); // Define localização para Português
            atualizarTextos(); // Recarrega a interface
        });

        btnEN.addActionListener(e -> {
            localeAtual = new Locale("en"); // Define localização para Inglês
            atualizarTextos(); // Recarrega a interface
        });

        // Chamada inicial para aplicar os textos no idioma padrão
        atualizarTextos();

        setVisible(true);
    }

    /**
     * Método responsável pela Internacionalização (i18n).
     * Carrega o arquivo .properties correspondente ao localeAtual e
     * atualiza o texto de todos os componentes visuais.
     */
    private void atualizarTextos() {
        // Carrega o ResourceBundle (messages_pt.properties ou messages_en.properties)
        ResourceBundle bundle = ResourceBundle.getBundle("messages", localeAtual);

        // Aplica os textos recuperados do arquivo
        setTitle(bundle.getString("menu.titulo"));
        btnEditora.setText(bundle.getString("menu.editoras"));
        btnRevista.setText(bundle.getString("menu.revistas"));
        btnEdicao.setText(bundle.getString("menu.edicoes"));
        btnListagem.setText(bundle.getString("menu.listagem"));
        labelIdioma.setText(bundle.getString("menu.idioma"));
        btnSair.setText(bundle.getString("btn.sair"));
    }
}