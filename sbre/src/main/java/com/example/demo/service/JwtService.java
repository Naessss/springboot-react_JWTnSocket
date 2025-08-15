package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JJWT 0.12.x 문법에 맞춘 JWT 유틸 서비스.
 * - issuer 미사용, 만료 24h 고정
 * - roles 클레임(List<String>)로 권한 내장
 * - 임시 SecretKey 사용(서버 재시작 시 기존 토큰 무효) → 운영에선 외부화된 고정 키 권장
 */
@Service
public class JwtService {

    /** 토큰 만료(24h, ms) */
    public static final long EXPIRATION_MS = 24L * 60 * 60 * 1000;

    /** Authorization 헤더 접두사 */
    private static final String BEARER = "Bearer ";

    /** 권한 클레임 키 */
    private static final String ROLES_CLAIM = "roles";

    /**
     * 임시 서명 키 (HS256).
     * - Jwts.SIG.HS256.key().build()는 매 실행마다 새 키를 만들기 때문에
     *   운영 전환 시에는 Base64 시크릿으로 고정 SecretKey를 로딩하세요.
     */
    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();

    /**
     * 액세스 토큰 생성
     * @param username  sub에 들어갈 사용자명
     * @param authorities  Spring Security 권한 컬렉션
     */
    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);

        List<String> roles = (authorities == null)
                ? List.of()
                : authorities.stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .subject(username)          // sub
                .issuedAt(now)              // iat
                .expiration(exp)            // exp
                .claim(ROLES_CLAIM, roles)  // 커스텀 권한 클레임
                // 0.12에서도 명시적 알고리즘 지정 가능. (KEY에 맞춰 자동 선택도 가능)
                .signWith(KEY)
                .compact();
    }

    /**
     * Authorization 헤더에서 Bearer 토큰만 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            return header.substring(BEARER.length()).trim();
        }
        return null;
    }

    /**
     * 토큰 유효성 검증 (서명/형식/만료 등)
     */
    public boolean validate(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            // 0.12: parser() -> verifyWith(key) -> clockSkewSeconds(...) -> build() -> parseSignedClaims(...)
            Jwts.parser()
                .verifyWith(KEY)          // 서명 검증키 지정
                .clockSkewSeconds(30)     // (옵션) 시계 오차 허용
                .build()
                .parseSignedClaims(token); // 파싱 + 검증
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 만료
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 서명불일치/형식오류/기타
        }
    }

    /**
     * 토큰에서 subject(username) 추출
     */
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 0.12: getBody() 대신 getPayload()
        return claims.getSubject();
    }

    /**
     * 토큰에서 roles 클레임을 Spring 권한 객체로 복원
     */
    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object raw = claims.get(ROLES_CLAIM);
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
