package database;

import classes.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroDao {

    public boolean cadastrar(Usuario usuario, String senha) throws SQLException {

        String sql = """
                     INSERT INTO Usuario (
                         email_usu,
                         senha_hash_usu,
                         nome_usu,
                         funcao_usu,
                         cpf_usu,
                         telefone_usu,
                         data_nascimento_usu
                     ) VALUES (?, SHA2(?, 256), ?, ?, ?, ?, ?)
                     """;

        Connection conexao = obterConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, senha);
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getFuncao());
            stmt.setString(5, usuario.getCpf());
            stmt.setString(6, usuario.getTelefone());

            stmt.setDate(
                    7,
                    Date.valueOf(usuario.getDataNascimento())
            );

            return stmt.executeUpdate() == 1;
        }
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