package classes;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Imagem com recurso e dimensões editáveis no formulário, preservando a paleta acessível. */
public class RotuloImagem extends JLabel {
    private String recurso = "/images/Logo.png";
    private int larguraImagem = 337, alturaImagem = 110;
    public RotuloImagem() {
        setHorizontalAlignment(SwingConstants.CENTER);
        putClientProperty("baixaVisao.logo", true);
        atualizarImagem();
    }
    public String getRecurso() { return recurso; }
    public void setRecurso(String valor) { recurso = valor; atualizarImagem(); }
    public int getLarguraImagem() { return larguraImagem; }
    public void setLarguraImagem(int valor) { larguraImagem = Math.max(1,valor); atualizarImagem(); }
    public int getAlturaImagem() { return alturaImagem; }
    public void setAlturaImagem(int valor) { alturaImagem = Math.max(1,valor); atualizarImagem(); }
    public boolean isAmpliarBaixaVisao() { return Boolean.TRUE.equals(getClientProperty("baixaVisao.logo")); }
    public void setAmpliarBaixaVisao(boolean valor) { putClientProperty("baixaVisao.logo",valor); }
    private void atualizarImagem() { setIcon(new IconeImagem(recurso,larguraImagem,alturaImagem)); }
}
