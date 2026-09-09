package database;

import classes.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {

    public Usuario realizarLogin(String email, String senha) throws SQLException {

        String sql = """
                     SELECT 
                         id_usu_pk,
                         email_usu,
                         senha_hash_usu,
                         nome_usu,
                         funcao_usu,
                         cpf_usu,
                         telefone_usu,
                         data_nascimento_usu
                     FROM Usuario   
                     WHERE email_usu = ?
                       AND senha_hash_usu = SHA2(?, 256)
                     """;

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }

        return null;
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

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setIdUsuario(
                rs.getInt("id_usu_pk")
        );

        usuario.setEmail(
                rs.getString("email_usu")
        );

        usuario.setSenhaHash(
                rs.getString("senha_hash_usu")
        );

        usuario.setNome(
                rs.getString("nome_usu")
        );

        usuario.setFuncao(
                rs.getString("funcao_usu")
        );

        usuario.setCpf(
                rs.getString("cpf_usu")
        );

        usuario.setTelefone(
                rs.getString("telefone_usu")
        );

        Date dataNascimento = rs.getDate("data_nascimento_usu");

        if (dataNascimento != null) {
            usuario.setDataNascimento(
                    dataNascimento.toLocalDate()
            );
        }

        return usuario;
    }
}