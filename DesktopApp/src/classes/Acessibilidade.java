package classes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

/** Coordena as preferências e restaura as camadas visuais antes de aplicar outro modo. */
public final class Acessibilidade {
    private static boolean tecladoAtivo = true;
    private static boolean baixaVisaoAtiva;
    private static final java.util.prefs.Preferences preferencias = abrirPreferencias();
    private static boolean altoContrasteAtivo = carregarAltoContraste();
    private static ModoDaltonismo modoDaltonismo = altoContrasteAtivo
            ? ModoDaltonismo.PADRAO : carregarModoDaltonismo();
    private static boolean restaurando;
    private static final PropertyChangeSupport eventos = new PropertyChangeSupport(Acessibilidade.class);

    private Acessibilidade() { }
    public static boolean isTecladoAtivo() { return tecladoAtivo; }
    public static boolean isBaixaVisaoAtiva() { return baixaVisaoAtiva; }
    public static boolean isDaltonismoAtivo() { return modoDaltonismo != ModoDaltonismo.PADRAO; }
    public static ModoDaltonismo getModoDaltonismo() { return modoDaltonismo; }
    public static boolean isAltoContrasteAtivo() { return altoContrasteAtivo; }
    public static boolean isPaletaAdaptada() { return altoContrasteAtivo || isDaltonismoAtivo(); }
    static boolean isRestaurando() { return restaurando; }
    /** Uma janela nova pode receber a paleta antes do evento que aplica a ampliação. */
    static void prepararAmpliacao(javax.swing.JRootPane raiz) {
        boolean anterior = restaurando;
        restaurando = true;
        try { Daltonismo.restaurarTela(raiz); }
        finally { restaurando = anterior; }
    }
    public static PaletaAcessibilidade getPaleta() {
        return altoContrasteAtivo ? PaletaAcessibilidade.altoContraste() : PaletaAcessibilidade.paraModo(modoDaltonismo);
    }
    private static java.util.prefs.Preferences abrirPreferencias() {
        try {
            return java.util.prefs.Preferences.userRoot().node(
                    System.getProperty("mainrh.preferencias", "/mainrh/acessibilidade"));
        } catch (SecurityException e) { return null; }
    }
    private static boolean carregarAltoContraste() {
        try { return preferencias != null && preferencias.getBoolean("altoContrasteAtivo", false); }
        catch (SecurityException e) { return false; }
    }
    private static ModoDaltonismo carregarModoDaltonismo() {
        try { return preferencias == null ? ModoDaltonismo.PADRAO
                : ModoDaltonismo.valueOf(preferencias.get("modoDaltonismo", "PADRAO")); }
        catch (IllegalArgumentException | SecurityException e) { return ModoDaltonismo.PADRAO; }
    }
    public static void setBaixaVisaoAtiva(boolean ativo) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setBaixaVisaoAtiva(ativo)); return;
        }
        aplicarPreferencias(ativo, altoContrasteAtivo, modoDaltonismo);
    }
    public static void setAltoContrasteAtivo(boolean ativo) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setAltoContrasteAtivo(ativo)); return;
        }
        aplicarPreferencias(baixaVisaoAtiva, ativo, ativo ? ModoDaltonismo.PADRAO : modoDaltonismo);
    }
    public static void setDaltonismoAtivo(boolean ativo) {
        setModoDaltonismo(ativo ? ModoDaltonismo.PROTANOPIA : ModoDaltonismo.PADRAO);
    }
    public static void setModoDaltonismo(ModoDaltonismo modo) {
        java.util.Objects.requireNonNull(modo, "Modo de daltonismo obrigatório");
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setModoDaltonismo(modo)); return;
        }
        aplicarPreferencias(baixaVisaoAtiva, modo == ModoDaltonismo.PADRAO && altoContrasteAtivo, modo);
    }
    /** Restaura o design original, sem alterar dados ou navegação. */
    public static void restaurarPadrao() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(Acessibilidade::restaurarPadrao); return;
        }
        aplicarPreferencias(false, false, ModoDaltonismo.PADRAO);
    }
    private static void aplicarPreferencias(boolean baixa, boolean contraste, ModoDaltonismo modo) {
        boolean baixaAnterior = baixaVisaoAtiva, contrasteAnterior = altoContrasteAtivo;
        ModoDaltonismo modoAnterior = modoDaltonismo;
        // Impede que listeners recoloquem a paleta antiga durante a restauração.
        restaurando = true;
        try {
            Daltonismo.restaurarTelas();
            BaixaVisao.restaurarTelas();
        } finally { restaurando = false; }
        baixaVisaoAtiva = baixa;
        altoContrasteAtivo = contraste;
        modoDaltonismo = modo;
        Daltonismo.atualizarPadroesSwing();
        BaixaVisao.atualizarTelas();
        Daltonismo.atualizarTelas();
        try {
            if (preferencias != null) {
                preferencias.putBoolean("altoContrasteAtivo", contraste);
                preferencias.put("modoDaltonismo", modo.name());
                preferencias.flush();
            }
        } catch (java.util.prefs.BackingStoreException | SecurityException e) {
            java.util.logging.Logger.getLogger(Acessibilidade.class.getName()).log(
                    java.util.logging.Level.WARNING, "Não foi possível salvar a acessibilidade", e);
        }
        eventos.firePropertyChange("baixaVisaoAtiva", baixaAnterior, baixa);
        eventos.firePropertyChange("altoContrasteAtivo", contrasteAnterior, contraste);
        eventos.firePropertyChange("modoDaltonismo", modoAnterior, modo);
        eventos.firePropertyChange("daltonismoAtivo", modoAnterior != ModoDaltonismo.PADRAO, isDaltonismoAtivo());
    }
    public static void configurarTela(javax.swing.RootPaneContainer tela) {
        BaixaVisao.configurarTela(tela);
        Daltonismo.configurarTela(tela);
    }
    public static void setTecladoAtivo(boolean ativo) {
        boolean anterior = tecladoAtivo; tecladoAtivo = ativo;
        eventos.firePropertyChange("tecladoAtivo", anterior, ativo);
    }
    public static void adicionarListener(PropertyChangeListener listener) { eventos.addPropertyChangeListener(listener); }
    public static void removerListener(PropertyChangeListener listener) { eventos.removePropertyChangeListener(listener); }
}
