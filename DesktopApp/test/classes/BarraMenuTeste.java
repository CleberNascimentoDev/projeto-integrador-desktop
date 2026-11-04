package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Confere a pintura real do Nimbus, incluindo a área livre depois dos menus. */
public final class BarraMenuTeste {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeAndWait(() -> {
            JFrame janela = new JFrame() {
                @Override protected JRootPane createRootPane() {
                    return new JRootPane() { @Override public boolean isShowing() { return true; } };
                }
            };
            try {
                Acessibilidade.restaurarPadrao();
                BarraMenu barra = new BarraMenu();
                for(String nome:new String[]{"Gerenciamento","Cadastro","Saiba Mais"}) {
                    JMenu menu=new JMenu(nome); menu.add(new JMenuItem("Consultar")); barra.add(menu);
                }
                janela.setJMenuBar(barra);janela.setSize(1200,500); organizar(janela);
                Acessibilidade.configurarTela(janela);
                BufferedImage original=pintar(barra);
                salvar(original,"original");
                for(boolean baixa:new boolean[]{false,true}) {
                    Acessibilidade.setBaixaVisaoAtiva(baixa);
                    for(int modo=0;modo<4;modo++) {
                        if(modo==3)Acessibilidade.setAltoContrasteAtivo(true);
                        else Acessibilidade.setModoDaltonismo(ModoDaltonismo.values()[modo+1]);
                        organizar(janela); BufferedImage imagem=pintar(barra);
                        Color esperado=Acessibilidade.getPaleta().getSuperficie();
                        Color livre=new Color(imagem.getRGB(650,barra.getHeight()/2));
                        exigir(livre.equals(esperado),"Fundo da área livre não acompanha a paleta: "+livre+" esperado "+esperado);
                        for(int i=0;i<3;i++) {
                            JMenu menu=barra.getMenu(i);
                            Color fundo=new Color(imagem.getRGB(menu.getX()+3,barra.getHeight()/2));
                            exigir(fundo.equals(livre),"Retângulo de fundo diferente sob o menu "+menu.getText());
                        }
                        salvar(imagem,"modo-"+modo+"-baixa-"+baixa);
                        Acessibilidade.setAltoContrasteAtivo(false);
                    }
                    Acessibilidade.restaurarPadrao(); organizar(janela);
                    BufferedImage restaurada=pintar(barra);
                    salvar(restaurada,"padrao-restaurado");
                    exigir(original.getWidth()==restaurada.getWidth() && original.getHeight()==restaurada.getHeight(),"Dimensões originais não restauradas");
                    for(int y=0;y<original.getHeight();y++)for(int x=0;x<original.getWidth();x++)
                        exigir(original.getRGB(x,y)==restaurada.getRGB(x,y),"Pintura original não restaurada em "+x+","+y+" baixa="+baixa+" "+new Color(original.getRGB(x,y))+"/"+new Color(restaurada.getRGB(x,y)));
                    salvar(restaurada,"padrao-restaurado");
                }
                // Baixa visão sem paleta também deve pintar toda a barra de branco.
                Acessibilidade.setBaixaVisaoAtiva(true); organizar(janela);
                BufferedImage ampliada=pintar(barra);
                exigir(new Color(ampliada.getRGB(650,barra.getHeight()/2)).equals(Color.WHITE),"Barra ampliada conserva fundo do Nimbus");
                salvar(ampliada,"baixa-visao");
            } finally { Acessibilidade.restaurarPadrao();janela.dispose(); }
        });
        System.out.println("OK: fundo uniforme dos menus e da barra em todas as paletas, baixa visão e restauração pixel a pixel.");
    }
    private static void organizar(Container c) {c.doLayout();for(Component filho:c.getComponents())if(filho instanceof Container)organizar((Container)filho);}
    private static BufferedImage pintar(JComponent c) {
        BufferedImage imagem=new BufferedImage(c.getWidth(),c.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=imagem.createGraphics();c.printAll(g);g.dispose();return imagem;
    }
    private static void salvar(BufferedImage imagem,String nome) {
        try { File pasta=new File("DesktopApp/build/acessibilidade/menus");pasta.mkdirs();ImageIO.write(imagem,"png",new File(pasta,nome+".png")); }
        catch(Exception e){throw new RuntimeException(e);}
    }
    private static void exigir(boolean valor,String mensagem) {if(!valor)throw new AssertionError(mensagem);}
}
