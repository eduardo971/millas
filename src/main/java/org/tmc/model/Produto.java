package org.tmc.model;

public class Produto {
    private Long produtoId;
    private String descricaoReduzida;
    private String categoria;
    private Double preco;
    private Double estoqueMinimo;

    public Produto() {}

    public Produto(Long produtoId, String descricaoReduzida, String categoria, Double preco, Double estoqueMinimo) {
        this.produtoId = produtoId;
        this.descricaoReduzida = descricaoReduzida;
        this.categoria = categoria;
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

    public String getCategoriaId() {
        return categoria;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoria = categoriaId;
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
                ", categoriaId=" + categoria +
                ", preco=" + preco +
                ", estoqueMinimo=" + estoqueMinimo +
                '}';
    }
}