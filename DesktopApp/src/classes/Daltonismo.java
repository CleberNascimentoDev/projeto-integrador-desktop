package classes;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/** Paleta alternativa; mantém textos, símbolos e os layouts dos formulários. */
public final class Daltonismo {
    private static final Color azul = new Color(0, 85, 140);
    private static final Color laranja = new Color(255, 210, 128);
    private static final Color fundoNeutro = new Color(248, 245, 238);
    private static final String chaveEstado = "daltonismo.estado";
    private static final List<WeakReference<JRootPane>> telas = new ArrayList<>();
    private static boolean observandoJanelas;

    private Daltonismo() { }

    public static void configurarTela(RootPaneContainer tela) {
        JRootPane raiz = tela.getRootPane();
        for (WeakReference<JRootPane> referencia : telas) {
            if (referencia.get() == raiz) return;
        }
        telas.add(new WeakReference<>(raiz));
        raiz.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && raiz.isShowing()) {
                SwingUtilities.invokeLater(() -> atualizarTela(raiz));
            }
        });
        // Mensagens de validação e diálogos de ajuda também recebem a paleta.
        if (!observandoJanelas) {
            observandoJanelas = true;
            Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
                if (e instanceof WindowEvent && e.getID() == WindowEvent.WINDOW_OPENED
                        && e.getSource() instanceof RootPaneContainer) {
                    RootPaneContainer janela = (RootPaneContainer) e.getSource();
                    configurarTela(janela);
                    atualizarTela(janela.getRootPane());
                }
            }, AWTEvent.WINDOW_EVENT_MASK);
        }
    }

    public static void atualizarTelas() {
        telas.removeIf(referencia -> referencia.get() == null);
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null && raiz.isShowing()) atualizarTela(raiz);
        }
    }

    private static void atualizarTela(JRootPane raiz) {
        if (Acessibilidade.isBaixaVisaoAtiva()) return;
        if (Acessibilidade.isDaltonismoAtivo()) {
            guardarComponentes(raiz.getContentPane());
            if (raiz.getJMenuBar() != null) guardarComponentes(raiz.getJMenuBar());
        }
        aplicarComponentes(raiz.getContentPane());
        if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar());
        raiz.repaint();
    }

    public static void restaurarTelas() {
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null) {
                aplicarComponentes(raiz.getContentPane(), false);
                if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar(), false);
            }
        }
    }

    private static void guardarComponentes(Component componente) {
        if (!(componente instanceof JComponent) || componente instanceof JInternalFrame) return;
        JComponent campo = (JComponent) componente;
        if (campo.getClientProperty(chaveEstado) == null) {
            campo.putClientProperty(chaveEstado, new EstadoComponente(campo));
        }
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JScrollPane
                || campo instanceof JViewport || campo instanceof JMenuBar || campo instanceof JPopupMenu
                || campo instanceof JOptionPane) {
            for (Component filho : campo.getComponents()) guardarComponentes(filho);
        }
        if (campo instanceof JMenu) guardarComponentes(((JMenu) campo).getPopupMenu());
    }

    private static void aplicarComponentes(Component componente) {
        aplicarComponentes(componente, Acessibilidade.isDaltonismoAtivo() && !Acessibilidade.isBaixaVisaoAtiva());
    }

    private static void aplicarComponentes(Component componente, boolean ativo) {
        if (!(componente instanceof JComponent) || componente instanceof JInternalFrame) return;
        JComponent campo = (JComponent) componente;
        EstadoComponente estado = (EstadoComponente) campo.getClientProperty(chaveEstado);
        if (ativo) {
            if (estado == null) {
                estado = new EstadoComponente(campo);
                campo.putClientProperty(chaveEstado, estado);
            }
            estado.aplicar(campo);
        } else if (estado != null) {
            campo.removePropertyChangeListener(estado.preferenciaCores);
            campo.setForeground(estado.frente);
            campo.setBackground(estado.fundo);
            campo.setBorder(estado.borda);
            campo.putClientProperty(chaveEstado, null);
        }
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JScrollPane
                || campo instanceof JViewport || campo instanceof JMenuBar || campo instanceof JPopupMenu
                || campo instanceof JOptionPane) {
            for (Component filho : campo.getComponents()) aplicarComponentes(filho, ativo);
        }
        if (campo instanceof JMenu) aplicarComponentes(((JMenu) campo).getPopupMenu(), ativo);
    }

    private static boolean vermelho(Color cor) {
        return cor != null && cor.getRed() > cor.getGreen() * 1.4
                && cor.getRed() > cor.getBlue() * 1.4;
    }

    private static boolean verde(Color cor) {
        return cor != null && cor.getGreen() > cor.getRed() * 1.3
                && cor.getGreen() > cor.getBlue() * 1.1;
    }

    private static boolean amarelo(Color cor) {
        return cor != null && cor.getRed() > 180 && cor.getGreen() > 120
                && cor.getBlue() < 80;
    }

    private static Color adaptarBorda(Color cor) {
        if (vermelho(cor) || amarelo(cor)) return new Color(128, 72, 0);
        if (verde(cor)) return azul;
        return cor;
    }

    private static Border adaptarBorda(Border borda) {
        if (borda instanceof LineBorder) {
            LineBorder linha = (LineBorder) borda;
            return new LineBorder(adaptarBorda(linha.getLineColor()), linha.getThickness(), linha.getRoundedCorners());
        }
        if (borda instanceof CompoundBorder) {
            CompoundBorder composta = (CompoundBorder) borda;
            return new CompoundBorder(adaptarBorda(composta.getOutsideBorder()), adaptarBorda(composta.getInsideBorder()));
        }
        return borda;
    }

    static Color adaptarFundo(JComponent campo, Color fundo) {
        if (campo instanceof AbstractButton && ((AbstractButton) campo).isContentAreaFilled()
                && !(campo instanceof JCheckBox) && !(campo instanceof JRadioButton)) {
            String nome = ((AbstractButton) campo).getText();
            if ((nome != null && nome.startsWith("Excluir")) || vermelho(fundo) || amarelo(fundo)) return laranja;
            if (fundo != null && (verde(fundo) || (fundo.getBlue() > fundo.getRed() && fundo.getRed() < 100))) return azul;
        }
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JViewport
                || campo instanceof JOptionPane || campo instanceof JMenuBar || campo instanceof JPopupMenu) return fundoNeutro;
        return fundo;
    }

    static Color adaptarTexto(JComponent campo, Color frente, Color fundo) {
        Color adaptado = adaptarFundo(campo, fundo);
        if (campo instanceof AbstractButton && ((AbstractButton) campo).isContentAreaFilled()
                && !(campo instanceof JCheckBox) && !(campo instanceof JRadioButton)) {
            if (laranja.equals(adaptado)) return Color.BLACK;
            if (azul.equals(adaptado)) return Color.WHITE;
        }
        return vermelho(frente) || verde(frente) || amarelo(frente) ? new Color(29, 45, 68) : frente;
    }

    private static final class EstadoComponente {
        Color frente, fundo;
        Border borda;
        boolean ajustandoCores;
        final PropertyChangeListener preferenciaCores;

        EstadoComponente(JComponent campo) {
            frente = campo.getForeground();
            fundo = campo.getBackground();
            borda = campo.getBorder();
            preferenciaCores = e -> {
                if (ajustandoCores) return;
                if ("foreground".equals(e.getPropertyName())) frente = (Color) e.getNewValue();
                else if ("background".equals(e.getPropertyName())) fundo = (Color) e.getNewValue();
                else if ("border".equals(e.getPropertyName())) borda = (Border) e.getNewValue();
                else return;
                aplicar(campo);
            };
            campo.addPropertyChangeListener(preferenciaCores);
        }

        void aplicar(JComponent campo) {
            ajustandoCores = true;
            try {
                Color corFundo = adaptarFundo(campo, fundo);
                Color corTexto = adaptarTexto(campo, frente, fundo);
                campo.setBackground(corFundo);
                campo.setForeground(corTexto);
                campo.setBorder(adaptarBorda(borda));
            } finally {
                ajustandoCores = false;
            }
        }
    }
}
