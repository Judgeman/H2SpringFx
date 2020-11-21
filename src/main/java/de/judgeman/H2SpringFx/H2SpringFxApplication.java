package de.judgeman.H2SpringFx;

import de.judgeman.H2SpringFx.HelperClasses.CustomRoutingDataSource;
import de.judgeman.H2SpringFx.Services.DataSourceService;
import de.judgeman.H2SpringFx.ViewControllers.MainViewController;
import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.ViewService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@SpringBootApplication
public class H2SpringFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;

    private LanguageService languageService;
    private ViewService viewService;

    private Exception exceptionOnStartup;

    private static LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Override
    public void init() throws Exception {
        try {
            springContext = SpringApplication.run(H2SpringFxApplication.class);

            languageService = springContext.getBean(LanguageService.class);
            viewService = springContext.getBean(ViewService.class);
            DataSourceService dataSourceService = springContext.getBean(DataSourceService.class);

            dataSourceService.checkSettingsDatasourceAndInitIfNeeded();

            root = viewService.getRootElementFromFXML(ViewService.FILE_PATH_MAIN_VIEW);
            viewService.registerMainViewController(springContext.getBean(MainViewController.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            exceptionOnStartup = ex;
        }
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        CustomRoutingDataSource customRoutingDataSource = new CustomRoutingDataSource();
        customRoutingDataSource.setTargetDataSources(customRoutingDataSource.getTargetDataSources());

        return customRoutingDataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() throws SQLException {
        entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("de.judgeman.H2SpringFx.Model");
        entityManagerFactory.setPersistenceUnitName("default");

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

    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new JpaTransactionManager(entityManagerFactory());
    }

    private void showStartUpErrorMessage(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ups something went wrong");
        alert.setHeaderText("Error on loading Application");
        alert.setContentText(ex.getMessage());

        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        if (exceptionOnStartup != null) {
            showStartUpErrorMessage(exceptionOnStartup);
            return;
        }

        primaryStage.setTitle(languageService.getLocalizationText("applicationTitle"));
        primaryStage.setScene(new Scene(root, ViewService.DEFAULT_WIDTH, ViewService.DEFAULT_HEIGHT));

        viewService.registerPrimaryStage(primaryStage);
        viewService.setDefaultStyleCss(primaryStage);
        viewService.restoreScenePositionAndSize(primaryStage);

        primaryStage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.stop();
        }
    }
}
