import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class CreatTokenTest {
    /**
     * 通过jwt创建token字符串
     * @param args
     */
    public static void main(String[] args) {
        JwtBuilder jwtBuilder = Jwts.builder().setId("88").setSubject("小白")
                .setIssuedAt(new Date())
                .claim("companyId",123)
                .signWith(SignatureAlgorithm.HS256,"igrm");//加密方法

        String token = jwtBuilder.compact();
        System.out.println(token);
    }
}
