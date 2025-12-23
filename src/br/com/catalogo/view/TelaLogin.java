package br.com.catalogo.view;

import br.com.catalogo.dao.UsuarioDAO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TelaLogin extends JFrame {

    // Componentes de Interface Gráfica
    private JLabel labelLogin;
    private JLabel labelSenha;
    private JTextField campoLogin;
    private JPasswordField campoSenha; // Componente específico para ocultar caracteres de senha
    private JButton botaoEntrar;

    public TelaLogin() {
        super("Acesso ao Sistema de Catálogo de HQs");

        // Configurações da Janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o processo ao clicar no X
        setSize(350, 250);
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(false); // Impede redimensionamento
        setLayout(null); // Layout absoluto (posicionamento manual via setBounds)

        adicionarComponentes();

        setVisible(true);
    }


    private void adicionarComponentes() {

        // --- Configuração dos Labels e Campos de Texto ---
        labelLogin = new JLabel("Login:");
        labelLogin.setBounds(50, 30, 80, 25);
        add(labelLogin);

        campoLogin = new JTextField();
        campoLogin.setBounds(130, 30, 150, 25);
        add(campoLogin);

        labelSenha = new JLabel("Senha:");
        labelSenha.setBounds(50, 70, 80, 25);
        add(labelSenha);

        // JPasswordField: Mostra bolinhas ou asteriscos em vez do texto real
        campoSenha = new JPasswordField();
        campoSenha.setBounds(130, 70, 150, 25);
        add(campoSenha);

        // --- Configuração do Botão ---
        botaoEntrar = new JButton("Entrar");
        botaoEntrar.setBounds(125, 140, 100, 30);
        add(botaoEntrar);

        // --- LÓGICA DE AUTENTICAÇÃO ---
        // Adiciona um ouvinte para o evento de clique
        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Captura dos dados inseridos
                String login = campoLogin.getText();

                // Conversão de char[] para String (segurança padrão do Swing)
                String senha = new String(campoSenha.getPassword());

                // 2. Validação junto ao Banco de Dados (Via DAO)
                UsuarioDAO dao = new UsuarioDAO();
                boolean logado = dao.verificarLogin(login, senha);

                // 3. Tomada de Decisão
                if (logado) {
                    // SUCESSO: Instancia a tela principal e fecha a tela de login
                    new TelaMenuPrincipal();
                    dispose(); // Libera os recursos desta janela e a fecha
                } else {
                    // FALHA: Exibe feedback visual ao usuário
                    JOptionPane.showMessageDialog(TelaLogin.this,
                            "Login ou senha incorretos! Tente novamente.",
                            "Erro de Acesso",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        new TelaLogin();
    }
}