package org.tmc.model;

public class Estoque {
    private Long produtoId;
    private Double qtdeEstoque;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Double getQtdeEstoque() {
        return qtdeEstoque;
    }

    public void setQtdeEstoque(Double qtdeEstoque) {
        this.qtdeEstoque = qtdeEstoque;
    }
}