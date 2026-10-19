/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package views;

import classes.ProcessoSeletivo;
import database.ProcessoSeletivoDao;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;



/**
 *
 * @author GabrielaCarvalhodeSo
 */
public class TelaProcessoSeletivo extends javax.swing.JInternalFrame {

    /**
     * Creates new form TelaProcessoSeletivo
     */
   
    public TelaProcessoSeletivo() {
       initComponents();
       classes.Acessibilidade.configurarTela(this);
       customizarTabela();
       configurarBuscaDinamica();
       carregarTabela("");
       configurarAtalhos();
       
        java.awt.EventQueue.invokeLater(() -> {
        if (classes.Acessibilidade.isTecladoAtivo()) btVoltar.requestFocusInWindow();
        acessibilidade();
        });
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
    }
    
    
    
    
        private void configurarAtalhos() {
        javax.swing.InputMap inputMap = this.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap actionMap = this.getRootPane().getActionMap();

        // 1.1 Atalho para Cadastrar: Ctrl + N
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK), "cadastrar");
        actionMap.put("cadastrar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btCadastrar.doClick(); 
            }
        });

        // 1.2 Atalho para Editar: Ctrl + E
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK), "editar");
        actionMap.put("editar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Dispara o clique diretamente; a validação e o JOptionPane já ocorrem dentro da ação do botão
                btEditar.doClick();
            }
        });

        // 1.3 Atalho para Excluir: Ctrl + D
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK), "excluir");
        actionMap.put("excluir", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Dispara o clique diretamente; a validação e o JOptionPane já ocorrem dentro da ação do botão
                btExcluir.doClick();
            }
        });
        
        btCadastrar.setToolTipText(btCadastrar.getToolTipText() + " (Ctrl + N)");
        btEditar.setToolTipText(btEditar.getToolTipText() + " (Ctrl + E)");
        btExcluir.setToolTipText(btExcluir.getToolTipText() + " (Ctrl + D)");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void acessibilidade(){
        javax.swing.InputMap mapa = tbProcessos.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object tabOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("TAB"));
        Object shiftOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"));
        
        // 1. Permite que a tabela receba o foco ao navegar com o TAB
        tbProcessos.setFocusable(true);

        // 2. Altera o comportamento do TAB dentro da tabela para APENAS passar o foco adiante
        tbProcessos.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("TAB"), "proximoCampo");

        tbProcessos.getActionMap().put("proximoCampo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbProcessos.transferFocus(); 
            }
        });

        // 3. Faz o mesmo para o Shift+TAB voltar para o campo anterior
        tbProcessos.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), "campoAnterior");

        tbProcessos.getActionMap().put("campoAnterior", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbProcessos.transferFocusBackward(); 
            }
        });

        // 4. Controla visual (Borda de Foco) e seleção baseado no ganho/perda de foco
        javax.swing.border.Border bordaFoco = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 215), 3);
        javax.swing.border.Border bordaSpOriginal = spBarra.getBorder();

        java.awt.event.FocusAdapter controladorDeFoco = new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                java.awt.Component fonte = (java.awt.Component) e.getSource();

                if (fonte == btCadastrar) {
                    tbProcessos.clearSelection();
                }

                // Adiciona a borda de destaque visualmente
                if (fonte == tbProcessos) {
                    spBarra.setBorder(bordaFoco);
                    if (classes.Acessibilidade.isTecladoAtivo() && tbProcessos.getRowCount() > 0 && tbProcessos.getSelectedRow() == -1) {
                        tbProcessos.setRowSelectionInterval(0, 0);
                    }
                } else if (fonte instanceof javax.swing.JButton) {
                    javax.swing.JButton btn = (javax.swing.JButton) fonte;
                    if (btn.getClientProperty("bordaOriginal") == null) {
                        btn.putClientProperty("bordaOriginal", btn.getBorder());
                    }
                    btn.setBorder(classes.PainelListagem.criarBordaFoco(btn, (javax.swing.border.Border) btn.getClientProperty("bordaOriginal")));
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                java.awt.Component fonte = (java.awt.Component) e.getSource();

                // Restaura a borda original de quem perdeu o foco
                if (fonte == tbProcessos) {
                    spBarra.setBorder(bordaSpOriginal);
                } else if (fonte instanceof javax.swing.JButton) {
                    javax.swing.JButton btn = (javax.swing.JButton) fonte;
                    javax.swing.border.Border original = (javax.swing.border.Border) btn.getClientProperty("bordaOriginal");
                    if (original != null) {
                        btn.setBorder(original);
                    }
                }

                // A mudança de foco não altera a linha selecionada.
            }
        };

        tbProcessos.addFocusListener(controladorDeFoco);
        btEditar.addFocusListener(controladorDeFoco);
        btExcluir.addFocusListener(controladorDeFoco);
        btCadastrar.addFocusListener(controladorDeFoco);
        btVoltar.addFocusListener(controladorDeFoco);

        preferenciaTeclado = e -> aplicarNavegacao(mapa, tabOriginal, shiftOriginal);
        classes.Acessibilidade.adicionarListener(preferenciaTeclado);
        aplicarNavegacao(mapa, tabOriginal, shiftOriginal);
    }
    
    private java.beans.PropertyChangeListener preferenciaTeclado;

    private void aplicarNavegacao(javax.swing.InputMap mapa, Object tabOriginal, Object shiftOriginal) {
        boolean ativo = classes.Acessibilidade.isTecladoAtivo();
        mapa.put(javax.swing.KeyStroke.getKeyStroke("TAB"), ativo ? "proximoCampo" : tabOriginal);
        mapa.put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), ativo ? "campoAnterior" : shiftOriginal);
        botaoAjuda.setFocusPainted(ativo || classes.Acessibilidade.isBaixaVisaoAtiva());
    }

    @Override public void dispose() {
        if (preferenciaTeclado != null) classes.Acessibilidade.removerListener(preferenciaTeclado);
        super.dispose();
    }
    
    
    
    public void carregarTabela(String termo) {
        DefaultTableModel model = (DefaultTableModel) tbProcessos.getModel();
        model.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            ProcessoSeletivoDao dao = new ProcessoSeletivoDao();
            List<ProcessoSeletivo> lista = dao.listar(termo);

            for (ProcessoSeletivo p : lista) {
                String strInicio = (p.getDataInicio() != null) ? p.getDataInicio().format(dtf) : "-";
                String strFim = (p.getDataFim() != null) ? p.getDataFim().format(dtf) : "-";

                model.addRow(new Object[]{
                    p.getNomeProcesso(),
                    strInicio,
                    strFim
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar a lista de processos: " + e.getMessage(),
                "Erro no Banco de Dados",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarBuscaDinamica() {
        tfPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                carregarTabela(tfPesquisa.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                carregarTabela(tfPesquisa.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                carregarTabela(tfPesquisa.getText());
            }
        });
    }
    
    
     private void customizarTabela() {
        classes.PainelListagem.configurarTabela(tbProcessos, spBarra, false);
    }

  
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelListagem = new classes.PainelListagem();
        btVoltar = new classes.BotaoPadrao();
        painelCabecalho = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        botaoAjuda = new classes.BotaoAjuda();
        lbDescricao = new javax.swing.JLabel();
        painelFerramentas = new classes.PainelFerramentas();
        painelBusca = new classes.PainelBusca();
        lbLupa = new classes.RotuloLupa();
        tfPesquisa = new classes.CampoPesquisa();
        btExcluir = new classes.BotaoPadrao();
        btEditar = new classes.BotaoPadrao();
        btCadastrar = new classes.BotaoPadrao();
        painelTabela = new classes.PainelArredondado();
        painelTituloTabela = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbQuantidade = new classes.ContadorRegistros();
        spBarra = new javax.swing.JScrollPane();
        tbProcessos = new classes.TabelaPadrao();
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelListagem.setBorder(javax.swing.BorderFactory.createEmptyBorder(28,40,40,40));
        painelListagem.setLayout(new java.awt.GridBagLayout());
        btVoltar.setFont(new java.awt.Font("Segoe UI",1,17));
        btVoltar.setText("‹  Voltar");
        btVoltar.setPreferredSize(new java.awt.Dimension(130, 48));
        btVoltar.addActionListener(this::btVoltarActionPerformed);
        painelListagem.add(btVoltar,new java.awt.GridBagConstraints(0,0,1,1,0,0.0,18,0,new java.awt.Insets(0,0,24,0),0,0));
        painelCabecalho.setOpaque(false);
        painelCabecalho.setLayout(new java.awt.BorderLayout(16,0));
        jLabel1.setBackground(new java.awt.Color(29,45,68));
        jLabel1.setText("Processos Seletivos");
        jLabel1.setFont(new java.awt.Font("Segoe UI",1,36));
        jLabel1.setForeground(new java.awt.Color(0,0,0));
        painelCabecalho.add(jLabel1,java.awt.BorderLayout.CENTER);
        botaoAjuda.setTextoAjuda("Esta é a tela de gerenciamento de processos seletivos do sistema MAINRH, destinada à consulta e controle de seleções ativas e encerradas. Utilize o campo de busca superior para localizar um processo específico. Na tabela Listas de processos seletivos, você pode visualizar os nomes dos processos cadastrados juntamente com suas respectivas Data de início e Data de fim. Para gerenciar as informações, selecione um item da lista e clique no botão Editar para alterar seus dados, ou clique no botão Cadastrar para abrir o formulário de criação de um novo processo seletivo. Para retornar à tela anterior, utilize o botão Voltar no canto superior esquerdo.");
        botaoAjuda.setPreferredSize(new java.awt.Dimension(42, 42));
        painelCabecalho.add(botaoAjuda,java.awt.BorderLayout.EAST);
        painelListagem.add(painelCabecalho,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,4,0),0,0));
        lbDescricao.setText("Consulte e gerencie os processos seletivos.");
        lbDescricao.setFont(new java.awt.Font("Segoe UI",0,19));
        lbDescricao.setForeground(new java.awt.Color(64,64,64));
        painelListagem.add(lbDescricao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,28,0),0,0));
        painelFerramentas.setLayout(new java.awt.FlowLayout(0,16,0));
        painelBusca.setLayout(new java.awt.BorderLayout(16,0));
        painelBusca.add(lbLupa,java.awt.BorderLayout.WEST);
        tfPesquisa.setPlaceholder("Buscar processo...");
        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusGained(java.awt.event.FocusEvent evt) { tfPesquisaFocusGained(evt); } });
        tfPesquisa.addActionListener(this::tfPesquisaActionPerformed);
        painelBusca.add(tfPesquisa,java.awt.BorderLayout.CENTER);
        painelFerramentas.add(painelBusca);
        btExcluir.setToolTipText("Selecione um cargo da tabela e clique aqui caso deseje excluir o cargo");
        btExcluir.setFont(new java.awt.Font("Segoe UI",0,18));
        btExcluir.setText("Excluir");
        btExcluir.addActionListener(this::btExcluirActionPerformed);
        painelFerramentas.add(btExcluir);
        btEditar.setFont(new java.awt.Font("Segoe UI",1,18));
        btEditar.setText("Editar");
        btEditar.addActionListener(this::btEditarActionPerformed);
        painelFerramentas.add(btEditar);
        btCadastrar.setToolTipText("Clique aqui para cadastrar um novo cargo");
        btCadastrar.setFont(new java.awt.Font("Segoe UI",1,18));
        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(this::btCadastrarActionPerformed);
        painelFerramentas.add(btCadastrar);
        painelListagem.add(painelFerramentas,new java.awt.GridBagConstraints(0,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,30,0),0,0));
        painelTabela.setMinimumSize(new java.awt.Dimension(0, 0));
        painelTabela.setLayout(new java.awt.BorderLayout(0,18));
        painelTituloTabela.setOpaque(false);
        painelTituloTabela.setLayout(new java.awt.BorderLayout(16,0));
        jLabel3.setBackground(new java.awt.Color(29,45,68));
        jLabel3.setText("Listas de processos seletivos ");
        jLabel3.setFont(new java.awt.Font("Segoe UI",1,30));
        jLabel3.setForeground(new java.awt.Color(0,0,0));
        painelTituloTabela.add(jLabel3,java.awt.BorderLayout.WEST);
        lbQuantidade.setSingular("processo");
        lbQuantidade.setPlural("processos");
        painelTituloTabela.add(lbQuantidade,java.awt.BorderLayout.EAST);
        painelTabela.add(painelTituloTabela,java.awt.BorderLayout.NORTH);
        spBarra.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230,235,240),1,true));
        tbProcessos.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},new String[]{"Processos","Data de início","Data de fim"}) { @Override public boolean isCellEditable(int row,int column) { return false; } });
        tbProcessos.setToolTipText("Aqui aparecem os cargos já cadastrados em sistema");
        tbProcessos.setShowGrid(true);
        spBarra.setViewportView(tbProcessos);
        painelTabela.add(spBarra,java.awt.BorderLayout.CENTER);
        painelListagem.add(painelTabela,new java.awt.GridBagConstraints(0,4,1,1,1.0,1,18,1,new java.awt.Insets(0,0,0,0),0,0));
        getContentPane().add(painelListagem,java.awt.BorderLayout.CENTER);
        lbQuantidade.setTabela(tbProcessos);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfPesquisaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPesquisaFocusGained
        tfPesquisa.setForeground(java.awt.Color.BLACK);
    }//GEN-LAST:event_tfPesquisaFocusGained

    private void tfPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPesquisaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPesquisaActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        int linhaSelecionada = tbProcessos.getSelectedRow();

            if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione um processo seletivo na tabela para excluir.",
                "Nenhum Item Selecionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ProcessoSeletivoDao dao = new ProcessoSeletivoDao();
            List<ProcessoSeletivo> lista = dao.listar(tfPesquisa.getText());
            ProcessoSeletivo processoSelecionado = lista.get(linhaSelecionada);

            int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o processo seletivo \"" + processoSelecionado.getNomeProcesso() + "\"?\nEsta ação não poderá ser desfeita.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacao == JOptionPane.YES_OPTION) {
                boolean excluiu = dao.excluir(processoSelecionado.getIdProcesso());

                if (excluiu) {
                    JOptionPane.showMessageDialog(this,
                        "Processo Seletivo excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarTabela("");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Não foi possível excluir o processo seletivo.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this,
                    "Não é possível excluir este processo seletivo pois ele possui candidatos ou etapas vinculadas.",
                    "Aviso de Restrição",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir do banco de dados: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }    
    }//GEN-LAST:event_btExcluirActionPerformed

    
    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        TelaCadastrarProcessoJd cadastrar = new TelaCadastrarProcessoJd(null, true, this);
        cadastrar.setVisible(true);
    }                                            

    private void btEditarActionPerformed(java.awt.event.ActionEvent evt) {                                         
        int linhaSelecionada = tbProcessos.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecione um processo seletivo na tabela para editar.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ProcessoSeletivoDao dao = new ProcessoSeletivoDao();
            List<ProcessoSeletivo> lista = dao.listar(tfPesquisa.getText());
            ProcessoSeletivo processoSelecionado = lista.get(linhaSelecionada);

            TelaEditarProcessoJd editar = new TelaEditarProcessoJd(null, true, this, processoSelecionado);
            editar.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados para edição: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }                                        

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {                                         
        this.dispose();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.BotaoAjuda botaoAjuda;
    private classes.BotaoPadrao btCadastrar;
    private classes.BotaoPadrao btEditar;
    private classes.BotaoPadrao btExcluir;
    private classes.BotaoPadrao btVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lbDescricao;
    private classes.RotuloLupa lbLupa;
    private classes.ContadorRegistros lbQuantidade;
    private classes.PainelBusca painelBusca;
    private javax.swing.JPanel painelCabecalho;
    private classes.PainelFerramentas painelFerramentas;
    private classes.PainelListagem painelListagem;
    private classes.PainelArredondado painelTabela;
    private javax.swing.JPanel painelTituloTabela;
    private javax.swing.JScrollPane spBarra;
    private classes.TabelaPadrao tbProcessos;
    private classes.CampoPesquisa tfPesquisa;
    //GEN-END:variables
}
