package org.tmc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tmc.model.Categoria;
import org.tmc.model.Estoque;
import org.tmc.model.Produto;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private static final String API_URL = "https://apiexternal.mobne.com.br/api/v1/Produto/consulta-cadastro-produto";
    private static final String API_KEY = "ApiKey 6lrFHzvTsSl+Sowjd8ztRv3uqdaGuqL+UhbVh09bqhLsh9YNg9uaaY0RCDCEprIpATRXMN2PC1Q5aNeCE8u4mw==";
    private static final String EMPRESA_ID = "675";
    private static final String STATUS_VENDA = "A";
    private static final String NUMERO_EMPRESA = "2";

    private final RestTemplate restTemplate;

    public ProdutoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Produto> buscarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        int pageNumber = 1;
        int pageSize = 100; // Ajuste conforme necessário
        boolean hasMorePages = true;
        int totalPages = 1; // Inicializa com 1

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", API_KEY);
        headers.set("Accept", "application/json");

        List<Categoria> categoriaList = consultarCategorias();
        Map<Long, String> categoriaPorId = categoriaList.stream()
                .collect(Collectors.toMap(Categoria::getCategoriaId, Categoria::getCategoria));

        // List<Estoque> estoqueList = consultarEstoque();
        // Map<Long, Double> estoquePorProdutoId = estoqueList.stream()
        //         .collect(Collectors.toMap(Estoque::getProdutoId, Estoque::getQtdeEstoque));

        while (pageNumber <= totalPages && hasMorePages) {
            String url = API_URL + "?Filter.EmpresaId=" + EMPRESA_ID
                    + "&Filter.StatusVenda=" + STATUS_VENDA
                    + "&PageSize=" + pageSize
                    + "&PageNumber=" + pageNumber;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("Data");
                JsonNode itemsNode = dataNode.path("Items");
                JsonNode pagingNode = dataNode.path("Paging");

                if (pagingNode.has("TotalPages")) {
                    totalPages = pagingNode.path("TotalPages").asInt(1);
                }

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    for (JsonNode item : itemsNode) {
                        String descricao = item.path("Descricao").asText(null);

                        if (descricao == null || descricao.trim().isEmpty()) {
                            continue;
                        }

                        Long produtoId = item.path("ProdutoId").asLong();

                        Double estoque = 1.0;

                        if (estoque <= 0) {
                            continue;
                        }

                        Long categoriaId = item.path("CategoriaId").asLong();
                        String nomeCategoria = categoriaPorId.get(categoriaId);

                        Double preco = null;

                        JsonNode prodEmpresaNode = item.path("ProdEmpresa");
                        if (prodEmpresaNode.isArray()) {
                            for (JsonNode empresa : prodEmpresaNode) {
                                if (empresa.path("EmpresaId").asInt() == 675) {
                                    preco = empresa.path("Preco").get(0).path("Preco").asDouble();
                                }
                            }
                        }

                        produtos.add(new Produto(produtoId, descricao, nomeCategoria, preco, estoque));
                    }
                } else {
                    hasMorePages = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hasMorePages = false;
            }

            pageNumber++;
        }

        salvarEmCSV(produtos);
        return produtos;
    }

    public List<Estoque> consultarEstoque() {
        String url = "https://apiexternal.mobne.com.br/api/v1/Produto/consulta-estoque-produto"
                + "?Filter.NroEmpresa=" + NUMERO_EMPRESA;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "ApiKey 6lrFHzvTsSl+Sowjd8ztRv3uqdaGuqL+UhbVh09bqhLsh9YNg9uaaY0RCDCEprIpATRXMN2PC1Q5aNeCE8u4mw==");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Estoque> estoqueList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode itemsNode = rootNode.path("Data").path("Items");

            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    Long produtoId = itemNode.path("ProdutoId").asLong();
                    Double qtdeEstoque = itemNode.path("QtdeEstoque").asDouble(0.0);

                    if (qtdeEstoque > 0) {
                        Estoque estoque = new Estoque();
                        estoque.setProdutoId(produtoId);
                        estoque.setQtdeEstoque(qtdeEstoque);
                        estoqueList.add(estoque);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return estoqueList;
    }

    public List<Categoria> consultarCategorias() {
        String urlBase = "https://apiexternal.mobne.com.br/api/v1/Produto/consulta-cadastro-categoria";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "ApiKey 6lrFHzvTsSl+Sowjd8ztRv3uqdaGuqL+UhbVh09bqhLsh9YNg9uaaY0RCDCEprIpATRXMN2PC1Q5aNeCE8u4mw==");
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<Categoria> categorias = new ArrayList<>();
        int pageNumber = 1;
        int totalPages = 1; // Inicializa com 1 para entrar no loop

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            while (pageNumber <= totalPages) {
                String urlCompleta = urlBase + "?PageNumber=" + pageNumber;
                ResponseEntity<String> response = restTemplate.exchange(urlCompleta, HttpMethod.GET, entity, String.class);
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("Data");
                JsonNode itemsNode = dataNode.path("Items");
                JsonNode pagingNode = dataNode.path("Paging");

                if (pagingNode.has("TotalPages")) {
                    totalPages = pagingNode.path("TotalPages").asInt(1);
                }

                if (itemsNode.isArray()) {
                    for (JsonNode itemNode : itemsNode) {
                        Long categoriaId = itemNode.path("CategoriaId").asLong();
                        String categoriaNome = itemNode.path("Categoria").asText();

                        Categoria categoria = new Categoria();
                        categoria.setCategoriaId(categoriaId);
                        categoria.setCategoria(categoriaNome);
                        categorias.add(categoria);
                    }
                }

                pageNumber++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorias;
    }

    public void salvarEmCSV(List<Produto> produtos) {
        String caminhoArquivo = "produtos.csv";

        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            // Criando cabeçalho
            writer.append("NAME;SKU;PRICE;CATEGORIES;STOCK\n");

            // Preenchendo o arquivo com os produtos
            for (Produto produto : produtos) {
                writer.append(produto.getDescricao()).append(";");
                writer.append(String.valueOf(produto.getProdutoId())).append(";");
                writer.append(String.valueOf(produto.getPreco())).append(";");
                writer.append(String.valueOf(produto.getCategoriaId())).append(";");
                writer.append(String.valueOf(produto.getEstoqueMinimo())).append("\n");
            }

            System.out.println("Arquivo CSV salvo com sucesso em: " + caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

