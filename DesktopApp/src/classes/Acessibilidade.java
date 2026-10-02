package classes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Preferência compartilhada pelas telas durante a execução do sistema. */
public final class Acessibilidade {
    private static boolean tecladoAtivo = true;
    private static final PropertyChangeSupport eventos = new PropertyChangeSupport(Acessibilidade.class);

    private Acessibilidade() { }

    public static boolean isTecladoAtivo() { return tecladoAtivo; }

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
