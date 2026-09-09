package database;

import classes.Cargo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CargoDao {

    public boolean cadastrar(Cargo cargo) throws SQLException {
        String sql = """
                     INSERT INTO Cargo (
                         nome_cargo,
                         salario_base_cargo,
                         requisitos_cargo,
                         nivel_cargo,
                         setor_cargo,
                         atividades_cargo
                     ) VALUES (?, ?, ?, ?, ?, ?)
                     """;

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, cargo);

            boolean cadastrou = stmt.executeUpdate() == 1;
            if (cadastrou) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        cargo.setId(chaves.getInt(1));
                    }
                }
            }
            return cadastrou;
        }
    }

    public List<Cargo> listar() throws SQLException {
        return buscarPorNome("");
    }

    public List<Cargo> buscarPorNome(String nome) throws SQLException {
        String sql = """
                     SELECT id_cargo_pk,
                            nome_cargo,
                            salario_base_cargo,
                            requisitos_cargo,
                            nivel_cargo,
                            setor_cargo,
                            atividades_cargo
                     FROM Cargo
                     WHERE nome_cargo LIKE ?
                     ORDER BY nome_cargo
                     """;

        List<Cargo> cargos = new ArrayList<>();
        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + (nome == null ? "" : nome.trim()) + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cargos.add(mapearCargo(rs));
                }
            }
        }
        return cargos;
    }

    public Cargo buscarPorId(int id) throws SQLException {
        String sql = """
                     SELECT id_cargo_pk,
                            nome_cargo,
                            salario_base_cargo,
                            requisitos_cargo,
                            nivel_cargo,
                            setor_cargo,
                            atividades_cargo
                     FROM Cargo
                     WHERE id_cargo_pk = ?
                     """;

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearCargo(rs) : null;
            }
        }
    }

    public boolean editar(Cargo cargo) throws SQLException {
        String sql = """
                     UPDATE Cargo
                     SET nome_cargo = ?,
                         salario_base_cargo = ?,
                         requisitos_cargo = ?,
                         nivel_cargo = ?,
                         setor_cargo = ?,
                         atividades_cargo = ?
                     WHERE id_cargo_pk = ?
                     """;

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherParametros(stmt, cargo);
            stmt.setInt(7, cargo.getId());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM Cargo WHERE id_cargo_pk = ?";

        Connection conexao = obterConexao();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    private Connection obterConexao() throws SQLException {
        Connection conexao = ConexaoBanco.getConexao();
        if (conexao == null) {
            throw new SQLException("Não foi possível estabelecer conexão com o banco de dados.");
        }
        return conexao;
    }

    private void preencherParametros(PreparedStatement stmt, Cargo cargo)
            throws SQLException {
        stmt.setString(1, cargo.getNome());
        stmt.setBigDecimal(2, cargo.getSalarioBase());
        stmt.setString(3, cargo.getRequisitos());
        stmt.setString(4, cargo.getNivel());
        stmt.setString(5, cargo.getSetor());
        stmt.setString(6, cargo.getAtividades());
    }

    private Cargo mapearCargo(ResultSet rs) throws SQLException {
        return new Cargo(
                rs.getInt("id_cargo_pk"),
                rs.getString("nome_cargo"),
                rs.getBigDecimal("salario_base_cargo"),
                rs.getString("requisitos_cargo"),
                rs.getString("nivel_cargo"),
                rs.getString("setor_cargo"),
                rs.getString("atividades_cargo")
        );
    }
}
