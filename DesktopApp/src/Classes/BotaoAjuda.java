/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;
import javax.swing.JLabel;
import java.awt.Cursor;
import java.awt.Font;

public class BotaoAjuda extends JLabel {

    // Essa é a propriedade que você vai mudar em cada tela
    private String textoAjuda = "Digite o texto de ajuda aqui...";

    public BotaoAjuda() {
        setText("?"); // O que vai aparecer na tela
        setFont(new Font("Arial", Font.BOLD, 30)); // Estilo da fonte
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Mãozinha ao passar o mouse
        atualizarDica();
    }

    public String getTextoAjuda() {
        return textoAjuda;
    }

    public void setTextoAjuda(String textoAjuda) {
        this.textoAjuda = textoAjuda;
        atualizarDica();
    }

    // Configura o balãozinho para pular linha automaticamente
    private void atualizarDica() {
        setToolTipText("<html><body style='width: 180px; padding: 4px;'>" + textoAjuda + "</body></html>");
    }
}
