package edu.sjsu.cmpe.library.ui.views;

import edu.sjsu.cmpe.library.LibraryService;
import edu.sjsu.cmpe.library.domain.Book;
import io.dropwizard.views.View;
import lombok.Data;

import java.util.List;

@Data
public class HomeView extends View {

    private final List<Book> books;
    private String libraryName;
    private String stompTopicName;
    private String stompQueueName;
    private String apolloUser;
    private String apolloPassword;
    private String apolloHost;
    private String apolloPort;

    public HomeView(List<Book> books) {
        super("home.mustache");

        this.books = books;
        this.libraryName = LibraryService.configuration.getLibraryName();
        this.stompTopicName = LibraryService.configuration.getStompTopicName();
        this.stompQueueName = LibraryService.configuration.getStompQueueName();
        this.apolloUser = LibraryService.configuration.getApolloUser();
        this.apolloPassword = LibraryService.configuration.getApolloPassword();
        this.apolloHost = LibraryService.configuration.getApolloHost();
        this.apolloPort = LibraryService.configuration.getApolloPort();
    }
}
