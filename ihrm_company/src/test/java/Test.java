import com.ntu.company.CompanyApplication;
import com.ntu.company.dao.CompanyDao;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = CompanyApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class Test {
    @Autowired
    private CompanyDao companyDao;

    @org.junit.Test
    public void test(){
        System.out.println(companyDao.count());
    }
}
