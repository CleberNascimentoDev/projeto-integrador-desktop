package classes;

import java.time.LocalDate;

public class Usuario {

    private int idUsuario;
    private String email;
    private String senhaHash;
    private String nome;
    private String funcao;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;

    public Usuario() {
    }

    public Usuario(
            int idUsuario,
            String email,
            String senhaHash,
            String nome,
            String funcao,
            String cpf,
            String telefone,
            LocalDate dataNascimento) {

        this.idUsuario = idUsuario;
        this.email = email;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.funcao = funcao;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return this.getNome();
    }
}