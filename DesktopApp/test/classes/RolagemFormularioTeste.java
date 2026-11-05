package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.Connection;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import views.*;

/** Verifica campos finais e ações em janelas menores, sem acessar o banco real. */
public final class RolagemFormularioTeste {
    public static void main(String[] args) throws Exception {
        System.setProperty("mainrh.preferencias", "/mainrh/testes/rolagem-" + System.nanoTime());
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Method simular = DaltonismoTeste.class.getDeclaredMethod("simularJdbc", Class.class);
        simular.setAccessible(true);
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true); conexao.set(null, simular.invoke(null, Connection.class));
        SwingUtilities.invokeAndWait(() -> {
            try {
                for (JDialog tela : new JDialog[]{new TelaCadastroJd(null,false), new TelaEditarJd(null,false),
                        new TelaCadastroUsuarioJd(null,false), new TelaCadastrarProcessoJd(null,false),
                        new TelaEditarProcessoJd(null,false)}) verificar(tela);
                verificarMenu();
            } catch (Exception erro) { throw new RuntimeException(erro); }
            finally {
                Acessibilidade.restaurarPadrao();
                for (Window janela : Window.getWindows()) janela.dispose();
            }
        });
        System.out.println("OK: formularios em 1024x700 e 800x600; campos finais acessiveis; acoes fixas; menu sem cortes; restauracao.");
    }

    private static void verificar(JDialog tela) throws Exception {
        tela.setVisible(true);
        for (int modo : new int[]{0,1,2,0}) {
            Acessibilidade.setAltoContrasteAtivo(modo == 2);
            Acessibilidade.setBaixaVisaoAtiva(modo != 0);
            for (Dimension tamanho : new Dimension[]{new Dimension(1024,700), new Dimension(800,600)}) {
                tela.setSize(tamanho); organizar(tela);
                RolagemFormulario rolagem = localizar(tela, RolagemFormulario.class);
                PainelCampos campos = localizar(tela, PainelCampos.class);
                if (rolagem == null || campos == null) throw new AssertionError("Formulario sem rolagem");
                if (campos.getWidth() > rolagem.getViewport().getWidth()) throw new AssertionError("Rolagem horizontal");
                Component ultimo = campos.getComponent(campos.getComponentCount()-1);
                campos.scrollRectToVisible(ultimo.getBounds()); organizar(tela);
                rolagem.getVerticalScrollBar().setValue(rolagem.getVerticalScrollBar().getMaximum());
                organizar(tela);
                if (!rolagem.getViewport().getViewRect().intersects(ultimo.getBounds()))
                    throw new AssertionError(tela.getClass().getSimpleName()+": ultimo campo inacessivel");
                if (rolagem.getViewport().getViewRect().getMaxY() + 1 < ultimo.getBounds().getMaxY())
                    throw new AssertionError(tela.getClass().getSimpleName()+": parte final do campo inacessivel");
                JPanel acoes = (JPanel) campo(tela,"painelAcoes");
                for (Component botao : acoes.getComponents()) {
                    Rectangle area = SwingUtilities.convertRectangle(botao.getParent(),botao.getBounds(),tela);
                    if (!new Rectangle(tamanho).contains(area)) throw new AssertionError("Acao fora da janela: "+area);
                }
                verificarCampos(campos, campos);
                salvar(tela, tela.getClass().getSimpleName()+"-"+modo+"-"+tamanho.width+"-final");
                rolagem.getVerticalScrollBar().setValue(0); organizar(tela);
                salvar(tela, tela.getClass().getSimpleName()+"-"+modo+"-"+tamanho.width);
            }
        }
        tela.dispose();
    }

    private static void verificarCampos(Container painel, PainelCampos grade) {
        for (Component campo : painel.getComponents()) {
            if (campo instanceof JTextComponent || campo instanceof JComboBox) {
                Component entrada = campo instanceof JTextArea
                        ? (Component) SwingUtilities.getAncestorOfClass(JScrollPane.class, campo) : campo;
                Rectangle area=SwingUtilities.convertRectangle(entrada.getParent(),entrada.getBounds(),grade);
                if (area.width < 80 || area.height < 20 || !new Rectangle(grade.getSize()).contains(area))
                    throw new AssertionError("Entrada cortada: "+area+"; grade="+grade.getSize());
            }
            if (campo instanceof Container) verificarCampos((Container)campo,grade);
        }
    }

    private static void verificarMenu() {
        BarraMenu barra=new BarraMenu();
        barra.add(new JMenu("Gerenciamento")); barra.add(new JMenu("Cadastro")); barra.add(new JMenu("Saiba Mais"));
        for(Component campo:barra.getComponents()) if(campo instanceof JButton)campo.setPreferredSize(new Dimension(280,64));
        barra.setSize(1280,barra.getPreferredSize().height); barra.doLayout();
        for(Component campo:barra.getComponents()) if(campo instanceof JButton && campo.getHeight()<campo.getPreferredSize().height)
            throw new AssertionError("Botao da barra cortado");
    }

    private static Object campo(Object objeto,String nome)throws Exception{
        Field campo=objeto.getClass().getDeclaredField(nome);campo.setAccessible(true);return campo.get(objeto);
    }
    private static <T> T localizar(Component campo,Class<T> tipo){
        if(tipo.isInstance(campo))return tipo.cast(campo);
        if(campo instanceof Container)for(Component filho:((Container)campo).getComponents()){
            T achado=localizar(filho,tipo);if(achado!=null)return achado;
        }
        return null;
    }
    private static void organizar(Container painel){
        for(int i=0;i<2;i++){
            painel.doLayout();for(Component filho:painel.getComponents())if(filho instanceof Container)organizar((Container)filho);
        }
    }
    private static void salvar(JDialog tela,String nome)throws Exception{
        File arquivo=new File("DesktopApp/build/formularios/"+nome+".png");arquivo.getParentFile().mkdirs();
        BufferedImage imagem=new BufferedImage(tela.getWidth(),tela.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=imagem.createGraphics();tela.paint(g);g.dispose();ImageIO.write(imagem,"png",arquivo);
    }
}
