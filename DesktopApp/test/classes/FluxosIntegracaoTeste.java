package classes;

import database.*;
import views.*;
import java.awt.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/** Aciona os eventos reais com armazenamento JDBC em memória, sem tocar no banco do usuário. */
public final class FluxosIntegracaoTeste {
    private static final Map<Integer, Map<String,Object>> cargos = new LinkedHashMap<>();
    private static final java.util.List<String> mensagens = new ArrayList<>();
    private static int proximoId, usuariosCadastrados;

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Field conexao=ConexaoBanco.class.getDeclaredField("conexao");conexao.setAccessible(true);
        Object original=conexao.get(null);
        conexao.set(null, proxy(Connection.class,(p,m,a)-> {
            if(m.getName().equals("prepareStatement"))return preparar((String)a[0]);
            return padrao(m.getReturnType());
        }));
        try {
            SwingUtilities.invokeAndWait(()-> {
                try {
                    for(boolean adaptado:new boolean[]{false,true}) {
                        Acessibilidade.restaurarPadrao();
                        if(adaptado) {Acessibilidade.setAltoContrasteAtivo(true);Acessibilidade.setBaixaVisaoAtiva(true);}
                        verificarLogin();verificarUsuarios();verificarCargos();
                    }
                }catch(Exception e){throw new RuntimeException(e);}
                finally { Acessibilidade.restaurarPadrao();for(Window w:Window.getWindows())w.dispose(); }
            });
        } finally {conexao.set(null,original);}
        System.out.println("OK: login válido/inválido, validação de e-mail, cadastro de usuário e CRUD/pesquisa de cargos pelos eventos reais, em padrão e alto contraste com baixa visão; JDBC em memória.");
    }
    private static void verificarLogin() throws Exception {
        TelaLogin login=new TelaLogin();login.setVisible(true);
        texto(login,"tfCampoEmail","teste@mainrh.example");texto(login,"pfCampoSenha","incorreta");
        clicar(login,"btBotaoEntrar");
        exigir(mensagens.stream().anyMatch(s->s.contains("inválidos")),"Login inválido não foi recusado");
        texto(login,"pfCampoSenha","teste");clicar(login,"btBotaoEntrar");
        boolean abriu=false;
        for(Window w:Window.getWindows())if(w instanceof TelaMenu && w.isShowing()){abriu=true;w.dispose();}
        exigir(abriu && !login.isDisplayable(),"Login ADM não abriu o menu");
    }
    private static void verificarUsuarios() throws Exception {
        TelaCadastroUsuarioJd tela=new TelaCadastroUsuarioJd(null,false);
        ((JComboBox<?>)campo(tela,"cbCampoFuncao")).setSelectedIndex(1);
        int antes=usuariosCadastrados;
        texto(tela,"tfCampoEmail","email-invalido");clicar(tela,"jButton1");
        exigir(usuariosCadastrados==antes && mensagens.stream().anyMatch(s->s.contains("E-mail inválido")),"E-mail inválido chegou ao banco");
        texto(tela,"tfCampoNome","Usuário de teste");texto(tela,"tfCampoEmail","novo@mainrh.example");
        texto(tela,"tfCampoCPF","111.222.333-44");texto(tela,"jtfCampoTelefone","(71) 99999-9999");
        texto(tela,"tfCampoDataNascimento","01/01/2000");texto(tela,"pfCampoSenha","teste");texto(tela,"pfCampoConfirmarSenha","teste");
        clicar(tela,"jButton1");
        exigir(usuariosCadastrados==antes+1,"Cadastro válido não chamou o DAO");tela.dispose();
    }
    private static void verificarCargos() throws Exception {
        TelaCadastroJd cadastro=new TelaCadastroJd(null,false);
        texto(cadastro,"tfNomeCargo","Cargo de integração");texto(cadastro,"ffSalario","2500,00");
        texto(cadastro,"tfNivel","Júnior");texto(cadastro,"tfSetor","TI");
        texto(cadastro,"taRequisitos","Java");texto(cadastro,"taAtividade","Desenvolvimento");
        clicar(cadastro,"btCadastrarCargo");
        exigir(cadastro.isCadastroRealizado() && cargos.size()==1,"Cadastro do cargo não foi concluído");
        Cargo cargo=new CargoDao().listar().get(0);
        TelaEditarJd editar=new TelaEditarJd(null,false,cargo);
        texto(editar,"tfEditarNomeCargo","Cargo atualizado");clicar(editar,"btEditarCadastrarCargo");
        exigir(editar.isAlteracaoRealizada() && new CargoDao().buscarPorId(cargo.getId()).getNome().equals("CARGO ATUALIZADO"),"Edição não preservou o vínculo do cargo");
        TelaCargo listagem=new TelaCargo();
        JTextField pesquisa=(JTextField)campo(listagem,"tfPesquisa");
        pesquisa.setText("inexistente");pesquisa.postActionEvent();
        JTable tabela=(JTable)campo(listagem,"tbCargo");exigir(tabela.getRowCount()==0,"Pesquisa não filtrou a tabela");
        pesquisa.setText("atualizado");pesquisa.postActionEvent();exigir(tabela.getRowCount()==1,"Pesquisa não encontrou o cargo editado");
        tabela.setRowSelectionInterval(0,0);clicar(listagem,"btExcluir");
        exigir(cargos.isEmpty() && tabela.getRowCount()==0,"Exclusão não recarregou a listagem");listagem.dispose();
    }
    private static void clicar(Object tela,String nome) throws Exception {
        mensagens.clear();
        javax.swing.Timer responder=new javax.swing.Timer(70,e->{
            for(Window w:Window.getWindows())if(w instanceof JDialog && w.isShowing()) {
                JOptionPane pane=encontrar(((JDialog)w).getContentPane());
                if(pane!=null) {mensagens.add(String.valueOf(pane.getMessage()));pane.setValue(JOptionPane.OK_OPTION);}
            }
        });
        responder.start();
        try {((AbstractButton)campo(tela,nome)).doClick();}finally{responder.stop();}
    }
    private static JOptionPane encontrar(Container c) {
        if(c instanceof JOptionPane)return (JOptionPane)c;
        for(Component filho:c.getComponents())if(filho instanceof Container) {JOptionPane p=encontrar((Container)filho);if(p!=null)return p;}
        return null;
    }
    private static Object campo(Object tela,String nome) throws Exception {Field f=tela.getClass().getDeclaredField(nome);f.setAccessible(true);return f.get(tela);}
    private static void texto(Object tela,String nome,String valor) throws Exception {((JTextComponent)campo(tela,nome)).setText(valor);}
    private static Object preparar(String sql) {
        Map<Integer,Object> parametros=new HashMap<>();String consulta=sql.toUpperCase(Locale.ROOT);
        return proxy(PreparedStatement.class,(p,m,a)-> {
            String nome=m.getName();
            if(nome.startsWith("set") && a!=null && a.length>=2 && a[0] instanceof Integer) {parametros.put((Integer)a[0],a[1]);return null;}
            if(nome.equals("getGeneratedKeys"))return resultado(Collections.singletonList(Map.of("1",proximoId)));
            if(nome.equals("executeUpdate")) {
                if(consulta.contains("INSERT INTO CARGO") || consulta.contains("UPDATE CARGO")) {
                    int id=consulta.contains("INSERT")?++proximoId:(Integer)parametros.get(7);
                    String[] colunas={"nome_cargo","salario_base_cargo","requisitos_cargo","nivel_cargo","setor_cargo","atividades_cargo"};
                    Map<String,Object> linha=new HashMap<>();linha.put("id_cargo_pk",id);
                    for(int i=0;i<colunas.length;i++)linha.put(colunas[i],parametros.get(i+1));cargos.put(id,linha);return 1;
                }
                if(consulta.contains("DELETE FROM CARGO"))return cargos.remove(parametros.get(1))==null?0:1;
                if(consulta.contains("INSERT INTO USUARIO")){usuariosCadastrados++;return 1;}
                throw new AssertionError("Escrita inesperada no teste: "+sql);
            }
            if(nome.equals("executeQuery")) {
                java.util.List<Map<String,Object>> linhas=new ArrayList<>();
                if(consulta.contains("FROM CARGO")) {
                    for(Map<String,Object> linha:cargos.values()) {
                        boolean aceita=consulta.contains("LIKE") ? linha.get("nome_cargo").toString().toLowerCase(Locale.ROOT).contains(parametros.get(1).toString().replace("%","").toLowerCase(Locale.ROOT))
                                : Objects.equals(linha.get("id_cargo_pk"),parametros.get(1));
                        if(aceita)linhas.add(linha);
                    }
                } else if(consulta.contains("FROM USUARIO") && "teste@mainrh.example".equals(parametros.get(1)) && "teste".equals(parametros.get(2))) {
                    linhas.add(Map.of("id_usu_pk",1,"nome_usu","Teste","email_usu",parametros.get(1),"funcao_usu","ADM"));
                } else if(!consulta.contains("FROM USUARIO")) throw new AssertionError("Consulta inesperada no teste: "+sql);
                return resultado(linhas);
            }
            return padrao(m.getReturnType());
        });
    }
    private static Object resultado(java.util.List<Map<String,Object>> linhas) {
        int[] indice={-1};
        return proxy(ResultSet.class,(p,m,a)-> {
            if(m.getName().equals("next"))return ++indice[0]<linhas.size();
            if(m.getName().startsWith("get") && a!=null && a.length==1) {
                Object valor=linhas.get(indice[0]).get(String.valueOf(a[0]));
                if(m.getName().equals("getString"))return valor==null?null:valor.toString();
                return valor==null?padrao(m.getReturnType()):valor;
            }
            return padrao(m.getReturnType());
        });
    }
    private static Object proxy(Class<?> tipo,InvocationHandler handler) {return Proxy.newProxyInstance(FluxosIntegracaoTeste.class.getClassLoader(),new Class<?>[]{tipo},handler);}
    private static Object padrao(Class<?> tipo) {
        if(tipo==boolean.class)return false;if(tipo==int.class)return 0;if(tipo==long.class)return 0L;if(tipo==double.class)return 0d;return null;
    }
    private static void exigir(boolean valor,String mensagem) {if(!valor)throw new AssertionError(mensagem);}
}
