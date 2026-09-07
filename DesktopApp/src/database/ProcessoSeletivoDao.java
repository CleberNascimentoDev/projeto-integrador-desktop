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
            preencherParametros(stmt, processo);

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

    public List<ProcessoSeletivo> listar() throws SQLException {
        return listar("");
    }

    public List<ProcessoSeletivo> listar(String termoBusca) throws SQLException {
        List<ProcessoSeletivo> lista = new ArrayList<>();
        
        String sql = """
                     SELECT id_proce_pk, nome_proce, data_inicio_proce, data_fim_proce, id_cargo_fk, id_usuario_fk
                     FROM Processo_seletivo
                     """;

        boolean temFiltro = termoBusca != null && !termoBusca.trim().isEmpty() && !termoBusca.equals("Digite aqui...");

        if (temFiltro) {
            sql += " WHERE LOWER(nome_proce) LIKE LOWER(?)";
        }

        sql += " ORDER BY id_proce_pk DESC";

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (temFiltro) {
                stmt.setString(1, "%" + termoBusca.trim() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProcesso(rs));
                }
            }
        }
        return lista;
    }

    public boolean atualizar(ProcessoSeletivo processo) throws SQLException {
        String sql = """
                     UPDATE Processo_seletivo 
                     SET nome_proce = ?, 
                         data_inicio_proce = ?, 
                         data_fim_proce = ?, 
                         id_cargo_fk = ?,
                         id_usuario_fk = ?
                     WHERE id_proce_pk = ?
                     """;

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherParametros(stmt, processo);
            stmt.setInt(6, processo.getIdProcesso());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean excluir(int idProcesso) throws SQLException {
        String sql = "DELETE FROM Processo_seletivo WHERE id_proce_pk = ?";

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idProcesso);
            return stmt.executeUpdate() > 0;
        }
    }

    private Connection obterConexao() throws SQLException {
        Connection conexao = ConexaoBanco.getConexao();
        if (conexao == null) {
            throw new SQLException("Não foi possível estabelecer conexão com o banco de dados.");
        }
        return conexao;
    }

    private void preencherParametros(PreparedStatement stmt, ProcessoSeletivo processo) throws SQLException {
        stmt.setString(1, processo.getNomeProcesso());
        stmt.setDate(2, processo.getDataInicio() != null ? Date.valueOf(processo.getDataInicio()) : null);
        stmt.setDate(3, processo.getDataFim() != null ? Date.valueOf(processo.getDataFim()) : null);
        stmt.setInt(4, processo.getIdCargo());

        if (processo.getIdUsuario() != null) {
            stmt.setInt(5, processo.getIdUsuario());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }
    }

    private ProcessoSeletivo mapearProcesso(ResultSet rs) throws SQLException {
        ProcessoSeletivo p = new ProcessoSeletivo();
        p.setIdProcesso(rs.getInt("id_proce_pk"));
        p.setNomeProcesso(rs.getString("nome_proce"));

        Date dtInicio = rs.getDate("data_inicio_proce");
        if (dtInicio != null) p.setDataInicio(dtInicio.toLocalDate());

        Date dtFim = rs.getDate("data_fim_proce");
        if (dtFim != null) p.setDataFim(dtFim.toLocalDate());

        p.setIdCargo(rs.getInt("id_cargo_fk"));

        int idUsuario = rs.getInt("id_usuario_fk");
        p.setIdUsuario(rs.wasNull() ? null : idUsuario);

        return p;
    }
}
