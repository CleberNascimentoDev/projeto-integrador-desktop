package classes;

import javax.swing.JTable;

/** Renderizadores da aplicação disponíveis também na prévia do formulário. */
public class TabelaPadrao extends JTable {
    private boolean recrutadores;
    public TabelaPadrao() { atualizarEstilo(); }
    public boolean isRecrutadores() { return recrutadores; }
    public void setRecrutadores(boolean valor) { recrutadores = valor; atualizarEstilo(); }
    private void atualizarEstilo() { PainelListagem.configurarTabela(this, null, recrutadores); }
}
