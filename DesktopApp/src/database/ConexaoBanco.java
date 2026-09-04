/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author RaphaelBispoIssa
 */
public class ConexaoBanco {

    private static final String URL = "jdbc:mysql://localhost:3306/mainrh?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Coloque sua senha aqui, se houver
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection conexao = null;

    /**
     * Obtém uma conexão ativa com o banco de dados.
     * @return Connection
     */
    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                // Carrega o Driver JDBC
                Class.forName(DRIVER);
                // Estabelece a conexão
                conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Driver do Banco de Dados não encontrado!\n" + e.getMessage(), 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar ao Banco de Dados!\n" + e.getMessage(), 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
        }
        return conexao;
    }

    /**
     * Fecha a conexão se ela estiver aberta.
     */
    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao fechar a conexão!\n" + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}