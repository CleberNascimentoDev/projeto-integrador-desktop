package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Exercita a fila real de eventos, cliques, restauração e janelas abertas com modos ativos. */
public final class OpcoesAcessibilidadeTeste {
    private static JFrame janela, nova;
    private static TelaAcessibilidade opcoes;
    private static JTextField campo, campoNovo;
    private static JTable tabela;
    private static Object modelo;
    private static final Map<JComponent, Object[]> originais = new IdentityHashMap<>();

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        try {
            executar(() -> {
                Acessibilidade.restaurarPadrao();
                janela = new JFrame("Teste de acessibilidade");
                JPanel painel = new JPanel(new BorderLayout()); painel.setBackground(Color.WHITE);
                campo = new JTextField("Texto preservado");
                tabela = new TabelaPadrao();
                tabela.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{"Cargo"}}, new String[]{"Cargos"}));
                tabela.setRowSelectionInterval(0,0); modelo = tabela.getModel();
                painel.add(campo,BorderLayout.NORTH); painel.add(new JScrollPane(tabela));
                janela.setContentPane(painel); janela.setSize(700,400);
                Acessibilidade.configurarTela(janela); janela.setVisible(true);
                opcoes = new TelaAcessibilidade(janela); opcoes.setModal(false); opcoes.setVisible(true);
            });
            executar(() -> { guardar(janela.getContentPane()); guardar(opcoes.getContentPane()); });
            for (int ciclo=0; ciclo<2; ciclo++) {
                for (String nome : new String[]{"Baixa visão","Alto contraste","Protanopia","Alto contraste","Deuteranopia","Tritanopia","Padrão"}) {
                    executar(() -> botao(nome).doClick());
                    executar(() -> {
                        exigir(!(Acessibilidade.isAltoContrasteAtivo() && Acessibilidade.isDaltonismoAtivo()),"Modos incompatíveis simultâneos");
                        exigir(tabela.getModel()==modelo && tabela.getSelectedRow()==0 && campo.getText().equals("Texto preservado"),"Dados ou seleção alterados");
                        verificarLayout(opcoes.getContentPane(),opcoes.getContentPane());
                        salvar(nome);
                        exigir(opcoes.getWidth()<=1366 && opcoes.getHeight()<=728,"Opções ultrapassam a área útil de 1366x768: "+nome+" "+opcoes.getSize());
                        if(nome.equals("Padrão")) verificarRestauracao();
                        salvar(nome);
                    });
                }
            }
            executar(() -> {
                Acessibilidade.setAltoContrasteAtivo(true); Acessibilidade.setBaixaVisaoAtiva(true);
                nova = new JFrame(); JPanel painel = new JPanel(); painel.setBackground(Color.WHITE);
                campoNovo = new JTextField("Janela nova",20); campoNovo.setForeground(Color.BLACK); campoNovo.setBackground(Color.WHITE);
                painel.add(campoNovo); nova.setContentPane(painel); nova.pack();
                Acessibilidade.configurarTela(nova); nova.setVisible(true);
            });
            executar(Acessibilidade::restaurarPadrao);
            executar(() -> {
                exigir(campoNovo.getForeground().equals(Color.BLACK) && campoNovo.getBackground().equals(Color.WHITE),
                        "Janela criada com modos ativos não restaurou texto/fundo originais");
                verificarRestauracao();
            });
            System.out.println("OK: cliques reais, exclusão mútua, textos completos, janela 1366x768, restauração e novas janelas.");
        } finally {
            executar(() -> { Acessibilidade.restaurarPadrao(); for(Window w:Window.getWindows())w.dispose(); });
        }
    }
    private static void executar(Runnable acao) throws Exception {
        SwingUtilities.invokeAndWait(acao);
        SwingUtilities.invokeAndWait(() -> { });
        SwingUtilities.invokeAndWait(() -> { });
    }
    private static JButton botao(String texto) { return encontrar(opcoes.getContentPane(),texto); }
    private static JButton encontrar(Container raiz,String texto) {
        for(Component c:raiz.getComponents()) {
            if(c instanceof JButton && texto.equals(((JButton)c).getText()))return (JButton)c;
            if(c instanceof Container) { JButton b=encontrar((Container)c,texto); if(b!=null)return b; }
        }
        return null;
    }
    private static void guardar(Component c) {
        if(c instanceof JComponent)originais.put((JComponent)c,new Object[]{c.getForeground(),c.getBackground(),c.getFont()});
        if(c instanceof Container)for(Component filho:((Container)c).getComponents())guardar(filho);
    }
    private static void verificarRestauracao() {
        exigir(!Acessibilidade.isBaixaVisaoAtiva() && !Acessibilidade.isPaletaAdaptada(),"Padrão não desliga todos os modos visuais");
        originais.forEach((c,v) -> exigir(Objects.equals(c.getForeground(),v[0]) && Objects.equals(c.getBackground(),v[1])
                && Objects.equals(c.getFont(),v[2]),"Design original não restaurado: "+c.getClass().getSimpleName()));
    }
    private static void verificarLayout(Container raiz,Container painel) {
        for(Component c:raiz.getComponents()) {
            Rectangle area=SwingUtilities.convertRectangle(c.getParent(),c.getBounds(),painel);
            exigir(area.x>=0 && area.y>=0 && area.x+area.width<=painel.getWidth() && area.y+area.height<=painel.getHeight(),"Componente cortado: "+c.getClass().getSimpleName());
            if(c instanceof JButton) {
                JButton b=(JButton)c; Insets i=b.getInsets();
                String pintado=SwingUtilities.layoutCompoundLabel(b,b.getFontMetrics(b.getFont()),b.getText(),b.getIcon(),b.getVerticalAlignment(),b.getHorizontalAlignment(),
                        b.getVerticalTextPosition(),b.getHorizontalTextPosition(),new Rectangle(i.left,i.top,b.getWidth()-i.left-i.right,b.getHeight()-i.top-i.bottom),new Rectangle(),new Rectangle(),b.getIconTextGap());
                exigir(pintado.equals(b.getText()),"Rótulo cortado: "+b.getText());
            }
            if(c instanceof Container)verificarLayout((Container)c,painel);
        }
    }
    private static void salvar(String nome) {
        try {
            Container p=opcoes.getContentPane(); BufferedImage imagem=new BufferedImage(p.getWidth(),p.getHeight(),BufferedImage.TYPE_INT_RGB);
            Graphics2D g=imagem.createGraphics(); p.printAll(g);g.dispose();
            File pasta=new File("DesktopApp/build/acessibilidade/imagens");pasta.mkdirs();
            ImageIO.write(imagem,"png",new File(pasta,nome.replace(' ','-')+".png"));
        }catch(Exception e){throw new RuntimeException(e);}
    }
    private static void exigir(boolean valor,String mensagem) {if(!valor)throw new AssertionError(mensagem);}
}
