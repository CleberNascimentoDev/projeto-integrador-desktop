/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.time.LocalDate;

/**
 *
 * @author rapha
 */
public class ProcessoSeletivo {

    private int idProcesso;
    private String nomeProcesso;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int idCargo;
    private Integer idUsuario; // Integer permite valores nulos (já que id_usuario_fk pode ser NULL)

    public ProcessoSeletivo() {
    }

    public ProcessoSeletivo(int idProcesso, String nomeProcesso, LocalDate dataInicio, LocalDate dataFim, int idCargo, Integer idUsuario) {
        this.idProcesso = idProcesso;
        this.nomeProcesso = nomeProcesso;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.idCargo = idCargo;
        this.idUsuario = idUsuario;
    }

    public int getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(int idProcesso) {
        this.idProcesso = idProcesso;
    }

    public String getNomeProcesso() {
        return nomeProcesso;
    }

    public void setNomeProcesso(String nomeProcesso) {
        this.nomeProcesso = nomeProcesso;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    @Override
    public String toString() {
        return this.getNomeProcesso(); 
    }
}