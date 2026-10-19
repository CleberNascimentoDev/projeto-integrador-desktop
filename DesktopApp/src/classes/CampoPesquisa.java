package classes;

import java.awt.*;
import javax.swing.*;

/** Campo de busca com placeholder que não vira conteúdo pesquisável. */
public class CampoPesquisa extends JTextField {
    private String placeholder = "Buscar...";
    public CampoPesquisa() {
        setFont(PainelListagem.fonte(Font.PLAIN, 18));
        setForeground(PainelListagem.cinza);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        atualizarPlaceholder();
    }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String valor) { placeholder = valor; atualizarPlaceholder(); }
    private void atualizarPlaceholder() {
        setUI(new PainelListagem.CampoBusca(placeholder));
        getAccessibleContext().setAccessibleName(placeholder);
        repaint();
    }
}
