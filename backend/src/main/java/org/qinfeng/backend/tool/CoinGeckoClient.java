package org.qinfeng.backend.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class CoinGeckoClient {

    private static final String BASE_URL = "https://api.coingecko.com/api/v3";

    @Autowired
    @Qualifier("proxyOkHttpClient")
    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> getSimplePrice(String ids, String vsCurrencies) {
        String url = BASE_URL + "/simple/price?ids=" + ids + "&vs_currencies=" + vsCurrencies 
                + "&include_24hr_vol=true&include_24hr_change=true&include_market_cap=true";
        
        log.info("CoinGecko API请求 - 获取价格, ids={}, vsCurrencies={}", ids, vsCurrencies);
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            Map<String, Object> result = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String coinId = entry.getKey();
                JsonNode priceData = entry.getValue();
                
                Map<String, Object> coinInfo = new HashMap<>();
                coinInfo.put("id", coinId);
                
                String currency = vsCurrencies.split(",")[0].toLowerCase();
                coinInfo.put("price", priceData.path(currency).asDouble(0));
                coinInfo.put("marketCap", priceData.path(currency + "_market_cap").asDouble(0));
                coinInfo.put("24hVolume", priceData.path(currency + "_24h_vol").asDouble(0));
                coinInfo.put("24hChange", priceData.path(currency + "_24h_change").asDouble(0));
                
                result.put(coinId, coinInfo);
            }
            
            log.info("CoinGecko API响应成功 - 获取价格, coins={}", result.keySet());
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取价格, error={}", e.getMessage(), e);
            throw new RuntimeException("获取价格失败: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getOhlc(String coinId, String vsCurrency, int days) {
        String url = BASE_URL + "/coins/" + coinId + "/ohlc?vs_currency=" + vsCurrency + "&days=" + days;
        
        log.info("CoinGecko API请求 - 获取OHLC, coinId={}, vsCurrency={}, days={}", coinId, vsCurrency, days);
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonNode node : root) {
                Map<String, Object> ohlc = new HashMap<>();
                ohlc.put("timestamp", node.get(0).asLong());
                ohlc.put("open", node.get(1).asDouble());
                ohlc.put("high", node.get(2).asDouble());
                ohlc.put("low", node.get(3).asDouble());
                ohlc.put("close", node.get(4).asDouble());
                result.add(ohlc);
            }
            
            log.info("CoinGecko API响应成功 - 获取OHLC, dataCount={}", result.size());
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取OHLC, error={}", e.getMessage(), e);
            throw new RuntimeException("获取OHLC数据失败: " + e.getMessage());
        }
    }

    public Map<String, Object> getMarketChart(String coinId, String vsCurrency, int days) {
        String url = BASE_URL + "/coins/" + coinId + "/market_chart?vs_currency=" + vsCurrency + "&days=" + days;
        
        log.info("CoinGecko API请求 - 获取历史数据, coinId={}, vsCurrency={}, days={}", coinId, vsCurrency, days);
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            Map<String, Object> result = new HashMap<>();
            
            List<Map<String, Object>> prices = new ArrayList<>();
            for (JsonNode node : root.path("prices")) {
                Map<String, Object> item = new HashMap<>();
                item.put("timestamp", node.get(0).asLong());
                item.put("price", node.get(1).asDouble());
                prices.add(item);
            }
            result.put("prices", prices);
            
            List<Map<String, Object>> marketCaps = new ArrayList<>();
            for (JsonNode node : root.path("market_caps")) {
                Map<String, Object> item = new HashMap<>();
                item.put("timestamp", node.get(0).asLong());
                item.put("marketCap", node.get(1).asDouble());
                marketCaps.add(item);
            }
            result.put("marketCaps", marketCaps);
            
            List<Map<String, Object>> totalVolumes = new ArrayList<>();
            for (JsonNode node : root.path("total_volumes")) {
                Map<String, Object> item = new HashMap<>();
                item.put("timestamp", node.get(0).asLong());
                item.put("volume", node.get(1).asDouble());
                totalVolumes.add(item);
            }
            result.put("totalVolumes", totalVolumes);
            
            log.info("CoinGecko API响应成功 - 获取历史数据, pricesCount={}", prices.size());
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取历史数据, error={}", e.getMessage(), e);
            throw new RuntimeException("获取历史数据失败: " + e.getMessage());
        }
    }

    public Map<String, Object> getCoinDetails(String coinId) {
        String url = BASE_URL + "/coins/" + coinId 
                + "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";
        
        log.info("CoinGecko API请求 - 获取币种详情, coinId={}", coinId);
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", root.path("id").asText());
            result.put("symbol", root.path("symbol").asText());
            result.put("name", root.path("name").asText());
            result.put("description", root.path("description").path("en").asText());
            result.put("image", root.path("image").path("large").asText());
            
            JsonNode marketData = root.path("market_data");
            Map<String, Object> marketInfo = new HashMap<>();
            
            JsonNode currentPrice = marketData.path("current_price");
            Map<String, Double> prices = new HashMap<>();
            currentPrice.fields().forEachRemaining(entry -> 
                prices.put(entry.getKey(), entry.getValue().asDouble()));
            marketInfo.put("currentPrice", prices);
            
            JsonNode ath = marketData.path("ath");
            Map<String, Double> athPrices = new HashMap<>();
            ath.fields().forEachRemaining(entry -> 
                athPrices.put(entry.getKey(), entry.getValue().asDouble()));
            marketInfo.put("ath", athPrices);
            
            JsonNode athChange = marketData.path("ath_change_percentage");
            Map<String, Double> athChangePercent = new HashMap<>();
            athChange.fields().forEachRemaining(entry -> 
                athChangePercent.put(entry.getKey(), entry.getValue().asDouble()));
            marketInfo.put("athChangePercentage", athChangePercent);
            
            marketInfo.put("marketCapRank", marketData.path("market_cap_rank").asInt(0));
            marketInfo.put("totalVolume", marketData.path("total_volume").path("usd").asDouble(0));
            marketInfo.put("marketCap", marketData.path("market_cap").path("usd").asDouble(0));
            marketInfo.put("circulatingSupply", marketData.path("circulating_supply").asDouble(0));
            marketInfo.put("totalSupply", marketData.path("total_supply").asDouble(0));
            marketInfo.put("maxSupply", marketData.path("max_supply").asDouble(0));
            
            marketInfo.put("priceChange24h", marketData.path("price_change_24h").asDouble(0));
            marketInfo.put("priceChangePercentage24h", marketData.path("price_change_percentage_24h").asDouble(0));
            marketInfo.put("priceChangePercentage7d", marketData.path("price_change_percentage_7d").asDouble(0));
            marketInfo.put("priceChangePercentage30d", marketData.path("price_change_percentage_30d").asDouble(0));
            
            result.put("marketData", marketInfo);
            
            log.info("CoinGecko API响应成功 - 获取币种详情, name={}", result.get("name"));
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取币种详情, error={}", e.getMessage(), e);
            throw new RuntimeException("获取币种详情失败: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getTrending() {
        String url = BASE_URL + "/search/trending";
        
        log.info("CoinGecko API请求 - 获取热门币种");
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonNode coin : root.path("coins")) {
                JsonNode item = coin.path("item");
                Map<String, Object> coinInfo = new HashMap<>();
                coinInfo.put("id", item.path("id").asText());
                coinInfo.put("name", item.path("name").asText());
                coinInfo.put("symbol", item.path("symbol").asText());
                coinInfo.put("marketCapRank", item.path("market_cap_rank").asInt(0));
                coinInfo.put("priceBtc", item.path("price_btc").asDouble());
                coinInfo.put("score", item.path("score").asInt(0));
                result.add(coinInfo);
            }
            
            log.info("CoinGecko API响应成功 - 获取热门币种, count={}", result.size());
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取热门币种, error={}", e.getMessage(), e);
            throw new RuntimeException("获取热门币种失败: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getTopCoins(String vsCurrency, int perPage, int page) {
        String url = BASE_URL + "/coins/markets?vs_currency=" + vsCurrency 
                + "&order=market_cap_desc&per_page=" + perPage + "&page=" + page 
                + "&sparkline=false&price_change_percentage=24h,7d";
        
        log.info("CoinGecko API请求 - 获取Top币种, vsCurrency={}, perPage={}, page={}", vsCurrency, perPage, page);
        
        try {
            String responseBody = executeRequest(url);
            JsonNode root = objectMapper.readTree(responseBody);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonNode node : root) {
                Map<String, Object> coin = new HashMap<>();
                coin.put("id", node.path("id").asText());
                coin.put("symbol", node.path("symbol").asText());
                coin.put("name", node.path("name").asText());
                coin.put("image", node.path("image").asText());
                coin.put("currentPrice", node.path("current_price").asDouble(0));
                coin.put("marketCap", node.path("market_cap").asDouble(0));
                coin.put("marketCapRank", node.path("market_cap_rank").asInt(0));
                coin.put("totalVolume", node.path("total_volume").asDouble(0));
                coin.put("high24h", node.path("high_24h").asDouble(0));
                coin.put("low24h", node.path("low_24h").asDouble(0));
                coin.put("priceChange24h", node.path("price_change_24h").asDouble(0));
                coin.put("priceChangePercentage24h", node.path("price_change_percentage_24h").asDouble(0));
                coin.put("priceChangePercentage7d", node.path("price_change_percentage_7d_in_currency").asDouble(0));
                coin.put("circulatingSupply", node.path("circulating_supply").asDouble(0));
                coin.put("totalSupply", node.path("total_supply").asDouble(0));
                coin.put("ath", node.path("ath").asDouble(0));
                coin.put("athChangePercentage", node.path("ath_change_percentage").asDouble(0));
                coin.put("lastUpdated", node.path("last_updated").asText());
                result.add(coin);
            }
            
            log.info("CoinGecko API响应成功 - 获取Top币种, count={}", result.size());
            return result;
        } catch (Exception e) {
            log.error("CoinGecko API调用失败 - 获取Top币种, error={}", e.getMessage(), e);
            throw new RuntimeException("获取Top币种失败: " + e.getMessage());
        }
    }

    private String executeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        long startTime = System.currentTimeMillis();
        try (Response response = okHttpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - startTime;
            
            log.debug("CoinGecko API响应, url={}, status={}, elapsed={}ms", 
                    url, response.code(), elapsed);
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("CoinGecko API调用失败, status={}, body={}", response.code(), errorBody);
                throw new RuntimeException("API调用失败: " + response.code() + " - " + errorBody);
            }
            
            return response.body().string();
        }
    }
}
