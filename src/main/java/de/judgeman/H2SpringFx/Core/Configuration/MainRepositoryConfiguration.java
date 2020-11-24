package de.judgeman.H2SpringFx.Core.Configuration;

import de.judgeman.H2SpringFx.HelperClasses.CustomRoutingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Paul Richter on Mon 23/11/2020
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager",
        basePackages = {"de.judgeman.H2SpringFx.Core.Repositories"})
public class MainRepositoryConfiguration {

    private static LocalContainerEntityManagerFactoryBean entityManagerFactory;

    public static CustomRoutingDataSource customRoutingDataSource;

    public DataSource dataSource() throws SQLException {
        customRoutingDataSource = new CustomRoutingDataSource();
        customRoutingDataSource.setTargetDataSources(customRoutingDataSource.getTargetDataSources());

        return customRoutingDataSource;
    }

    @Bean (name = "mainEntityManager")
    public EntityManager entityManager() throws SQLException {
        return entityManagerFactory().createEntityManager();
    }

    @Bean (name = "mainEntityManagerFactory")
    public EntityManagerFactory entityManagerFactory() throws SQLException {
        entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("de.judgeman.H2SpringFx.Model");
        entityManagerFactory.setPersistenceUnitName("mainPersistenceUnit");

        testSettingDialect("org.hibernate.dialect.H2Dialect"); // default dialect !?
        entityManagerFactory.afterPropertiesSet();

        return entityManagerFactory.getObject();
    }

    public static void testSettingDialect(String dialect) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);
//        properties.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        entityManagerFactory.setJpaProperties(properties);
        entityManagerFactory.afterPropertiesSet();
    }

    @Bean (name = "mainTransactionManager")
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new JpaTransactionManager(entityManagerFactory());
    }
}
