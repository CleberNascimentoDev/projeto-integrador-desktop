/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import classes.ProcessoSeletivo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 *
 * @author rapha
 */
public class ProcessoSeletivoDao {

    public boolean cadastrar(ProcessoSeletivo processo) throws SQLException {
        String sql = """
                     INSERT INTO Processo_seletivo (
                         nome_proce,
                         data_inicio_proce,
                         data_fim_proce,
                         id_cargo_fk,
                         id_usuario_fk
                     ) VALUES (?, ?, ?, ?, ?)
                     """;

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, processo.getNomeProcesso());
            
            // Converte LocalDate do Java para java.sql.Date do banco
            stmt.setDate(2, processo.getDataInicio() != null ? Date.valueOf(processo.getDataInicio()) : null);
            stmt.setDate(3, processo.getDataFim() != null ? Date.valueOf(processo.getDataFim()) : null);
            
            stmt.setInt(4, processo.getIdCargo());
            
            // Tratamento para id_usuario_fk que aceita nulo
            if (processo.getIdUsuario() != null) {
                stmt.setInt(5, processo.getIdUsuario());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            boolean cadastrou = stmt.executeUpdate() == 1;
            
            if (cadastrou) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        processo.setIdProcesso(chaves.getInt(1));
                    }
                }
            }
            
            return cadastrou;
        }
    }

    private Connection obterConexao() throws SQLException {
        Connection conexao = ConexaoBanco.getConexao();
        if (conexao == null) {
            throw new SQLException("Não foi possível estabelecer conexão com o banco de dados.");
        }
        return conexao;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}