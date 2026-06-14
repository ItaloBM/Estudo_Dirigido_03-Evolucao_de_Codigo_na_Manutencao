package refatorado;

/**
 * Enum que representa os tipos de chamado de suporte,
 * encapsulando o SLA padrão e o valor-base de cada categoria.
 */
public enum TipoChamado {

    INCIDENTE("Incidente"),
    REQUISICAO("Requisição de Serviço"),
    MUDANCA("Requisição de Mudança"),
    DESCONHECIDO("Tipo Desconhecido");

    private final String descricao;

    TipoChamado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
