package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestaoDao {

    public List<Object[]> listar(String termoBusca) throws SQLException {

        List<Object[]> lista = new ArrayList<>();

        String sql = """
                     SELECT
                         p.id_proce_pk,
                         p.nome_proce,
                         u.nome_usu AS recrutador
                     FROM Processo_seletivo p
                     LEFT JOIN Usuario u
                         ON u.id_usu_pk = p.id_usuario_fk
                         AND u.funcao_usu = 'RECRUTADOR'
                     """;

        boolean temFiltro =
                termoBusca != null
                && !termoBusca.trim().isEmpty()
                && !termoBusca.equals("Digite aqui...");

        if (temFiltro) {
            sql += " WHERE LOWER(p.nome_proce) LIKE LOWER(?)";
        }

        sql += " ORDER BY p.id_proce_pk DESC";

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (temFiltro) {
                stmt.setString(1, "%" + termoBusca.trim() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    lista.add(new Object[]{
                        rs.getInt("id_proce_pk"),
                        rs.getString("nome_proce"),
                        rs.getString("recrutador")
                    });
                }
            }
        }

        return lista;
    }

    private Connection obterConexao() throws SQLException {

        Connection conexao = ConexaoBanco.getConexao();

        if (conexao == null) {
            throw new SQLException(
                    "Não foi possível estabelecer conexão com o banco de dados."
            );
        }

        return conexao;
    }
}