package classes;

import java.awt.*;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JPanel;

/** Grade do formulário: duas colunas no Design e uma quando faltar largura. */
public class PainelCampos extends JPanel {
    private final Map<Component,GridBagConstraints> posicoes = new IdentityHashMap<>();
    public PainelCampos() { setOpaque(false); setLayout(new GridBagLayout()); }
    @Override public void doLayout() {
        // No GUI Builder as restrições são controladas pelo editor, inclusive durante o arraste.
        if (!java.beans.Beans.isDesignTime() && getLayout() instanceof GridBagLayout) {
            posicoes.keySet().removeIf(campo -> campo.getParent() != this);
            GridBagLayout grade = (GridBagLayout)getLayout();
            for (Component campo : getComponents()) {
                GridBagConstraints original = posicoes.computeIfAbsent(campo, grade::getConstraints);
                GridBagConstraints atual = (GridBagConstraints)original.clone();
                if (getWidth() > 0 && getWidth() < 650) {
                    atual.gridx = 0; atual.gridy = original.gridy * 2 + original.gridx;
                    atual.insets = new Insets(original.insets.top,0,original.insets.bottom,0);
                }
                grade.setConstraints(campo,atual);
            }
        }
        super.doLayout();
    }
}
