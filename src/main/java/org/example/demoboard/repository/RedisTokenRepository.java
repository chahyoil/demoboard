//package org.example.demoboard.repository;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class RedisTokenRepository {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public RedisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public void saveToken(String token, String username) {
//        // 토큰 저장 로직
//    }
//
//    @Override
//    public String getUsernameFromToken(String token) {
//        // 토큰으로부터 사용자명 가져오는 로직
//        return null;
//    }
//
//    @Override
//    public void deleteToken(String token) {
//        // 토큰 삭제 로직
//    }
//}