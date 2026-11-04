package classes;

import java.awt.*;
import javax.swing.*;

/** Desktop editável no formulário, mantendo os detalhes sobrepostos à listagem. */
public class DesktopListagem extends JDesktopPane {
    public DesktopListagem() { setLayout(new BorderLayout()); }
    @Override public void setLayout(LayoutManager layout) {
        if (layout instanceof BorderLayout) {
            BorderLayout borda = (BorderLayout)layout;
            super.setLayout(new BorderLayout(borda.getHgap(), borda.getVgap()) {
                @Override public void addLayoutComponent(Component campo, Object restricoes) {
                    if (!(campo instanceof JInternalFrame)) super.addLayoutComponent(campo,restricoes);
                }
            });
        } else super.setLayout(layout);
    }
}
