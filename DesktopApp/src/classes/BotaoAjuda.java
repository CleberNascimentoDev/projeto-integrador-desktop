package classes;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
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
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class BotaoAjuda extends JButton {

    private String tituloJanela = "Ajuda";
    private String textoAjuda = "Digite o texto de ajuda aqui...";

    public BotaoAjuda() {
        super("?"); 
        setFont(new Font("Segoe UI", Font.BOLD, 32));
        setForeground(Color.BLACK); 
        
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("Clique para obter ajuda");
        
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setPreferredSize(new Dimension(40, 40));
        setMargin(new java.awt.Insets(0, 0, 0, 0));
        getAccessibleContext().setAccessibleName("Ajuda");

        addActionListener(e -> abrirJanelaAjuda());
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(Color.DARK_GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Color.BLACK);
            }
        });
    }

    private void abrirJanelaAjuda() {
        criarJanelaAjuda().setVisible(true);
    }

    private JDialog criarJanelaAjuda() {
        Window janelaPai = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(janelaPai, tituloJanela);
        dialog.setModal(true);
        dialog.setUndecorated(true);

        JPanel painelConteudo = new JPanel(new BorderLayout(15, 15));
        painelConteudo.setBackground(Color.WHITE);
        
        // CORRIGIDO: Borda agora utiliza a cor amarela (RGB: 255, 193, 7)
        painelConteudo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 3),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // CORRIGIDO: Instância do ícone ajustada para a cor amarela e renomeada
        Icon iconeAmarelo = new IconeExclamacao(32, Color.WHITE, new Color(255, 193, 7));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        painelTitulo.setOpaque(false);

        JLabel labelIconeEsq = new JLabel(iconeAmarelo);
        JLabel labelIconeDir = new JLabel(iconeAmarelo);
        JLabel labelTexto = new JLabel("ATENÇÃO");

        labelTexto.setFont(new Font("Segoe UI", Font.BOLD, 26));
        // CORRIGIDO: Cor do título alterada para amarelo
        labelTexto.setForeground(new Color(255, 193, 7));

        painelTitulo.add(labelIconeEsq);
        painelTitulo.add(labelTexto);
        painelTitulo.add(labelIconeDir);

        String textoFormatadoHTML = textoAjuda.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        String textoFinal = "<html><body style='text-align: left;'>"
                          + textoFormatadoHTML + "</body></html>";
        JLabel labelMensagem = new TextoAjuda(textoFinal, 600);
        labelMensagem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelMensagem.setForeground(new Color(51, 51, 51));

        JButton btnFechar = new JButton("👍  ENTENDI");
        btnFechar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        
        // CORRIGIDO: Fundo do botão alterado para amarelo
        btnFechar.setBackground(new Color(255, 193, 7));
        
        // MELHORIA DE UI: Texto branco no fundo amarelo causa baixo contraste. Alterado para preto.
        btnFechar.setForeground(Color.BLACK);
        
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
        Acessibilidade.configurarTela(dialog);
        return dialog;
    }

    private static class TextoAjuda extends JLabel {
        private final int larguraTexto;

        TextoAjuda(String texto, int larguraTexto) {
            super(texto);
            this.larguraTexto = larguraTexto;
        }

        @Override public Dimension getPreferredSize() {
            View conteudo = (View) getClientProperty(BasicHTML.propertyKey);
            if (conteudo == null) return super.getPreferredSize();
            int largura = getWidth() > 0 ? getWidth() : larguraTexto;
            conteudo.setSize(largura, 0);
            return new Dimension(largura, (int) Math.ceil(conteudo.getPreferredSpan(View.Y_AXIS)));
        }

        @Override public void setBounds(int x, int y, int largura, int altura) {
            super.setBounds(x, y, largura, altura);
            View conteudo = (View) getClientProperty(BasicHTML.propertyKey);
            if (conteudo != null && largura > 0) conteudo.setSize(largura, altura);
        }
    }

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

            int margem = 3; 
            int[] xPoints = {x + tamanho / 2, x + margem, x + tamanho - margem};
            int[] yPoints = {y + margem, y + tamanho - margem, y + tamanho - margem};

            g2.setColor(Acessibilidade.isDaltonismoAtivo() ? new Color(255, 210, 128) : corSinal);
            g2.fillPolygon(xPoints, yPoints, 3);
            
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolygon(xPoints, yPoints, 3);

            g2.setColor(Acessibilidade.isDaltonismoAtivo() ? Color.BLACK : corFundo);
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(tamanho * 0.55)));
            FontMetrics fm = g2.getFontMetrics();
            
            int textX = x + (tamanho / 2) - (fm.stringWidth("!") / 2);
            int textY = y + (int)(tamanho * 0.75); 
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
