package refatorado;

import java.time.LocalDateTime;

/**
 * Objeto de valor (Value Object) que representa um chamado de suporte.
 * Substitui o uso de arrays genéricos (Object/String[]) por um modelo
 * tipado, seguro e autoexplicativo.
 */
public class Chamado {

    private final String titulo;
    private final String descricao;
    private final String usuario;
    private final int impacto;
    private final TipoChamado tipo;
    private final boolean urgente;
    private final LocalDateTime dataAbertura;

    public Chamado(String titulo, String descricao, String usuario,
                   int impacto, TipoChamado tipo, boolean urgente) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.usuario = usuario;
        this.impacto = impacto;
        this.tipo = tipo;
        this.urgente = urgente;
        this.dataAbertura = LocalDateTime.now();
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getUsuario() { return usuario; }
    public int getImpacto() { return impacto; }
    public TipoChamado getTipo() { return tipo; }
    public boolean isUrgente() { return urgente; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
}
