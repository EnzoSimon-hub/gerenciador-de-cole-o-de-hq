package br.com.catalogo.model;

import java.util.ArrayList;
import java.util.List;

public class Editora {

    private int id_editora;
    private String nome;

    // Uma Editora tem uma LISTA de Revistas
    private List<Revista> revistas = new ArrayList<>();

    // Construtores
    public Editora() {
    }

    public Editora(int id_editora, String nome) {
        this.id_editora = id_editora;
        this.nome = nome;
    }

    public Editora(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public int getId_editora() {
        return id_editora;
    }

    public void setId_editora(int id_editora) {
        this.id_editora = id_editora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getters e Setters para a lista (que está vermelha)
    public List<Revista> getRevistas() {
        return revistas;
    }

    public void setRevistas(List<Revista> revistas) {
        this.revistas = revistas;
    }

    // Isso faz o JComboBox mostrar o Nome, e não o endereço de memória
    @Override
    public String toString() {
        return this.nome;
    }
}