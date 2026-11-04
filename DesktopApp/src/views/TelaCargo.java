/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package views;

import classes.Cargo;
import database.CargoDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author RaphaelBispoIssa
 */
public class TelaCargo extends javax.swing.JInternalFrame {

    private final CargoDao cargoDao = new CargoDao();
    private List<Cargo> cargosExibidos = new ArrayList<>();

    /**
     * Creates new form TelaCargo
     */
    public TelaCargo() {
        initComponents();
        classes.Acessibilidade.configurarTela(this);
        customizarTabela();
        configurarPesquisa();
        carregarCargos();
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
        javax.swing.InputMap mapa = tbCargo.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object tabOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("TAB"));
        Object shiftOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"));
        
        // 1. Permite que a tabela receba o foco ao navegar com o TAB
        tbCargo.setFocusable(true);

        // 2. Altera o comportamento do TAB dentro da tabela para APENAS passar o foco adiante
        tbCargo.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("TAB"), "proximoCampo");

        tbCargo.getActionMap().put("proximoCampo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbCargo.transferFocus(); 
            }
        });

        // 3. Faz o mesmo para o Shift+TAB voltar para o campo anterior
        tbCargo.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), "campoAnterior");

        tbCargo.getActionMap().put("campoAnterior", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbCargo.transferFocusBackward(); 
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
                    tbCargo.clearSelection();
                }

                // Adiciona a borda de destaque visualmente
                if (fonte == tbCargo) {
                    spBarra.setBorder(bordaFoco);
                    if (classes.Acessibilidade.isTecladoAtivo() && tbCargo.getRowCount() > 0 && tbCargo.getSelectedRow() == -1) {
                        tbCargo.setRowSelectionInterval(0, 0);
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
                if (fonte == tbCargo) {
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

        tbCargo.addFocusListener(controladorDeFoco);
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
    
    
    
    
    
    
    
    private void configurarPesquisa() {
        tfPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                carregarCargos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                carregarCargos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                carregarCargos();
            }
        });
    }

    private void carregarCargos() {
        String pesquisa = tfPesquisa.getText().trim();
        if ("Digite aqui...".equalsIgnoreCase(pesquisa)) {
            pesquisa = "";
        }

        try {
            cargosExibidos = cargoDao.buscarPorNome(pesquisa);
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"Cargos"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Cargo cargo : cargosExibidos) {
                modelo.addRow(new Object[]{cargo.getNome()});
            }
            tbCargo.setModel(modelo);
            customizarTabela();
        } catch (SQLException e) {
            mostrarErroBanco("carregar os cargos", e);
        }
    }

    private Cargo obterCargoSelecionado() {
        int linhaSelecionada = tbCargo.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cargo na tabela.",
                    "Cargo não selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        int linhaModelo = tbCargo.convertRowIndexToModel(linhaSelecionada);
        if (linhaModelo < 0 || linhaModelo >= cargosExibidos.size()) {
            return null;
        }
        return cargosExibidos.get(linhaModelo);
    }

    private void mostrarErroBanco(String operacao, SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                "Erro ao " + operacao + ":\n" + e.getMessage(),
                "Erro no banco de dados",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    private void customizarTabela() {
        classes.PainelListagem.configurarTabela(tbCargo, spBarra, false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelDesktopPane = new classes.DesktopListagem();
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
        tbCargo = new classes.TabelaPadrao();
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelDesktopPane.setLayout(new java.awt.BorderLayout(0,0));
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
        jLabel1.setText("Cargos");
        jLabel1.setFont(new java.awt.Font("Segoe UI",1,36));
        jLabel1.setForeground(new java.awt.Color(0,0,0));
        painelCabecalho.add(jLabel1,java.awt.BorderLayout.CENTER);
        botaoAjuda.setTextoAjuda("Esta é a tela de gerenciamento de cargos do sistema MAINRH, utilizada para visualizar e administrar as posições cadastradas. Utilize a barra de pesquisa no topo para filtrar cargos pelo nome. Na tabela **Cargos cadastrados**, selecione um item da lista para realizar ações específicas: clique em **Editar** para alterar as informações do cargo, em **Excluir** para removê-lo do sistema, ou no botão **Cadastrar** para abrir o formulário de inclusão de uma nova posição. Para retornar à tela anterior, clique no botão **Voltar** no canto superior esquerdo.");
        botaoAjuda.setPreferredSize(new java.awt.Dimension(42, 42));
        painelCabecalho.add(botaoAjuda,java.awt.BorderLayout.EAST);
        painelListagem.add(painelCabecalho,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,4,0),0,0));
        lbDescricao.setText("Consulte e gerencie os cargos cadastrados.");
        lbDescricao.setFont(new java.awt.Font("Segoe UI",0,19));
        lbDescricao.setForeground(new java.awt.Color(64,64,64));
        painelListagem.add(lbDescricao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,28,0),0,0));
        painelFerramentas.setLayout(new java.awt.FlowLayout(0,16,0));
        painelBusca.setLayout(new java.awt.BorderLayout(16,0));
        painelBusca.add(lbLupa,java.awt.BorderLayout.WEST);
        tfPesquisa.setPlaceholder("Buscar cargo...");
        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusGained(java.awt.event.FocusEvent evt) { tfPesquisaFocusGained(evt); } });
        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusLost(java.awt.event.FocusEvent evt) { tfPesquisaFocusLost(evt); } });
        tfPesquisa.addActionListener(this::tfPesquisaActionPerformed);
        painelBusca.add(tfPesquisa,java.awt.BorderLayout.CENTER);
        painelFerramentas.add(painelBusca);
        btExcluir.setToolTipText("Selecione um cargo da tabela e clique aqui caso deseje excluir o cargo");
        btExcluir.setFont(new java.awt.Font("Segoe UI",0,18));
        btExcluir.setText("Excluir");
        btExcluir.addActionListener(this::btExcluirActionPerformed);
        painelFerramentas.add(btExcluir);
        btEditar.setToolTipText("Selecione um cargo da tabela e clique aqui caso deseje editar o cargo");
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
        jLabel3.setText("Cargos cadastrados");
        jLabel3.setFont(new java.awt.Font("Segoe UI",1,30));
        jLabel3.setForeground(new java.awt.Color(0,0,0));
        painelTituloTabela.add(jLabel3,java.awt.BorderLayout.WEST);
        lbQuantidade.setSingular("cargo");
        lbQuantidade.setPlural("cargos");
        painelTituloTabela.add(lbQuantidade,java.awt.BorderLayout.EAST);
        painelTabela.add(painelTituloTabela,java.awt.BorderLayout.NORTH);
        spBarra.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230,235,240),1,true));
        tbCargo.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},new String[]{"Cargos"}) { @Override public boolean isCellEditable(int row,int column) { return false; } });
        tbCargo.setToolTipText("Aqui aparecem os cargos já cadastrados em sistema");
        tbCargo.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent evt) { tbCargoMouseClicked(evt); } });
        spBarra.setViewportView(tbCargo);
        painelTabela.add(spBarra,java.awt.BorderLayout.CENTER);
        painelListagem.add(painelTabela,new java.awt.GridBagConstraints(0,4,1,1,1.0,1,18,1,new java.awt.Insets(0,0,0,0),0,0));
        painelDesktopPane.add(painelListagem,java.awt.BorderLayout.CENTER);
        getContentPane().add(painelDesktopPane,java.awt.BorderLayout.CENTER);
        lbQuantidade.setTabela(tbCargo);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        TelaCadastroJd cadastro = new TelaCadastroJd(null, true);
        cadastro.setVisible(true);
        if (cadastro.isCadastroRealizado()) {
            carregarCargos();
        }
    }//GEN-LAST:event_btCadastrarActionPerformed
                       
    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        Cargo cargo = obterCargoSelecionado();
        if (cargo == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cargo \"" + cargo.getNome() + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (cargoDao.excluir(cargo.getId())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cargo excluído com sucesso!",
                        "Exclusão de cargo",
                        JOptionPane.INFORMATION_MESSAGE
                );
                carregarCargos();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "O cargo não foi encontrado.",
                        "Exclusão de cargo",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Este cargo está vinculado a um processo seletivo e não pode ser excluído.",
                        "Cargo em uso",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                mostrarErroBanco("excluir o cargo", e);
            }
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void tfPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPesquisaActionPerformed
        carregarCargos();
    }//GEN-LAST:event_tfPesquisaActionPerformed


    private void tfPesquisaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPesquisaFocusGained
        tfPesquisa.setForeground(java.awt.Color.BLACK);
    }//GEN-LAST:event_tfPesquisaFocusGained

    
        
    private void btEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditarActionPerformed
        Cargo cargo = obterCargoSelecionado();
        if (cargo == null) {
            return;
        }

        TelaEditarJd editar = new TelaEditarJd(null, true, cargo);
        editar.setVisible(true);
        if (editar.isAlteracaoRealizada()) {
            carregarCargos();
        }
    }//GEN-LAST:event_btEditarActionPerformed
    

    private void tbCargoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCargoMouseClicked
        if (evt.getClickCount() == 2
                && evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            Cargo cargoSelecionado = obterCargoSelecionado();
            if (cargoSelecionado == null) {
                return;
            }

            try {
                Cargo cargo = cargoDao.buscarPorId(cargoSelecionado.getId());
                if (cargo == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "O cargo selecionado não foi encontrado.",
                            "Cargo não encontrado",
                            JOptionPane.WARNING_MESSAGE
                    );
                    carregarCargos();
                    return;
                }

                TelaDetalhesCargo telaDetalhe = new TelaDetalhesCargo(cargo);
                painelDesktopPane.add(telaDetalhe, javax.swing.JLayeredPane.PALETTE_LAYER);
                telaDetalhe.setSize(Math.min(telaDetalhe.getWidth(), painelDesktopPane.getWidth()),
                        Math.min(telaDetalhe.getHeight(), painelDesktopPane.getHeight()));
                telaDetalhe.setLocation(Math.max(0, (painelDesktopPane.getWidth() - telaDetalhe.getWidth()) / 2),
                        Math.max(0, (painelDesktopPane.getHeight() - telaDetalhe.getHeight()) / 2));
                telaDetalhe.setVisible(true);
                telaDetalhe.setSelected(true);
                telaDetalhe.toFront();
            } catch (SQLException e) {
                mostrarErroBanco("consultar os detalhes do cargo", e);
            } catch (java.beans.PropertyVetoException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível abrir os detalhes do cargo.",
                        "Erro ao abrir tela",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }//GEN-LAST:event_tbCargoMouseClicked

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        this.dispose();       // TODO add your handling code here:
    }//GEN-LAST:event_btVoltarActionPerformed

    private void tfPesquisaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPesquisaFocusLost
        tfPesquisa.setForeground(tfPesquisa.getText().isEmpty() ? java.awt.Color.GRAY : java.awt.Color.BLACK);
    }//GEN-LAST:event_tfPesquisaFocusLost


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
    private classes.DesktopListagem painelDesktopPane;
    private classes.PainelFerramentas painelFerramentas;
    private classes.PainelListagem painelListagem;
    private classes.PainelArredondado painelTabela;
    private javax.swing.JPanel painelTituloTabela;
    private javax.swing.JScrollPane spBarra;
    private classes.TabelaPadrao tbCargo;
    private classes.CampoPesquisa tfPesquisa;
    //GEN-END:variables
}
