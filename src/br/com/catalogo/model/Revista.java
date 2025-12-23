package br.com.catalogo.model;

import java.util.ArrayList;
import java.util.List;

public class Revista {

    private int id_revista; // Lembra que trocamos o nome do ID
    private String nome;
    private int ano_inicio;

    // A tradução do Diagrama de Classes:
    // Uma Revista pertence a UMA Editora
    private Editora editora;

    // E uma Revista tem VÁRIAS Edicoes
    private List<Edicao> edicoes = new ArrayList<>();

    // Construtores
    public Revista() {
    }

    // Getters e Setters
    public int getId_revista() {
        return id_revista;
    }

    public void setId_revista(int id_revista) {
        this.id_revista = id_revista;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAno_inicio() {
        return ano_inicio;
    }

    public void setAno_inicio(int ano_inicio) {
        this.ano_inicio = ano_inicio;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public List<Edicao> getEdicoes() {
        return edicoes;
    }

    public void setEdicoes(List<Edicao> edicoes) {
        this.edicoes = edicoes;
    }
    @Override
    public String toString() {
        return this.nome;
    }
}