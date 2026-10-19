package classes;

import java.awt.Color;
import javax.swing.JLabel;

/** Ícone vetorial compartilhado, também desenhado no editor visual. */
public class RotuloLupa extends JLabel {
    public RotuloLupa() {
        setIcon(new PainelListagem.Lupa());
        setForeground(new Color(153, 153, 153));
    }
}
