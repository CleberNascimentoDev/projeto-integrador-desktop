package classes;

import java.awt.Font;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.TableModelListener;

/** Rótulo JavaBean ligado ao modelo; no Design mostra apenas a quantidade de exemplo. */
public class ContadorRegistros extends JLabel {
    private JTable tabela;
    private String singular = "registro", plural = "registros";
    private final TableModelListener alteracaoModelo = e -> atualizar();
    private final PropertyChangeListener trocaModelo = e -> {
        ((javax.swing.table.TableModel)e.getOldValue()).removeTableModelListener(alteracaoModelo);
        ((javax.swing.table.TableModel)e.getNewValue()).addTableModelListener(alteracaoModelo);
        atualizar();
    };
    public ContadorRegistros() {
        setFont(PainelListagem.fonte(Font.PLAIN, 16));
        setForeground(PainelListagem.cinza);
        atualizar();
    }
    public JTable getTabela() { return tabela; }
    public void setTabela(JTable valor) {
        if (tabela != null) {
            tabela.getModel().removeTableModelListener(alteracaoModelo);
            tabela.removePropertyChangeListener("model", trocaModelo);
        }
        tabela = valor;
        if (tabela != null) {
            tabela.getModel().addTableModelListener(alteracaoModelo);
            tabela.addPropertyChangeListener("model", trocaModelo);
        }
        atualizar();
    }
    public String getSingular() { return singular; }
    public void setSingular(String valor) { singular = valor; atualizar(); }
    public String getPlural() { return plural; }
    public void setPlural(String valor) { plural = valor; atualizar(); }
    private void atualizar() {
        int total = tabela == null ? 0 : tabela.getRowCount();
        setText(total + " " + (total == 1 ? singular : plural));
    }
}
