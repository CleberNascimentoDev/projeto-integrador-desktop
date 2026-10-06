package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import views.*;

/** Exercita cores reais, combinações, restauração, novas telas e preferências isoladas. */
public final class AltoContrasteTeste {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--gravar")) {
            SwingUtilities.invokeAndWait(() -> {
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.TRITANOPIA);
                Acessibilidade.setAltoContrasteAtivo(true);
            });
            System.out.println("OK: alto contraste e Tritanopia salvos"); return;
        }
        if (args.length > 0 && args[0].equals("--ler")) {
            exigir(Acessibilidade.isAltoContrasteAtivo(), "Alto contraste não foi carregado");
            exigir(Acessibilidade.getModoDaltonismo() == ModoDaltonismo.TRITANOPIA, "Paleta em espera perdida");
            SwingUtilities.invokeAndWait(() -> {
                Acessibilidade.setAltoContrasteAtivo(false);
                exigir(Acessibilidade.getPaleta() == PaletaAcessibilidade.paraModo(ModoDaltonismo.TRITANOPIA), "Paleta não restaurada");
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
            });
            System.out.println("OK: preferências carregadas em outra JVM e paleta restaurada"); return;
        }
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Method jdbc = DaltonismoTeste.class.getDeclaredMethod("simularJdbc", Class.class); jdbc.setAccessible(true);
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao"); conexao.setAccessible(true);
        conexao.set(null, jdbc.invoke(null, Connection.class));
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(); }
            catch (Exception e) { throw new RuntimeException(e); }
            finally {
                Acessibilidade.setAltoContrasteAtivo(false);
                Acessibilidade.setBaixaVisaoAtiva(false);
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
                for (Window janela : Window.getWindows()) janela.dispose();
            }
        });
        System.out.println("OK: alto contraste nas 13 telas, combinações, seleção, menus, campos, diálogos, restauração e logo.");
    }
    private static void verificar() throws Exception {
        Acessibilidade.setAltoContrasteAtivo(false); Acessibilidade.setBaixaVisaoAtiva(false);
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
        JFrame janela = new JFrame() {
            @Override protected JRootPane createRootPane() {
                return new JRootPane() { @Override public boolean isShowing() { return true; } };
            }
        };
        JPanel painel = new JPanel(); painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        JButton botao = new JButton("Cadastrar"), inativo = new JButton("Indisponível");
        PainelListagem.configurarBotao(botao, "Cadastrar", 18);
        inativo.setEnabled(false);
        JTextField campo = new JTextField("Texto selecionável", 30);
        JPasswordField senha = new JPasswordField("senha");
        JComboBox<String> combo = new JComboBox<>(new String[]{"ADM", "Recrutador"});
        JButton olho = new JButton();
        ImageIcon iconeOlho = new ImageIcon(AltoContrasteTeste.class.getResource("/icons/olhoAberto.png"));
        olho.setIcon(iconeOlho);
        JTable tabela = new JTable(new DefaultTableModel(new Object[][]{{"DEV SENIOR", "Não atribuído"}}, new String[]{"Processo", "Recrutador"}));
        JScrollPane rolagem = new JScrollPane(tabela); PainelListagem.configurarTabela(tabela, rolagem, true);
        tabela.setRowSelectionInterval(0, 0);
        for (JComponent c : new JComponent[]{botao,inativo,campo,senha,olho,combo,rolagem,new BotaoAcessibilidade()}) painel.add(c);
        janela.setContentPane(painel); janela.getRootPane().setDefaultButton(botao);
        JMenuBar barra = new JMenuBar(); JMenu menu = new JMenu("Gerenciamento");
        menu.add(new JMenuItem("Cadastrar")); barra.add(menu); janela.setJMenuBar(barra); janela.pack(); Acessibilidade.configurarTela(janela);
        Color originalFundo = campo.getBackground(), originalTexto = campo.getForeground(), originalGrid = tabela.getGridColor();
        Object modelo = tabela.getModel(); int eventos = botao.getActionListeners().length;
        for (boolean baixa : new boolean[]{false,true}) for (ModoDaltonismo modo : ModoDaltonismo.values()) {
            Acessibilidade.setBaixaVisaoAtiva(baixa); Acessibilidade.setModoDaltonismo(modo);
            Dimension tamanho = janela.getSize(); Font fonte = campo.getFont();
            for (int i = 0; i < 2; i++) {
                Acessibilidade.setAltoContrasteAtivo(true);
                PaletaAcessibilidade p = Acessibilidade.getPaleta();
                exigir(p == PaletaAcessibilidade.altoContraste(), "Alto contraste não tem prioridade");
                exigir(campo.getFont().equals(fonte) && janela.getSize().equals(tamanho), "Alto contraste mudou fontes/dimensões");
                exigir(tabela.getSelectedRow() == 0 && tabela.getModel() == modelo, "Seleção ou modelo alterado");
                exigir(botao.getActionListeners().length == eventos && Acessibilidade.isTecladoAtivo(), "Eventos/teclado alterados");
                for (JComponent c : new JComponent[]{botao,inativo,campo,senha,combo,menu}) verificarTexto(c);
                Component celula = tabela.prepareRenderer(tabela.getCellRenderer(0,0),0,0);
                verificarTexto(celula); exigir(((JLabel)celula).getIcon() instanceof IconeSelecao, "Seleção sem marca");
                Component cabecalho = tabela.getColumnModel().getColumn(0).getHeaderRenderer().getTableCellRendererComponent(tabela,"Processo",false,false,-1,0);
                verificarTexto(cabecalho);
                exigir(contraste(p.getBorda(),p.getSuperficie()) >= 3, "Borda sem contraste");
                exigir(contraste(p.getTextoAviso(),p.getAviso()) >= 7, "Aviso sem contraste");
                exigir(olho.getIcon() instanceof IconeImagem, "Olho não adaptado");
                verificarIcone(olho);
                verificarSeta(combo);
                BufferedImage imagemBotao = new BufferedImage(botao.getWidth(),botao.getHeight(),BufferedImage.TYPE_INT_RGB);
                Graphics2D desenhoBotao = imagemBotao.createGraphics(); botao.printAll(desenhoBotao); desenhoBotao.dispose();
                exigir(contraste(botao.getForeground(),new Color(imagemBotao.getRGB(8,botao.getHeight()/2))) >= 7,
                        "Pintura do botão padrão sem contraste");
                inativo.setEnabled(true); verificarTexto(inativo); inativo.setEnabled(false);
                botao.getModel().setRollover(true); verificarTexto(botao); botao.getModel().setRollover(false);
                Acessibilidade.setAltoContrasteAtivo(false);
                exigir(olho.getIcon() == iconeOlho, "Ícone original não restaurado");
                exigir(Acessibilidade.getModoDaltonismo() == modo && Acessibilidade.isBaixaVisaoAtiva() == baixa, "Preferências alteradas");
                exigir(Acessibilidade.getPaleta() == PaletaAcessibilidade.paraModo(modo), "Daltonismo não restaurado");
            }
        }
        Acessibilidade.setBaixaVisaoAtiva(false); Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
        exigir(campo.getBackground().equals(originalFundo) && campo.getForeground().equals(originalTexto)
                && tabela.getGridColor().equals(originalGrid), "Padrão não restaurado");
        Acessibilidade.setAltoContrasteAtivo(true);
        RootPaneContainer[] telas = {new TelaLogin(),new TelaMenu(),new TelaCargo(),new TelaGestaoRec(),new TelaProcessoSeletivo(),
                new TelaDetalhesCargo(),new TelaCadastroJd(null,false),new TelaEditarJd(null,false),new TelaCadastroUsuarioJd(null,false),
                new TelaCadastrarProcessoJd(null,false),new TelaEditarProcessoJd(null,false),new TelaAtribuirRecJd(null,false),new TelaAcessibilidade(null)};
        File pasta = new File("DesktopApp/build/alto-contraste/imagens"); pasta.mkdirs();
        for (RootPaneContainer tela : telas) {
            if (tela instanceof Window) ((Window)tela).pack(); else { ((JInternalFrame)tela).addNotify(); ((JInternalFrame)tela).pack(); }
            atualizar(tela); organizar((Container)tela);
            auditar(tela.getContentPane()); salvar(tela.getContentPane(),new File(pasta,tela.getClass().getSimpleName()+".png"));
            if (tela instanceof TelaAcessibilidade) {
                JButton controle = encontrar(tela.getContentPane()); exigir(controle != null,"Botão de alto contraste ausente");
                controle.doClick(); exigir(!Acessibilidade.isAltoContrasteAtivo(),"Botão não desativa");
                controle.doClick(); exigir(Acessibilidade.isAltoContrasteAtivo(),"Botão não ativa");
                Acessibilidade.setBaixaVisaoAtiva(true);
                Method baixa = BaixaVisao.class.getDeclaredMethod("atualizarTela",JRootPane.class); baixa.setAccessible(true); baixa.invoke(null,tela.getRootPane());
                atualizar(tela); organizar((Container)tela); salvar(tela.getContentPane(),new File(pasta,"Opcoes-baixa-visao.png"));
                ((Window)tela).setSize(((Window)tela).getWidth(), 700);
                organizar((Container)tela);
                verificarControlesVisiveis(tela.getContentPane(), tela.getContentPane());
                salvar(tela.getContentPane(),new File(pasta,"Opcoes-baixa-visao-768.png"));
                Acessibilidade.setBaixaVisaoAtiva(false);
            }
        }
        for (int tipo : new int[]{JOptionPane.ERROR_MESSAGE,JOptionPane.WARNING_MESSAGE,JOptionPane.INFORMATION_MESSAGE}) {
            JDialog dialogo = new JOptionPane("Verifique os dados informados.",tipo).createDialog("Mensagem");
            Acessibilidade.configurarTela(dialogo); atualizar(dialogo); organizar(dialogo); auditar(dialogo.getContentPane());
            salvar(dialogo.getContentPane(),new File(pasta,"Mensagem-"+tipo+".png")); dialogo.dispose();
        }
        BotaoAjuda ajuda = new BotaoAjuda(); Method criar = BotaoAjuda.class.getDeclaredMethod("criarJanelaAjuda"); criar.setAccessible(true);
        JDialog dialogo = (JDialog)criar.invoke(ajuda); atualizar(dialogo); organizar(dialogo); auditar(dialogo.getContentPane());
        salvar(dialogo.getContentPane(),new File(pasta,"Ajuda.png")); dialogo.dispose();
        for (RootPaneContainer tela:telas) if(tela instanceof Window)((Window)tela).dispose();else((JInternalFrame)tela).dispose();
        janela.dispose();
    }
    private static void atualizar(RootPaneContainer tela) throws Exception {
        Method m=Daltonismo.class.getDeclaredMethod("atualizarTela",JRootPane.class);m.setAccessible(true);m.invoke(null,tela.getRootPane());
    }
    private static JButton encontrar(Container raiz) {
        for(Component c:raiz.getComponents()) {
            if(c instanceof JButton && Boolean.TRUE.equals(((JButton)c).getClientProperty("altoContraste")))return(JButton)c;
            if(c instanceof Container){JButton b=encontrar((Container)c);if(b!=null)return b;}
        }return null;
    }
    private static void verificarControlesVisiveis(Container raiz, Container painel) {
        for (Component c : raiz.getComponents()) {
            if (c instanceof JButton) {
                Rectangle r = SwingUtilities.convertRectangle(c.getParent(), c.getBounds(), painel);
                exigir(r.y >= 0 && r.y + r.height <= painel.getHeight(), "Opção fora da janela menor");
                exigir(c.getHeight() >= c.getPreferredSize().height, "Opção cortada na janela menor: " + ((JButton)c).getText() + " " + c.getHeight() + "/" + c.getPreferredSize().height);
            }
            if (c instanceof Container) verificarControlesVisiveis((Container)c, painel);
        }
    }
    private static void verificarIcone(JButton botao) {
        Icon icone = botao.getIcon();
        BufferedImage imagem = new BufferedImage(icone.getIconWidth(), icone.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics(); icone.paintIcon(botao, g, 0, 0); g.dispose();
        int claros = 0;
        for (int y = 0; y < imagem.getHeight(); y++) for (int x = 0; x < imagem.getWidth(); x++) {
            Color cor = new Color(imagem.getRGB(x,y),true);
            if (cor.getAlpha() > 240 && contraste(cor, Color.BLACK) >= 7) claros++;
        }
        exigir(claros > 5, "Ícone da senha invisível");
    }
    private static void verificarSeta(JComboBox<?> combo) {
        organizar(combo);
        for (Component c : combo.getComponents()) if (c instanceof JButton) {
            BufferedImage imagem = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagem.createGraphics(); c.printAll(g); g.dispose();
            int claros = 0;
            for (int y = Math.max(3,c.getHeight()/2-7); y < Math.min(c.getHeight()-3,c.getHeight()/2+7); y++)
                for (int x = 3; x < c.getWidth()-3; x++) if (contraste(new Color(imagem.getRGB(x,y)), Color.BLACK) >= 7) claros++;
            exigir(claros >= 10, "Seta do campo de seleção invisível");
        }
    }
    private static void auditar(Component c) {
        if(c instanceof JLabel && ((JLabel)c).getText() != null && !((JLabel)c).getText().isEmpty() || c instanceof JTextField || c instanceof JButton) {
            Color fundo=c.getBackground();
            if(c instanceof JLabel || c instanceof JButton && !((JButton)c).isContentAreaFilled()) {
                Container pai=c.getParent();if(pai!=null)fundo=pai.getBackground();
            }
            if(c.getForeground()!=null && fundo!=null) exigir(contraste(c.getForeground(),fundo)>=7,"Texto sem contraste: "+c.getClass().getSimpleName()+" "+(c instanceof JLabel?((JLabel)c).getText():"")+" "+c.getForeground()+"/"+fundo);
        }
        if(c instanceof Container && !(c instanceof JComboBox))for(Component filho:((Container)c).getComponents())auditar(filho);
    }
    private static void organizar(Container c){c.doLayout();for(Component f:c.getComponents())if(f instanceof Container)organizar((Container)f);}
    private static void salvar(Component c,File arquivo)throws Exception{BufferedImage i=new BufferedImage(Math.max(1,c.getWidth()),Math.max(1,c.getHeight()),BufferedImage.TYPE_INT_RGB);Graphics2D g=i.createGraphics();c.printAll(g);g.dispose();ImageIO.write(i,"png",arquivo);}
    private static void verificarTexto(Component c){exigir(contraste(c.getForeground(),c.getBackground())>=7,"Texto sem contraste: "+c.getClass().getSimpleName());}
    private static double contraste(Color a,Color b){double la=lum(a),lb=lum(b);return(Math.max(la,lb)+.05)/(Math.min(la,lb)+.05);}
    private static double lum(Color c){double[]v={c.getRed()/255.,c.getGreen()/255.,c.getBlue()/255.};for(int i=0;i<3;i++)v[i]=v[i]<=.04045?v[i]/12.92:Math.pow((v[i]+.055)/1.055,2.4);return .2126*v[0]+.7152*v[1]+.0722*v[2];}
    private static void exigir(boolean ok,String mensagem){if(!ok)throw new AssertionError(mensagem);}
}
