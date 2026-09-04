package Classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BotaoAjuda extends JLabel {

    private String tituloJanela = "Ajuda";
    private String textoAjuda = "Digite o texto de ajuda aqui...";

    public BotaoAjuda() {
        setText("?");
        setFont(new Font("Arial", Font.BOLD, 30));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("Clique para obter ajuda");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirJanelaAjuda();
            }
        });
    }

    private void abrirJanelaAjuda() {
        Window janelaPai = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(janelaPai, tituloJanela);
        dialog.setModal(true);
        dialog.setUndecorated(true);

        JPanel painelConteudo = new JPanel(new BorderLayout(15, 15));
        painelConteudo.setBackground(Color.WHITE);
        painelConteudo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 3),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Ícone customizado de triângulo vermelho com exclamação branca
        Icon iconeVermelho = new IconeExclamacao(28, Color.WHITE, new Color(220, 53, 69));

        // Painel centralizado contendo: [Ícone] [ATENÇÃO!] [Ícone]
        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        painelTitulo.setOpaque(false);

        JLabel labelIconeEsq = new JLabel(iconeVermelho);
        JLabel labelIconeDir = new JLabel(iconeVermelho);
        JLabel labelTexto = new JLabel("ATENÇÃO");

        labelTexto.setFont(new Font("Arial", Font.BOLD, 26));
        labelTexto.setForeground(new Color(220, 53, 69));

        painelTitulo.add(labelIconeEsq);
        painelTitulo.add(labelTexto);
        painelTitulo.add(labelIconeDir);

        // Formata o negrito no HTML do Swing
        String textoFormatadoHTML = textoAjuda.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        String textoFinal = "<html><body style='width: 420px; text-align: justify; font-size: 14pt; font-family: Arial; color: #333333;'>" 
                          + textoFormatadoHTML + "</body></html>";
        JLabel labelMensagem = new JLabel(textoFinal);

        // Botão ENTENDI com Joinha (👍)
        JButton btnFechar = new JButton("👍  ENTENDI");
        btnFechar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btnFechar.setBackground(new Color(220, 53, 69));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setPreferredSize(new Dimension(210, 50));
        btnFechar.setFocusPainted(false);
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFechar.addActionListener(e -> dialog.dispose());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setOpaque(false);
        painelBotao.add(btnFechar);

        painelConteudo.add(painelTitulo, BorderLayout.NORTH);
        painelConteudo.add(labelMensagem, BorderLayout.CENTER);
        painelConteudo.add(painelBotao, BorderLayout.SOUTH);

        dialog.add(painelConteudo);
        dialog.pack();
        dialog.setLocationRelativeTo(janelaPai);
        dialog.setVisible(true);
    }

    // Classe gráfica do triângulo de alerta preenchido em vermelho
    private static class IconeExclamacao implements Icon {
        private final int tamanho;
        private final Color corFundo;
        private final Color corSinal;

        public IconeExclamacao(int tamanho, Color corFundo, Color corSinal) {
            this.tamanho = tamanho;
            this.corFundo = corFundo;
            this.corSinal = corSinal;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int[] xPoints = {x + tamanho / 2, x, x + tamanho};
            int[] yPoints = {y, y + tamanho, y + tamanho};

            // Preenche o triângulo vermelho
            g2.setColor(corSinal);
            g2.fillPolygon(xPoints, yPoints, 3);

            // Escreve a exclamação branca
            g2.setColor(corFundo);
            g2.setFont(new Font("Arial", Font.BOLD, (int)(tamanho * 0.65)));
            
            int textX = x + (tamanho / 2) - (g2.getFontMetrics().stringWidth("!") / 2);
            int textY = y + (int)(tamanho * 0.82);
            g2.drawString("!", textX, textY);

            g2.dispose();
        }

        @Override public int getIconWidth() { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }
    }

    public String getTextoAjuda() { return textoAjuda; }
    public void setTextoAjuda(String textoAjuda) { this.textoAjuda = textoAjuda; }
    public String getTituloJanela() { return tituloJanela; }
    public void setTituloJanela(String tituloJanela) { this.tituloJanela = tituloJanela; }
}