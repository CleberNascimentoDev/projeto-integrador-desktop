package database;

import classes.ProcessoSeletivo;
import classes.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestaoDao {

    // Lista os processos seletivos e o recrutador alocado
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
                stmt.setString(
                        1,
                        "%" + termoBusca.trim() + "%"
                );
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


    // Remove o recrutador atribuído ao processo
    public boolean excluirRecrutador(int idProcesso) throws SQLException {

        String sql = """
                     UPDATE Processo_seletivo
                     SET id_usuario_fk = NULL
                     WHERE id_proce_pk = ?
                     """;

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idProcesso);

            return stmt.executeUpdate() > 0;
        }
    }


    // Lista apenas os usuários que possuem função RECRUTADOR
    public List<Usuario> listarRecrutadores(String termoBusca)
            throws SQLException {

        List<Usuario> lista = new ArrayList<>();

        String sql = """
                     SELECT
                         id_usu_pk,
                         nome_usu,
                         email_usu,
                         funcao_usu
                     FROM Usuario
                     WHERE funcao_usu = 'RECRUTADOR'
                     """;

        boolean temFiltro =
                termoBusca != null
                && !termoBusca.trim().isEmpty()
                && !termoBusca.equals("Digite aqui...");

        if (temFiltro) {

            sql += """
                    AND (
                        LOWER(nome_usu) LIKE LOWER(?)
                        OR LOWER(email_usu) LIKE LOWER(?)
                    )
                   """;
        }

        sql += " ORDER BY nome_usu";

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (temFiltro) {

                String busca =
                        "%" + termoBusca.trim() + "%";

                stmt.setString(1, busca);
                stmt.setString(2, busca);
            }

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario(
                            rs.getInt("id_usu_pk")
                    );

                    usuario.setNome(
                            rs.getString("nome_usu")
                    );

                    usuario.setEmail(
                            rs.getString("email_usu")
                    );

                    usuario.setFuncao(
                            rs.getString("funcao_usu")
                    );

                    lista.add(usuario);
                }
            }
        }

        return lista;
    }


    // Busca um processo seletivo pelo ID
    public ProcessoSeletivo buscarProcessoPorId(int idProcesso)
            throws SQLException {

        String sql = """
                     SELECT
                         id_proce_pk,
                         nome_proce,
                         data_inicio_proce,
                         data_fim_proce,
                         id_cargo_fk,
                         id_usuario_fk
                     FROM Processo_seletivo
                     WHERE id_proce_pk = ?
                     """;

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idProcesso);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    ProcessoSeletivo processo =
                            new ProcessoSeletivo();

                    processo.setIdProcesso(
                            rs.getInt("id_proce_pk")
                    );

                    processo.setNomeProcesso(
                            rs.getString("nome_proce")
                    );

                    java.sql.Date dataInicio =
                            rs.getDate("data_inicio_proce");

                    if (dataInicio != null) {
                        processo.setDataInicio(
                                dataInicio.toLocalDate()
                        );
                    }

                    java.sql.Date dataFim =
                            rs.getDate("data_fim_proce");

                    if (dataFim != null) {
                        processo.setDataFim(
                                dataFim.toLocalDate()
                        );
                    }

                    processo.setIdCargo(
                            rs.getInt("id_cargo_fk")
                    );

                    int idUsuario =
                            rs.getInt("id_usuario_fk");

                    processo.setIdUsuario(
                            rs.wasNull()
                                    ? null
                                    : idUsuario
                    );

                    return processo;
                }
            }
        }

        return null;
    }


    // Atribui ou troca o recrutador de um processo
    public boolean atribuirRecrutador(
            int idProcesso,
            int idUsuario) throws SQLException {

        String sql = """
                     UPDATE Processo_seletivo
                     SET id_usuario_fk = ?
                     WHERE id_proce_pk = ?
                     """;

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idProcesso);

            return stmt.executeUpdate() > 0;
        }
    }


    // Obtém uma conexão válida com o banco
    private Connection obterConexao() throws SQLException {

        Connection conexao =
                ConexaoBanco.getConexao();

        if (conexao == null) {

            throw new SQLException(
                    "Não foi possível estabelecer conexão com o banco de dados."
            );
        }

        return conexao;
    }
}