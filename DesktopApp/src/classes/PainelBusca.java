package classes;

import java.awt.*;

/** Pesquisa flexível dentro da barra; a estrutura interna é definida no .form. */
public class PainelBusca extends PainelArredondado {
    public PainelBusca() { setCorBorda(new Color(153,153,153)); setMargemInterna(new Insets(0,16,0,16)); }
    @Override public Dimension getPreferredSize() {
        Dimension preferencia = super.getPreferredSize();
        if (getParent() instanceof PainelFerramentas && getParent().getLayout() instanceof FlowLayout) {
            PainelFerramentas barra = (PainelFerramentas)getParent();
            int acoes = 0, altura = Math.max(56,preferencia.height), espaco = ((FlowLayout)barra.getLayout()).getHgap();
            for (Component c : barra.getComponents()) if (c != this && c.isVisible()) {
                acoes += c.getPreferredSize().width + espaco;
                altura = Math.max(altura,c.getPreferredSize().height);
            }
            int largura = barra.larguraDisponivel() - acoes - espaco * 2;
            if (largura < 240) largura = barra.larguraDisponivel() - espaco * 2;
            return new Dimension(Math.max(1,largura), altura);
        }
        return new Dimension(Math.max(240,preferencia.width),Math.max(56,preferencia.height));
    }
}
