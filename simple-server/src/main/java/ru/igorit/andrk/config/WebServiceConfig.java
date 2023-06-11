package ru.igorit.andrk.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet>
    messageDispatcherServletServlet(ApplicationContext applicationContext){

        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(servlet,
                "/GovKgdService/SendMessage",
                "/GovKgdService/openCloseAcc.wsdl");
    }

    @Bean(name="openCloseSchema")
    public XsdSchema openCloseSchema(){
        return new SimpleXsdSchema(new ClassPathResource("OpenClose.xsd"));
    };

    @Bean(name = "openCloseAcc")
    public DefaultWsdl11Definition defaultWsdl11Definition(
            @Qualifier("openCloseSchema") XsdSchema openCloseSchema) {
        var wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("OpenClosePort");
        wsdl11Definition.setLocationUri("/GovKgdService");
        wsdl11Definition.setTargetNamespace("http://bvu.actualization.arm.isna.kz");
        wsdl11Definition.setSchema(openCloseSchema);
        return wsdl11Definition;
    }
}
