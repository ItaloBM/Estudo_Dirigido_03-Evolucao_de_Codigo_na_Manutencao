package refatorado;

/**
 * Enum que encapsula os níveis de prioridade de um incidente,
 * substituindo números mágicos por constantes simbólicas.
 * Cada nível contém o SLA padrão (em horas), o SLA urgente,
 * o valor-base de cobrança e o multiplicador de urgência.
 */
public enum NivelPrioridade {

    CRITICO(4, 2, 500.0, 2.0),
    ALTO(8, 4, 300.0, 1.5),
    MEDIO(24, 12, 150.0, 1.5),
    BAIXO(48, 48, 75.0, 1.0);

    private final int slaPadraoHoras;
    private final int slaUrgenteHoras;
    private final double valorBase;
    private final double multiplicadorUrgencia;

    NivelPrioridade(int slaPadraoHoras, int slaUrgenteHoras,
                     double valorBase, double multiplicadorUrgencia) {
        this.slaPadraoHoras = slaPadraoHoras;
        this.slaUrgenteHoras = slaUrgenteHoras;
        this.valorBase = valorBase;
        this.multiplicadorUrgencia = multiplicadorUrgencia;
    }

    public int getSlaPadraoHoras() {
        return slaPadraoHoras;
    }

    public int getSlaUrgenteHoras() {
        return slaUrgenteHoras;
    }

    public double getValorBase() {
        return valorBase;
    }

    public double getMultiplicadorUrgencia() {
        return multiplicadorUrgencia;
    }

    /**
     * Determina o nível de prioridade com base no impacto informado.
     *
     * @param impacto Valor de 1 a 10 que representa o impacto do incidente.
     * @return NivelPrioridade correspondente.
     */
    public static NivelPrioridade avaliarPorImpacto(int impacto) {
        if (impacto >= 8) return CRITICO;
        if (impacto >= 5) return ALTO;
        if (impacto >= 3) return MEDIO;
        return BAIXO;
    }
}
