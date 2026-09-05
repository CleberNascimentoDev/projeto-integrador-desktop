package database;

import classes.Login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class LoginDao {

    public boolean realizarLogin(Login login) {

        String sql = """
                     SELECT id_usu_pk,
                            email_usu,
                            senha_hash_usu,
                            nome_usu,
                            `função_usu`
                     FROM Usuario
                     WHERE email_usu = ?
                     AND senha_hash_usu = ?
                     AND `função_usu` IN ('ADM', 'RECRUTADOR')
                     """;

        Connection conexao = ConexaoBanco.getConexao();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Dados informados na tela de login
            stmt.setString(1, login.getEmail());
            stmt.setString(2, login.getSenhaHash());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    // Guarda os dados do usuário que entrou no sistema
                    login.setId(rs.getInt("id_usu_pk"));
                    login.setNome(rs.getString("nome_usu"));
                    login.setFuncao(rs.getString("função_usu"));

                    return true;
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao realizar login!\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return false;
    }
}