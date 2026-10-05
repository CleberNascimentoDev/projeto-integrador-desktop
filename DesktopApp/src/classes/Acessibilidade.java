package classes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Preferência compartilhada pelas telas durante a execução do sistema. */
public final class Acessibilidade {
    private static boolean tecladoAtivo = true;
    private static boolean baixaVisaoAtiva = false;
    private static final java.util.prefs.Preferences preferencias = abrirPreferencias();
    private static ModoDaltonismo modoDaltonismo = carregarModoDaltonismo();
    private static final PropertyChangeSupport eventos = new PropertyChangeSupport(Acessibilidade.class);

    private Acessibilidade() { }

    public static boolean isTecladoAtivo() { return tecladoAtivo; }

    public static boolean isBaixaVisaoAtiva() { return baixaVisaoAtiva; }

    public static boolean isDaltonismoAtivo() { return modoDaltonismo != ModoDaltonismo.PADRAO; }

    public static ModoDaltonismo getModoDaltonismo() { return modoDaltonismo; }

    public static PaletaAcessibilidade getPaleta() { return PaletaAcessibilidade.paraModo(modoDaltonismo); }

    private static java.util.prefs.Preferences abrirPreferencias() {
        try {
            return java.util.prefs.Preferences.userRoot().node(
                    System.getProperty("mainrh.preferencias", "/mainrh/acessibilidade"));
        } catch (SecurityException e) {
            java.util.logging.Logger.getLogger(Acessibilidade.class.getName()).log(
                    java.util.logging.Level.WARNING, "Preferências indisponíveis; o modo funciona nesta sessão", e);
            return null;
        }
    }

    private static ModoDaltonismo carregarModoDaltonismo() {
        if (preferencias == null) return ModoDaltonismo.PADRAO;
        try { return ModoDaltonismo.valueOf(preferencias.get("modoDaltonismo", "PADRAO")); }
        catch (IllegalArgumentException | SecurityException e) { return ModoDaltonismo.PADRAO; }
    }

    public static void setBaixaVisaoAtiva(boolean ativo) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setBaixaVisaoAtiva(ativo));
            return;
        }
        boolean anterior = baixaVisaoAtiva;
        Daltonismo.restaurarTelas();
        BaixaVisao.restaurarTelas();
        baixaVisaoAtiva = ativo;
        BaixaVisao.atualizarTelas();
        Daltonismo.atualizarTelas();
        eventos.firePropertyChange("baixaVisaoAtiva", anterior, ativo);
    }

    public static void configurarTela(javax.swing.RootPaneContainer tela) {
        BaixaVisao.configurarTela(tela);
        Daltonismo.configurarTela(tela);
    }

    public static void setDaltonismoAtivo(boolean ativo) {
        setModoDaltonismo(ativo ? ModoDaltonismo.PROTANOPIA : ModoDaltonismo.PADRAO);
    }

    public static void setModoDaltonismo(ModoDaltonismo modo) {
        java.util.Objects.requireNonNull(modo, "Modo de daltonismo obrigatório");
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setModoDaltonismo(modo));
            return;
        }
        ModoDaltonismo anterior = modoDaltonismo;
        Daltonismo.restaurarTelas();
        BaixaVisao.restaurarTelas();
        modoDaltonismo = modo;
        Daltonismo.atualizarPadroesSwing();
        BaixaVisao.atualizarTelas();
        Daltonismo.atualizarTelas();
        try {
            if (preferencias != null) {
                preferencias.put("modoDaltonismo", modo.name());
                preferencias.flush();
            }
        } catch (java.util.prefs.BackingStoreException | SecurityException e) {
            java.util.logging.Logger.getLogger(Acessibilidade.class.getName()).log(
                    java.util.logging.Level.WARNING, "Não foi possível salvar o modo de daltonismo", e);
        }
        eventos.firePropertyChange("modoDaltonismo", anterior, modo);
        eventos.firePropertyChange("daltonismoAtivo", anterior != ModoDaltonismo.PADRAO, isDaltonismoAtivo());
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
