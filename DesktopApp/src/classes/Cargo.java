/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.math.BigDecimal;

/**
 *
 * @author RaphaelBispoIssa
 */
public class Cargo {

    private int id;
    private String nome;
    private BigDecimal salarioBase;
    private String requisitos;
    private String nivel;
    private String setor;
    private String atividades;

    public Cargo() {
    }

    public Cargo(String nome, BigDecimal salarioBase, String requisitos,
                 String nivel, String setor, String atividades) {
        this.nome = nome;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.nivel = nivel;
        this.setor = setor;
        this.atividades = atividades;
    }

    public Cargo(int id, String nome, BigDecimal salarioBase, String requisitos,
                 String nivel, String setor, String atividades) {
        this(nome, salarioBase, requisitos, nivel, setor, atividades);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getAtividades() {
        return atividades;
    }

    public void setAtividades(String atividades) {
        this.atividades = atividades;
    }

    @Override
    public String toString() {
        return nome;
    }
}
