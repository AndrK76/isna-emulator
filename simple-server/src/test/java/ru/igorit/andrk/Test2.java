package ru.igorit.andrk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.igorit.andrk.config.datasource.MainDatasourceConfiguration;
import ru.igorit.andrk.config.datasource.MainFlywayConfiguration;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.repository.main.ResponseRepository;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.service.store.MainStoreServiceJPAImpl;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.UUID;

@TestPropertySource(locations = "/testH2NoMigrate.properties")
@EnableAutoConfiguration(exclude={FlywayAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MainDatasourceConfiguration.class, MainFlywayConfiguration.class})
public class Test2 {

    @Autowired
    private DataSourceProperties mainProps;

    @Autowired
    private DataSource mainSource;

    @Autowired
    private RequestRepository reqRepo;
    @Autowired
    private ResponseRepository respRepo;

    private Request makeTestRequest() {
        var requestUUID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        return new Request(null, requestUUID, serviceName, requestDate, data);
    }

    //@Test
    public void test(){
        MainStoreService storeSvc= new MainStoreServiceJPAImpl(reqRepo,respRepo,null,null);
        var qq = storeSvc;
        var request = makeTestRequest();
        var request2 = storeSvc.saveRequest(request);
    }
}
