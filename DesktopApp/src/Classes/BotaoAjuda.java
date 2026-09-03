package Classes;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BotaoAjuda extends JLabel {

    // Propriedades personalizáveis pelo NetBeans
    private String tituloJanela = "Ajuda";
    private String textoAjuda = "Digite o texto de ajuda aqui...";

    public BotaoAjuda() {
        setText("?"); // O texto visual no JLabel
        setFont(new Font("Arial", Font.BOLD, 30)); // Estilo da fonte
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mãozinha
        
        // Mantém uma dica rápida ao passar o mouse por cima
        setToolTipText("Clique para obter ajuda");

        // Adiciona o ouvinte de clique para abrir o JOptionPane
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirJanelaAjuda();
            }
        });
    }

    private void abrirJanelaAjuda() {
    // 1. Obtém a janela pai genérica (funciona tanto para JFrame quanto para JDialog)
    Window janelaPai = SwingUtilities.getWindowAncestor(this);

    // 2. Cria o JDialog modal atrelado à janela pai correta
    JDialog dialog = new JDialog(janelaPai, tituloJanela);
    dialog.setModal(true);
    dialog.setUndecorated(true); // Remove a barra de título (impede de arrastar/mover)

    // 3. Monta o conteúdo da mensagem
    JPanel painelConteudo = new JPanel(new BorderLayout(10, 10));
    painelConteudo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY, 2), // Borda externa para destacar
        BorderFactory.createEmptyBorder(15, 15, 15, 15)  // Margem interna
    ));

    String textoFormatado = "<html><body style='width: 220px; text-align: center;'>" + textoAjuda + "</body></html>";
    JLabel labelMensagem = new JLabel(textoFormatado);

    // 4. Cria o botão de fechar
    JButton btnFechar = new JButton("OK");
    btnFechar.addActionListener(e -> dialog.dispose());

    JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
    painelBotao.add(btnFechar);

    painelConteudo.add(labelMensagem, BorderLayout.CENTER);
    painelConteudo.add(painelBotao, BorderLayout.SOUTH);

    dialog.add(painelConteudo);
    dialog.pack();

    // 5. Centraliza rigorosamente dentro da janela principal (Frame ou Dialog)
    dialog.setLocationRelativeTo(janelaPai);

    // 6. Exibe a janela
    dialog.setVisible(true);
    }

    // Getters e Setters (aparecem na janela de Propriedades do NetBeans)
    public String getTextoAjuda() {
        return textoAjuda;
    }

    public void setTextoAjuda(String textoAjuda) {
        this.textoAjuda = textoAjuda;
    }

    public String getTituloJanela() {
        return tituloJanela;
    }

    public void setTituloJanela(String tituloJanela) {
        this.tituloJanela = tituloJanela;
    }
}