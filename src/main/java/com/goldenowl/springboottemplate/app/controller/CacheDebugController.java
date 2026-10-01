package com.goldenowl.springboottemplate.app.controller;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/debug/cache")
@RequiredArgsConstructor
@Profile(ProfileConstant.DEVELOPMENT)
class CacheDebugController {

  private final CacheManager cacheManager;

  @GetMapping("/{cacheName}/{key}")
  Map<String, Object> getCacheEntry(@PathVariable String cacheName, @PathVariable String key) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache not found: " + cacheName);
    }

    Cache.ValueWrapper wrapper = cache.get(key);
    if (wrapper == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Key not found in cache: " + key);
    }

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("cache", cacheName);
    body.put("key", key);
    body.put("value", wrapper.get());
    return body;
  }
}
