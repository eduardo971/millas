package org.tmc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

    private final RestTemplate restTemplate;

    public ProdutoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Produto> buscarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        int pageNumber = 1;
        int pageSize = 100; // Ajuste conforme necessário
        boolean hasMorePages = true;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", API_KEY);
        headers.set("Accept", "application/json");

        List<Estoque> estoqueList = consultarEstoque();

        Map<Long, Double> estoquePorProdutoId = estoqueList.stream()
                .collect(Collectors.toMap(Estoque::getProdutoId, Estoque::getQtdeEstoque));

        while (hasMorePages) {
            String url = API_URL + "?Filter.EmpresaId=675&Filter.StatusVenda=A&PageSize=" + pageSize + "&PageNumber=" + pageNumber;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode itemsNode = rootNode.path("Data").path("Items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    for (JsonNode item : itemsNode) {
                        String descricaoReduzida = item.path("DescricaoReduzida").asText(null);

                        if (descricaoReduzida == null || descricaoReduzida.trim().isEmpty()) {
                            continue;
                        }

                        Long produtoId = item.path("ProdutoId").asLong();

                        Double estoque = estoquePorProdutoId.getOrDefault(produtoId, 0.0);

                        if (estoque <= 0) {
                            continue;
                        }

                        Long categoriaId = item.path("CategoriaId").asLong();

                        Double preco = null;

                        JsonNode prodEmpresaNode = item.path("ProdEmpresa");
                        if (prodEmpresaNode.isArray()) {
                            for (JsonNode empresa : prodEmpresaNode) {
                                if (empresa.path("EmpresaId").asInt() == 675) {
                                    preco = empresa.path("Preco").get(0).path("Preco").asDouble();
                                }
                            }
                        }

                        produtos.add(new Produto(produtoId, descricaoReduzida, categoriaId, preco, estoque));
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
                + "?Filter.NroEmpresa=2";

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

    public void salvarEmCSV(List<Produto> produtos) {
        String caminhoArquivo = "produtos.csv";

        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            // Criando cabeçalho
            writer.append("NAME;SKU;PRICE;CATEGORIES;STOCK\n");

            // Preenchendo o arquivo com os produtos
            for (Produto produto : produtos) {
                writer.append(produto.getDescricaoReduzida()).append(";");
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

