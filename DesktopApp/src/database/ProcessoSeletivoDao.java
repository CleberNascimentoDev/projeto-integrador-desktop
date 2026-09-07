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
import java.util.ArrayList;
import java.util.List;

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
    
    
    
    public List<ProcessoSeletivo> listar(String termoBusca) throws SQLException {
    List<ProcessoSeletivo> lista = new ArrayList<>();
    
    String sql = """
                 SELECT id_proce_pk, nome_proce, data_inicio_proce, data_fim_proce, id_cargo_fk, id_usuario_fk
                 FROM Processo_seletivo
                 """;

    // Adiciona o filtro caso o usuário tenha digitado algo na busca
    if (termoBusca != null && !termoBusca.trim().isEmpty() && !termoBusca.equals("Digite aqui...")) {
        sql += " WHERE LOWER(nome_proce) LIKE LOWER(?)";
    }

    sql += " ORDER BY id_proce_pk DESC";

    Connection conexao = obterConexao();
    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
        if (termoBusca != null && !termoBusca.trim().isEmpty() && !termoBusca.equals("Digite aqui...")) {
            stmt.setString(1, "%" + termoBusca.trim() + "%");
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ProcessoSeletivo p = new ProcessoSeletivo();
                p.setIdProcesso(rs.getInt("id_proce_pk"));
                p.setNomeProcesso(rs.getString("nome_proce"));
                
                // Conversão de java.sql.Date para LocalDate
                Date dtInicio = rs.getDate("data_inicio_proce");
                if (dtInicio != null) p.setDataInicio(dtInicio.toLocalDate());

                Date dtFim = rs.getDate("data_fim_proce");
                if (dtFim != null) p.setDataFim(dtFim.toLocalDate());

                p.setIdCargo(rs.getInt("id_cargo_fk"));
                p.setIdUsuario(rs.getInt("id_usuario_fk"));

                lista.add(p);
            }
        }
    }
    return lista;
}
    
    
    
    
    
    
    
    
    
    
    
}