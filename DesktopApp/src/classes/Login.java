package classes;

import java.time.LocalDate;

public class Login {

    // Atributos
    private int id;
    private String email;
    private String senhaHash;
    private String nome;
    private String funcao;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;

    // Construtor vazio
    public Login() {
    }

    // Construtor completo
    public Login(int id, String email, String senhaHash, String nome,
                 String funcao, String cpf, String telefone,
                 LocalDate dataNascimento) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.funcao = funcao;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }
    
    public Login (String email, String senhaHash) {
        this.email = email;
        this.senhaHash = senhaHash;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}