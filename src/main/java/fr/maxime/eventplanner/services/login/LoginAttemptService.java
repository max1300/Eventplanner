package fr.maxime.eventplanner.services.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAXIMUM_OF_ATTEMPT = 5;
    public static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> cache;

    public LoginAttemptService() {
        super();
        cache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromCache(String username) {
        cache.invalidate(username);
    }

    public void addUserToCache(String username) {
        int attempts = 0;

        try {
            attempts = ATTEMPT_INCREMENT + cache.get(username);
            cache.put(username, attempts);
        }  catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean hasExcedeedNumberOfAttempts(String username) throws ExecutionException {
        return cache.get(username) >= MAXIMUM_OF_ATTEMPT;
    }
}
