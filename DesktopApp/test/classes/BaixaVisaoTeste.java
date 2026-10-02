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
        for (RootPaneContainer tela : telas) {
            if (tela instanceof Window) ((Window) tela).pack();
            else { ((JInternalFrame) tela).addNotify(); ((JInternalFrame) tela).pack(); }
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
            for (int repeticao = 0; repeticao < 3; repeticao++) {
                Acessibilidade.setBaixaVisaoAtiva(true);
                atualizar.invoke(null, tela.getRootPane());
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
                }
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
