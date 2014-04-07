package edu.sjsu.cmpe.library;

import de.spinscale.dropwizard.jobs.JobsBundle;
import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryService extends Application<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static LibraryServiceConfiguration configuration;

    public static void main(String[] args) throws Exception {
        new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {

        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.library.jobs"));

    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
                    Environment environment) throws Exception {

        LibraryService.configuration = configuration;

        // This is how you pull the configurations from library_x_config.yml
        String queueName = configuration.getStompQueueName();
        String topicName = configuration.getStompTopicName();
        log.debug("{} - Queue name is {}. Topic name is {}", configuration.getLibraryName(), queueName, topicName);

        /** Root API */
        environment.jersey().register(RootResource.class);
        /** Books APIs */
        BookRepositoryInterface bookRepository = new BookRepository();
        environment.jersey().register(new BookResource(bookRepository));

        /** UI Resources */
        environment.jersey().register(new HomeResource(bookRepository));

        /** CrossOriginFilter - need this to enable websockets cross-origin policy */
        environment.servlets().addFilter("/*", new CrossOriginFilter());
    }
}
