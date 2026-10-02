package classes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Preferência compartilhada pelas telas durante a execução do sistema. */
public final class Acessibilidade {
    private static boolean tecladoAtivo = true;
    private static boolean daltonismoAtivo = false;
    private static final PropertyChangeSupport eventos = new PropertyChangeSupport(Acessibilidade.class);

    private Acessibilidade() { }

    public static boolean isTecladoAtivo() { return tecladoAtivo; }

    public static boolean isDaltonismoAtivo() { return daltonismoAtivo; }

    public static void setDaltonismoAtivo(boolean ativo) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setDaltonismoAtivo(ativo));
            return;
        }
        boolean anterior = daltonismoAtivo;
        daltonismoAtivo = ativo;
        Daltonismo.atualizarTelas();
        eventos.firePropertyChange("daltonismoAtivo", anterior, ativo);
    }

    public static void configurarTela(javax.swing.RootPaneContainer tela) {
        Daltonismo.configurarTela(tela);
    }

    public static void setTecladoAtivo(boolean ativo) {
        boolean anterior = tecladoAtivo;
        tecladoAtivo = ativo;
        eventos.firePropertyChange("tecladoAtivo", anterior, ativo);
    }

    public static void adicionarListener(PropertyChangeListener listener) {
        eventos.addPropertyChangeListener(listener);
    }

    public static void removerListener(PropertyChangeListener listener) {
        eventos.removePropertyChangeListener(listener);
    }
}
