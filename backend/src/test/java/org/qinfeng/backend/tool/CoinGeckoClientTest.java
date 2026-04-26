package org.qinfeng.backend.tool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinGeckoClientTest {

    @Autowired
    private CoinGeckoClient coinGeckoClient;

    @Test
    void getSimplePrice() {
        Map<String, Object> result = coinGeckoClient.getSimplePrice("bitcoin,ethereum", "usd");
        
        assertNotNull(result);
        assertTrue(result.containsKey("bitcoin"));
        
        Map<String, Object> btcInfo = (Map<String, Object>) result.get("bitcoin");
        assertNotNull(btcInfo);
        assertTrue(btcInfo.containsKey("price"));
        assertTrue(btcInfo.containsKey("marketCap"));
        assertTrue(btcInfo.containsKey("24hVolume"));
        assertTrue(btcInfo.containsKey("24hChange"));
        
        System.out.println("=== Simple Price ===");
        System.out.println(result);
    }

    @Test
    void getOhlc() {
        List<Map<String, Object>> result = coinGeckoClient.getOhlc("bitcoin", "usd", 7);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        Map<String, Object> firstOhlc = result.get(0);
        assertTrue(firstOhlc.containsKey("timestamp"));
        assertTrue(firstOhlc.containsKey("open"));
        assertTrue(firstOhlc.containsKey("high"));
        assertTrue(firstOhlc.containsKey("low"));
        assertTrue(firstOhlc.containsKey("close"));
        
        System.out.println("=== OHLC (7 days) ===");
        System.out.println("Data count: " + result.size());
        System.out.println("First candle: " + firstOhlc);
        System.out.println("Last candle: " + result.get(result.size() - 1));
    }

    @Test
    void getMarketChart() {
        Map<String, Object> result = coinGeckoClient.getMarketChart("bitcoin", "usd", 7);
        
        assertNotNull(result);
        assertTrue(result.containsKey("prices"));
        assertTrue(result.containsKey("marketCaps"));
        assertTrue(result.containsKey("totalVolumes"));
        
        List<Map<String, Object>> prices = (List<Map<String, Object>>) result.get("prices");
        assertFalse(prices.isEmpty());
        
        System.out.println("=== Market Chart (7 days) ===");
        System.out.println("Prices count: " + prices.size());
        System.out.println("First price: " + prices.get(0));
        System.out.println("Last price: " + prices.get(prices.size() - 1));
    }

    @Test
    void getCoinDetails() {
        Map<String, Object> result = coinGeckoClient.getCoinDetails("bitcoin");
        
        assertNotNull(result);
        assertEquals("bitcoin", result.get("id"));
        assertEquals("btc", result.get("symbol"));
        assertEquals("Bitcoin", result.get("name"));
        
        assertTrue(result.containsKey("marketData"));
        Map<String, Object> marketData = (Map<String, Object>) result.get("marketData");
        assertTrue(marketData.containsKey("currentPrice"));
        assertTrue(marketData.containsKey("marketCap"));
        assertTrue(marketData.containsKey("priceChange24h"));
        assertTrue(marketData.containsKey("priceChangePercentage24h"));
        
        System.out.println("=== Coin Details ===");
        System.out.println("ID: " + result.get("id"));
        System.out.println("Name: " + result.get("name"));
        System.out.println("Symbol: " + result.get("symbol"));
        System.out.println("Market Data: " + marketData);
    }

    @Test
    void getTrending() {
        List<Map<String, Object>> result = coinGeckoClient.getTrending();
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        Map<String, Object> firstCoin = result.get(0);
        assertTrue(firstCoin.containsKey("id"));
        assertTrue(firstCoin.containsKey("name"));
        assertTrue(firstCoin.containsKey("symbol"));
        
        System.out.println("=== Trending Coins ===");
        System.out.println("Count: " + result.size());
        for (int i = 0; i < Math.min(5, result.size()); i++) {
            System.out.println((i + 1) + ". " + result.get(i).get("name") + " (" + result.get(i).get("symbol") + ")");
        }
    }

    @Test
    void getTopCoins() {
        List<Map<String, Object>> result = coinGeckoClient.getTopCoins("usd", 10, 1);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() <= 10);
        
        Map<String, Object> firstCoin = result.get(0);
        assertTrue(firstCoin.containsKey("id"));
        assertTrue(firstCoin.containsKey("name"));
        assertTrue(firstCoin.containsKey("symbol"));
        assertTrue(firstCoin.containsKey("currentPrice"));
        assertTrue(firstCoin.containsKey("marketCap"));
        assertTrue(firstCoin.containsKey("marketCapRank"));
        
        System.out.println("=== Top 10 Coins ===");
        for (Map<String, Object> coin : result) {
            System.out.println(coin.get("marketCapRank") + ". " + coin.get("name") + " (" + coin.get("symbol") + ") - $" + coin.get("currentPrice"));
        }
    }
}
