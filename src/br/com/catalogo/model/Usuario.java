package br.com.catalogo.model;

public class Usuario {

    private int id_usuario;
    private String login;
    private String senha;

    public Usuario() {
    }

    // pra consulta
    public Usuario(int id_usuario, String login, String senha) {
        this.id_usuario = id_usuario;
        this.login = login;
        this.senha = senha;
    }

    // criari um novo usuario, o banco gera o id automaticamente
    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    // --- Getters e Setters ---

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }



    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}