package classes;

import java.awt.*;
import java.beans.*;
import java.io.File;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/** Verifica contratos JavaBean, eventos e a hierarquia do .form contra a tela executada. */
public final class FormulariosNetBeansTeste {
    private static final String[] telas = {"views.TelaLogin", "views.TelaMenu", "views.TelaCargo",
        "views.TelaProcessoSeletivo", "views.TelaGestaoRec", "views.TelaAtribuirRecJd",
        "views.TelaCadastroJd", "views.TelaEditarJd", "views.TelaDetalhesCargo",
        "views.TelaCadastrarProcessoJd", "views.TelaEditarProcessoJd", "views.TelaCadastroUsuarioJd",
        "views.TelaMapaAtalhoJd", "classes.TelaAcessibilidade"};

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Method simular = DaltonismoTeste.class.getDeclaredMethod("simularJdbc",Class.class);
        simular.setAccessible(true);
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null,simular.invoke(null,Connection.class));
        SwingUtilities.invokeAndWait(() -> {
            try {
                Acessibilidade.setBaixaVisaoAtiva(false);
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
                for (String nome : telas) verificar(nome);
            } catch (Exception e) { throw new RuntimeException(e); }
            finally { for (Window janela : Window.getWindows()) janela.dispose(); }
        });
        System.out.println("OK: 14 formulários, construtores JavaBean, propriedades, eventos e hierarquia visual preservada após initComponents.");
    }

    private static void verificar(String nome) throws Exception {
        Class<?> tipo = Class.forName(nome);
        RootPaneContainer tela;
        if (tipo == TelaAcessibilidade.class) tela = new TelaAcessibilidade(null);
        else if (JDialog.class.isAssignableFrom(tipo))
            tela = (RootPaneContainer)tipo.getConstructor(Frame.class,boolean.class).newInstance(null,false);
        else tela = (RootPaneContainer)tipo.getConstructor().newInstance();
        File arquivo = new File("DesktopApp/src/" + nome.replace('.','/') + ".form");
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        org.w3c.dom.Document formulario = fabrica.newDocumentBuilder().parse(arquivo);
        NodeList todos = formulario.getElementsByTagName("*");
        java.util.List<Object> prototipos = new ArrayList<>();
        try {
            for (int i=0; i<todos.getLength(); i++) {
                Element no = (Element)todos.item(i);
                if (!no.hasAttribute("class") || !no.hasAttribute("name")
                        || !(no.getTagName().equals("Component") || no.getTagName().equals("Container")
                        || no.getTagName().equals("Menu") || no.getTagName().equals("MenuItem"))) continue;
                Class<?> classe = Class.forName(no.getAttribute("class"));
                if (JMenuBar.class.isAssignableFrom(classe))
                    for (Element filho : filhos(no,"SubComponents","Component"))
                        exigir(JMenu.class.isAssignableFrom(Class.forName(filho.getAttribute("class"))),
                                nome+": o GUI Builder aceita apenas menus como filhos declarados de JMenuBar");
                Beans.setDesignTime(true);
                Object prototipo;
                try { prototipo = classe.getConstructor().newInstance(); }
                finally { Beans.setDesignTime(false); }
                prototipos.add(prototipo);
                Field campo = tipo.getDeclaredField(no.getAttribute("name")); campo.setAccessible(true);
                exigir(classe.isInstance(campo.get(tela)),nome + ": componente ausente ou tipo diferente do formulário: " + campo.getName());
                Map<String,PropertyDescriptor> propriedades = new HashMap<>();
                for (PropertyDescriptor p : Introspector.getBeanInfo(classe).getPropertyDescriptors()) propriedades.put(p.getName(),p);
                for (Element p : filhos(no,"Properties","Property")) {
                    String propriedade = p.getAttribute("name");
                    PropertyDescriptor descricao = propriedades.get(propriedade);
                    String setter = "set" + Character.toUpperCase(propriedade.charAt(0)) + propriedade.substring(1);
                    boolean setterPublico = Arrays.stream(classe.getMethods()).anyMatch(m -> m.getName().equals(setter) && m.getParameterCount()==1);
                    exigir(descricao != null || setterPublico, nome + ": propriedade não reconhecida: " + propriedade);
                    // JTableHeader/columnModel antigos são configurações de propriedades filhas, não substituições.
                    if (!propriedade.equals("columnModel") && !propriedade.equals("tableHeader"))
                        exigir(setterPublico,nome+": propriedade sem setter: "+propriedade);
                }
                for (Element e : filhos(no,"Events","EventHandler"))
                    tipo.getDeclaredMethod(e.getAttribute("handler"),Class.forName(e.getAttribute("parameters")));
            }
            Map<String,Integer> executada = hierarquia(tela);
            // Repete apenas o trecho gerado, sem construtor, banco, navegação ou preferências.
            if (tela instanceof Window) ((Window)tela).dispose(); // Libera o peer antes de configurar undecorated.
            tela.getContentPane().removeAll();
            Method iniciar = tipo.getDeclaredMethod("initComponents"); iniciar.setAccessible(true); iniciar.invoke(tela);
            exigir(executada.equals(hierarquia(tela)),nome+": o construtor ainda acrescenta/remove componentes fora do formulário");
        } finally {
            for (Object p : prototipos) if (p instanceof Window) ((Window)p).dispose();
            if (tela instanceof Window) ((Window)tela).dispose();
            else ((JInternalFrame)tela).dispose();
        }
    }

    private static java.util.List<Element> filhos(Element no,String grupo,String tag) {
        java.util.List<Element> encontrados = new ArrayList<>();
        for (Node filho = no.getFirstChild(); filho != null; filho = filho.getNextSibling())
            if (filho instanceof Element && filho.getNodeName().equals(grupo))
                for (Node p=filho.getFirstChild();p!=null;p=p.getNextSibling())
                    if (p instanceof Element && p.getNodeName().equals(tag)) encontrados.add((Element)p);
        return encontrados;
    }
    private static Map<String,Integer> hierarquia(RootPaneContainer tela) {
        Map<String,Integer> encontrados = new TreeMap<>();
        contar(tela.getContentPane(),encontrados);
        JMenuBar menu = tela.getRootPane().getJMenuBar();
        if (menu != null) contar(menu,encontrados);
        return encontrados;
    }
    private static void contar(Component campo,Map<String,Integer> encontrados) {
        encontrados.merge(campo.getClass().getName(),1,Integer::sum);
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JScrollPane
                || campo instanceof JViewport || campo instanceof JMenuBar)
            for (Component filho : ((Container)campo).getComponents()) contar(filho,encontrados);
        if (campo instanceof JMenu)
            for (Component filho : ((JMenu)campo).getMenuComponents()) contar(filho,encontrados);
    }
    private static void exigir(boolean valor,String mensagem) { if (!valor) throw new AssertionError(mensagem); }
}
