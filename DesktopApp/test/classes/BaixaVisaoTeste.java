package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import views.*;

/** Verificação manual executável sem banco de dados e sem abrir janelas. */
public class BaixaVisaoTeste {
    private static final java.util.List<RootPaneContainer> telas = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        if (args.length > 1 && "nimbus".equals(args[1])) {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        // Consultas vazias permitem verificar as telas sem acessar ou modificar dados reais.
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null, simularJdbc(Connection.class));
        SwingUtilities.invokeAndWait(() -> {
            try {
                verificar(args.length > 0 ? new File(args[0]) : null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Acessibilidade.setBaixaVisaoAtiva(false);
                Acessibilidade.setTecladoAtivo(true);
                for (RootPaneContainer tela : telas) {
                    if (tela instanceof Window) ((Window) tela).dispose();
                    else ((JInternalFrame) tela).dispose();
                }
            }
        });
        System.out.println("OK: 13 telas, ampliação, contraste, restauração e modos independentes.");
    }

    private static void verificar(File imagens) throws Exception {
        telas.add(new TelaLogin());
        telas.add(new TelaMenu());
        telas.add(new TelaCargo());
        telas.add(new TelaGestaoRec());
        telas.add(new TelaProcessoSeletivo());
        telas.add(new TelaDetalhesCargo());
        telas.add(new TelaCadastroJd(null, false));
        telas.add(new TelaEditarJd(null, false));
        telas.add(new TelaCadastroUsuarioJd(null, false));
        telas.add(new TelaCadastrarProcessoJd(null, false));
        telas.add(new TelaEditarProcessoJd(null, false));
        telas.add(new TelaAtribuirRecJd(null, false));
        telas.add(new TelaAcessibilidade(null));
        Method atualizar = BaixaVisao.class.getDeclaredMethod("atualizarTela", JRootPane.class);
        atualizar.setAccessible(true);
        Acessibilidade.setTecladoAtivo(false);
        JDesktopPane desktop = new JDesktopPane();
        desktop.setSize(1920, 960);
        for (RootPaneContainer tela : telas) {
            if (tela instanceof Window) ((Window) tela).pack();
            else {
                desktop.add((JInternalFrame) tela);
                ((JInternalFrame) tela).addNotify();
                ((JInternalFrame) tela).pack();
            }
            organizar((Container) tela);
            Container conteudo = tela.getContentPane();
            organizar(conteudo);
            Map<JComponent, Font> fontes = new IdentityHashMap<>();
            guardarFontes(conteudo, fontes);
            Map<JComponent, Color[]> cores = new IdentityHashMap<>();
            for (JComponent campo : fontes.keySet()) {
                cores.put(campo, new Color[]{campo.getForeground(), campo.getBackground()});
            }
            Dimension tamanho = ((Component) tela).getSize();
            LayoutManager layout = conteudo.getLayout();
            int rolagens = contarRolagens(conteudo);
            for (int repeticao = 0; repeticao < 3; repeticao++) {
                Acessibilidade.setBaixaVisaoAtiva(true);
                atualizar.invoke(null, tela.getRootPane());
                organizar((Container) tela);
                exigir(tela.getContentPane() == conteudo, "Baixa visão adicionou rolagem à janela");
                exigir(contarRolagens(conteudo) == rolagens, "Baixa visão criou barras adicionais");
                if (tela instanceof JInternalFrame) {
                    exigir(((Component) tela).getWidth() <= desktop.getWidth()
                                    && ((Component) tela).getHeight() <= desktop.getHeight(),
                            "Janela interna excedeu a área do menu");
                }
                if (tela.getRootPane().getJMenuBar() != null) verificarMenus(tela.getRootPane().getJMenuBar());
                for (Map.Entry<JComponent, Font> entrada : fontes.entrySet()) {
                    if (entrada.getValue() != null && !(entrada.getKey() instanceof JDesktopPane)) {
                        float esperado = Math.max(18f, entrada.getValue().getSize2D() * 1.5f);
                        exigir(entrada.getKey().getFont().getSize2D() == esperado,
                                tela.getClass().getSimpleName() + ": fonte não ampliada em "
                                        + entrada.getKey().getClass().getSimpleName() + ": "
                                        + entrada.getKey().getFont() + "; esperado " + esperado);
                    }
                    if (entrada.getKey() instanceof JLabel
                            || entrada.getKey() instanceof javax.swing.text.JTextComponent) {
                        exigir(Color.BLACK.equals(entrada.getKey().getForeground()),
                                "Texto sem alto contraste em " + tela.getClass().getSimpleName());
                    }
                }
                if (repeticao == 0 && imagens != null) {
                    imagens.mkdirs();
                    organizar((Container) tela);
                    Dimension area = tela.getContentPane().getSize();
                    BufferedImage imagem = new BufferedImage(Math.max(1, area.width),
                            Math.max(1, area.height), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = imagem.createGraphics();
                    tela.getContentPane().printAll(g);
                    g.dispose();
                    ImageIO.write(imagem, "png", new File(imagens, tela.getClass().getSimpleName() + ".png"));
                    JMenuBar barra = tela.getRootPane().getJMenuBar();
                    if (barra != null) {
                        BufferedImage imagemMenu = new BufferedImage(barra.getWidth(), barra.getHeight(), BufferedImage.TYPE_INT_RGB);
                        for (boolean selecionado : new boolean[]{false, true}) {
                            barra.getMenu(0).setSelected(selecionado);
                            Graphics2D graficoMenu = imagemMenu.createGraphics();
                            barra.printAll(graficoMenu);
                            graficoMenu.dispose();
                            ImageIO.write(imagemMenu, "png", new File(imagens,
                                    selecionado ? "MenuSelecionado.png" : "MenuNormal.png"));
                        }
                        barra.getMenu(0).setSelected(false);
                    }
                }
                verificarLimites(conteudo);
                Acessibilidade.setBaixaVisaoAtiva(false);
                atualizar.invoke(null, tela.getRootPane());
                exigir(tela.getContentPane() == conteudo, "Conteúdo original não restaurado");
                exigir(conteudo.getLayout() == layout, "Layout original não restaurado");
                exigir(((Component) tela).getSize().equals(tamanho), "Tamanho original não restaurado");
                for (Map.Entry<JComponent, Font> entrada : fontes.entrySet()) {
                    exigir(Objects.equals(entrada.getKey().getFont(), entrada.getValue()),
                            "Fonte original não restaurada");
                    Color[] original = cores.get(entrada.getKey());
                    exigir(Objects.equals(entrada.getKey().getForeground(), original[0])
                                    && Objects.equals(entrada.getKey().getBackground(), original[1]),
                            "Cores originais não restauradas");
                }
                exigir(!Acessibilidade.isTecladoAtivo(), "Baixa visão alterou a preferência de teclado");
            }
        }
    }

    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component filho : painel.getComponents()) {
            if (filho instanceof Container) organizar((Container) filho);
        }
    }

    private static void verificarBotao(JButton botao) {
        Insets margens = botao.getInsets();
        Rectangle area = new Rectangle(0, 0, botao.getWidth() - margens.left - margens.right,
                botao.getHeight() - margens.top - margens.bottom);
        Rectangle icone = new Rectangle();
        Rectangle texto = new Rectangle();
        String rotulo = SwingUtilities.layoutCompoundLabel(botao, botao.getFontMetrics(botao.getFont()),
                botao.getText(), botao.getIcon(), botao.getVerticalAlignment(), botao.getHorizontalAlignment(),
                botao.getVerticalTextPosition(), botao.getHorizontalTextPosition(), area, icone, texto, botao.getIconTextGap());
        exigir(Objects.equals(rotulo, botao.getText() == null ? "" : botao.getText()),
                "Botão com texto incompleto: " + botao.getText() + " -> " + rotulo);
        exigir(area.contains(icone) || botao.getIcon() == null, "Ícone cortado no botão " + botao.getText());
        exigir(area.contains(texto) || rotulo.isEmpty(), "Texto cortado no botão " + botao.getText());
    }

    private static int contarRolagens(Component componente) {
        int quantidade = componente instanceof JScrollPane ? 1 : 0;
        if (componente instanceof Container && !(componente instanceof JInternalFrame)) {
            for (Component filho : ((Container) componente).getComponents()) quantidade += contarRolagens(filho);
        }
        return quantidade;
    }

    private static void verificarLimites(Container painel) {
        for (Component filho : painel.getComponents()) {
            if (filho instanceof JButton) verificarBotao((JButton) filho);
            if (filho instanceof JButton || filho instanceof javax.swing.text.JTextComponent
                    || filho instanceof JComboBox || filho instanceof JScrollPane || filho instanceof JPanel
                    || filho instanceof JDesktopPane) {
                exigir(filho.getX() >= 0 && filho.getY() >= 0
                                && filho.getX() + filho.getWidth() <= painel.getWidth()
                                && filho.getY() + filho.getHeight() <= painel.getHeight(),
                        "Controle cortado: " + filho.getClass().getSimpleName() + " " + filho.getBounds()
                                + " em " + painel.getClass().getSimpleName() + " " + painel.getSize());
            }
            if (filho instanceof JPanel || filho instanceof JDesktopPane) verificarLimites((Container) filho);
        }
    }

    private static void verificarMenus(JMenuBar barra) {
        for (int i = 0; i < barra.getMenuCount(); i++) {
            JMenu menu = barra.getMenu(i);
            if (menu == null) continue;
            exigir(Color.BLACK.equals(menu.getForeground()), "Menu superior sem contraste");
            for (Component item : menu.getMenuComponents()) {
                if (item instanceof JMenuItem) {
                    exigir(Color.BLACK.equals(item.getForeground()), "Item de menu sem contraste");
                }
            }
            if (UIManager.getLookAndFeel() instanceof javax.swing.plaf.nimbus.NimbusLookAndFeel) {
                var estilo = javax.swing.plaf.synth.SynthLookAndFeel.getStyle(menu, javax.swing.plaf.synth.Region.MENU);
                var normal = new javax.swing.plaf.synth.SynthContext(menu, javax.swing.plaf.synth.Region.MENU,
                        estilo, javax.swing.plaf.synth.SynthConstants.ENABLED);
                var selecionado = new javax.swing.plaf.synth.SynthContext(menu, javax.swing.plaf.synth.Region.MENU,
                        estilo, javax.swing.plaf.synth.SynthConstants.ENABLED | javax.swing.plaf.synth.SynthConstants.SELECTED);
                exigir(Color.BLACK.equals(estilo.getColor(normal, javax.swing.plaf.synth.ColorType.TEXT_FOREGROUND)),
                        "Nimbus desenha texto claro no menu normal");
                menu.setSelected(true);
                exigir(Color.WHITE.equals(estilo.getColor(selecionado, javax.swing.plaf.synth.ColorType.TEXT_FOREGROUND)),
                        "Nimbus perdeu contraste no menu selecionado: "
                                + estilo.getColor(selecionado, javax.swing.plaf.synth.ColorType.TEXT_FOREGROUND)
                                + "; frente=" + menu.getForeground().getClass().getName());
                menu.setSelected(false);
            }
        }
    }

    private static void guardarFontes(Component componente, Map<JComponent, Font> fontes) {
        if (componente instanceof JInternalFrame) return;
        if (componente instanceof JComponent) fontes.put((JComponent) componente, componente.getFont());
        if (componente instanceof Container && !(componente instanceof JComboBox)
                && !(componente instanceof AbstractButton) && !(componente instanceof JTable)
                && !(componente instanceof JScrollBar)) {
            for (Component filho : ((Container) componente).getComponents()) guardarFontes(filho, fontes);
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static Object simularJdbc(Class<?> tipo) {
        return Proxy.newProxyInstance(BaixaVisaoTeste.class.getClassLoader(), new Class<?>[]{tipo},
                (proxy, metodo, argumentos) -> {
                    Class<?> retorno = metodo.getReturnType();
                    if (retorno == PreparedStatement.class) return simularJdbc(PreparedStatement.class);
                    if (retorno == ResultSet.class) return simularJdbc(ResultSet.class);
                    if (retorno == boolean.class) return false;
                    if (retorno == int.class) return 0;
                    if (retorno == long.class) return 0L;
                    if (retorno == double.class) return 0.0;
                    return null;
                });
    }
}
