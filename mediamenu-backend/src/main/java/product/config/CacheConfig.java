package product.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    // search data can update constantly, so we'll update that cache more frequently
    @Primary
    @Bean
    public CacheManager searchCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "searchAlbumCache",
                "searchArtistCache",
                "searchTrackCache");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .maximumSize(500)
                        .recordStats()
        );
        return cacheManager;
    }

    @Bean
    public CacheManager getCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "getAlbumCache",
                "getArtistCache",
                "getTrackCache",
                "getReissueCache");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(500)
                        .recordStats()
        );
        return cacheManager;
    }
}