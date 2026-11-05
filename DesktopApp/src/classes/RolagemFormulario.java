package classes;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

/** Rolagem apenas dos campos; cabeçalho e ações permanecem fora dela. */
public class RolagemFormulario extends JScrollPane {
    public RolagemFormulario() {
        setBorder(BorderFactory.createEmptyBorder());
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setMinimumSize(new Dimension(0, 96));
        getVerticalScrollBar().setUnitIncrement(24);
        getViewport().setOpaque(false);
        setOpaque(false);
    }

    @Override public void setViewportView(Component campos) {
        super.setViewportView(campos);
        acompanharFoco(campos);
    }

    private void acompanharFoco(Component campo) {
        if (campo == null) return;
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent evento) {
                SwingUtilities.invokeLater(() -> {
                    Component vista = getViewport().getView();
                    if (!(vista instanceof JComponent) || !SwingUtilities.isDescendingFrom(campo, vista)) return;
                    Rectangle area = SwingUtilities.convertRectangle(campo,
                            new Rectangle(0, 0, campo.getWidth(), campo.getHeight()), vista);
                    area.grow(0, 8);
                    ((JComponent) vista).scrollRectToVisible(area);
                });
            }
        });
        if (campo instanceof Container) for (Component filho : ((Container) campo).getComponents())
            acompanharFoco(filho);
    }
}
