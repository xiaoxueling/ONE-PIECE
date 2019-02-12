package cn.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: baoxuechao
 * @Date: 2019/2/12 14:07
 * @Description: 基于jwt的token身份认证
 */
public class JwtUtil {

    /**
     * 过期时间
     */
    private static final long EXPIRE_TIME = 15 * 60 * 1000;

    /**
     * token私钥
     */
    private static final String TOKEN_SECRET = "sdfa09asdf979a7g0a98f0q308s0df";

    /**
     * 生成签名,有效期15分钟
     * @param username 用户名
     * @return 加密的token
     */
    public static String sign(String username,String userId,Integer roleId,String authority){
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            //私钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String ,Object> header = new HashMap<String ,Object>(2);
            header.put("typ","JWT");
            header.put("alg","HS256");
            return JWT.create()
                    .withHeader(header)
                    .withClaim("loginName",username)
                    .withClaim("userId",userId)
                    .withClaim("authority",authority)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (UnsupportedEncodingException e){
            return null;
        }
    }

    /**
     * token解密
     */
    public static boolean verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
