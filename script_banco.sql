-- 1. Criar o Banco de Dados
CREATE DATABASE catalogo_hqs;
USE catalogo_hqs;

-- 2. Tabela Usuario (Para o Login)
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

-- 3. Tabela Editora (Cadastro Básico)
CREATE TABLE editora (
    id_editora INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Tabela Revista (Cadastro Intermediário - Liga com Editora)
CREATE TABLE revista (
    id_revista INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    ano_inicio INT,
    id_editora INT NOT NULL,
    FOREIGN KEY (id_editora) REFERENCES editora(id_editora)
);

-- 5. Tabela Edicao (Cadastro Intermediário - Liga com Revista)
CREATE TABLE edicao (
    id_edicao INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL,
    data_publicacao DATE,
    id_revista INT NOT NULL,
    FOREIGN KEY (id_revista) REFERENCES revista(id_revista)
);

-- 6. Inserir um usuário de teste para você conseguir logar depois
INSERT INTO usuario (login, senha) VALUES ('admin', '1234');