package org.tmc.model;

public class Produto {
    private Long produtoId;
    private String descricaoReduzida;
    private Long categoriaId;
    private Double preco;
    private Double estoqueMinimo;

    public Produto() {}

    public Produto(Long produtoId, String descricaoReduzida, Long categoriaId, Double preco, Double estoqueMinimo) {
        this.produtoId = produtoId;
        this.descricaoReduzida = descricaoReduzida;
        this.categoriaId = categoriaId;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "produtoId=" + produtoId +
                ", descricaoReduzida='" + descricaoReduzida + '\'' +
                ", categoriaId=" + categoriaId +
                ", preco=" + preco +
                ", estoqueMinimo=" + estoqueMinimo +
                '}';
    }
}