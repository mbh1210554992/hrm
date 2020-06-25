import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class ParseTokenTest {
    /**
     * 解析token字符串
     *      claims : token中的私有数据
     * @param args
     */
    public static void main(String[] args) {
        Claims claims =Jwts.parser().setSigningKey("saas-ntu")
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDYzNzA1NDgyOTM5NzMxOTY4Iiwic3ViIjoiY2d4IiwiaWF0IjoxNTgyMDE2OTcyLCJjb21wYW55SWQiOiIxIiwiY29tcGFueU5hbWUiOiJudHUiLCJleHAiOjE1ODIwMjA1NzJ9.XY_fUk6iwCTArlHGbmZqZHUXLLxkT5b0JrRtqoqoGZ0").getBody();
        System.out.println(claims.getId());
        System.out.println(claims.get("companyId"));
    }
}
