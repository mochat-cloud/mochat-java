package com.mochat.mochat.common.util;

import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.dao.entity.UserEntity;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description:jwt工具类
 * @author: Huayu
 * @time: 2020/11/19 17:49
 */
public final class JwtUtils {

    private static final String key = Const.SIMPLE_JWT_SECRET;
    private static final String key_prefix = Const.SIMPLE_JWT_PREFIX;

    public static String createToken(long ttlMillis, UserEntity user) {
        // 指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put(key_prefix, key);
        // 生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。

        // 生成签发人
        String subject = user.getName();

        // 生成JWT的时间
        Date now = new Date();
        long nowMillis = now.getTime();

        // 下面就是在为payload添加各种标准声明和私有声明了
        // 这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                // iat: jwt的签发时间
                .setIssuedAt(now)
                // 代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key);

        if (ttlMillis >= 0) {
            // 设置过期时间
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * Token的解密
     */
    public static Claims parseToken(String token) {
        // 去除 token 中的协议信息
        token = token.substring(token.indexOf(" ") + 1);

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验token
     * 在这里可以使用官方的校验，我这里校验的是token中携带的密码于数据库一致的话就校验通过
     */
    public static Boolean isVerify(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(key_prefix).equals(key);
        } catch (JwtException e) {
            return false;
        }
    }

}
