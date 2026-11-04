package classes;

import java.awt.*;
import javax.swing.JPanel;

/** Painel de conteúdo JavaBean; propriedades visuais disponíveis no NetBeans. */
public class PainelArredondado extends JPanel {
    private Color corBorda = PainelListagem.linha;
    private int raio = 8;
    private Insets margemInterna = new Insets(24, 28, 24, 28);

    public PainelArredondado() {
        setOpaque(false);
        setBackground(Color.WHITE);
        atualizarBorda();
    }
    public Color getCorBorda() { return corBorda; }
    public void setCorBorda(Color cor) { corBorda = cor; atualizarBorda(); }
    public int getRaio() { return raio; }
    public void setRaio(int valor) { raio = Math.max(0, valor); atualizarBorda(); }
    public Insets getMargemInterna() { return (Insets) margemInterna.clone(); }
    public void setMargemInterna(Insets valor) { margemInterna = (Insets) valor.clone(); atualizarBorda(); }
    private void atualizarBorda() {
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new PainelListagem.BordaArredondada(corBorda,raio * 2,0,0),
                javax.swing.BorderFactory.createEmptyBorder(margemInterna.top,margemInterna.left,
                        margemInterna.bottom,margemInterna.right)));
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D desenho = (Graphics2D) g.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        desenho.setColor(getBackground());
        desenho.fillRoundRect(0, 0, getWidth(), getHeight(), raio * 2, raio * 2);
        desenho.dispose();
    }
}
