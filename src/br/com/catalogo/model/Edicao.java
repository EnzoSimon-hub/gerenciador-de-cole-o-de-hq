package br.com.catalogo.model;

import java.util.Date; // Precisamos importar o tipo Date

public class Edicao {

    private int id_edicao;
    private int numero;
    private Date data_publicacao; // Campo do tipo Data

    // A tradução do Diagrama de Classes:
    // Uma Edição pertence a UMA Revista
    private Revista revista;

    // Construtores
    public Edicao() {
    }

    // Getters e Setters
    public int getId_edicao() {
        return id_edicao;
    }

    public void setId_edicao(int id_edicao) {
        this.id_edicao = id_edicao;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getData_publicacao() {
        return data_publicacao;
    }

    public void setData_publicacao(Date data_publicacao) {
        this.data_publicacao = data_publicacao;
    }

    public Revista getRevista() {
        return revista;
    }

    public void setRevista(Revista revista) {
        this.revista = revista;
    }

}