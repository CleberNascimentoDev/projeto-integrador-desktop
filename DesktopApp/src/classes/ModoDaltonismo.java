package classes;

/** Adaptações de cores; não são filtros de simulação visual. */
public enum ModoDaltonismo {
    PADRAO("Padrão"), PROTANOPIA("Protanopia"), DEUTERANOPIA("Deuteranopia"), TRITANOPIA("Tritanopia");

    private final String nome;
    ModoDaltonismo(String nome) { this.nome = nome; }
    @Override public String toString() { return nome; }
}
