package org.tmc.model;

public class Produto {
    private Long produtoId;
    private String descricao;
    private String categoria;
    private Double preco;
    private Double estoqueMinimo;

    public Produto() {}

    public Produto(Long produtoId, String descricao, String categoria, Double preco, Double estoqueMinimo) {
        this.produtoId = produtoId;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setdescricao(String descricao) {
        this.descricao = descricao;
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
                ", descricao='" + descricao + '\'' +
                ", categoriaId=" + categoria +
                ", preco=" + preco +
                ", estoqueMinimo=" + estoqueMinimo +
                '}';
    }
}