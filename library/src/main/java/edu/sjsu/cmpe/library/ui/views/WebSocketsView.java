package edu.sjsu.cmpe.library.ui.views;

import edu.sjsu.cmpe.library.LibraryService;
import io.dropwizard.views.View;
import lombok.Data;

/**
 * User: maksim
 * Date: 4/3/14 - 7:52 AM
 */
@Data
public class WebSocketsView extends View {

    private String libraryName;
    private String stompTopicName;
    private String stompQueueName;
    private String apolloUser;
    private String apolloPassword;
    private String apolloHost;
    private String apolloPort;

    public WebSocketsView() {

        super("websockets.mustache");
        this.libraryName = LibraryService.configuration.getLibraryName();
        this.stompTopicName = LibraryService.configuration.getStompTopicName();
        this.stompQueueName = LibraryService.configuration.getStompQueueName();
        this.apolloUser = LibraryService.configuration.getApolloUser();
        this.apolloPassword = LibraryService.configuration.getApolloPassword();
        this.apolloHost = LibraryService.configuration.getApolloHost();
        this.apolloPort = LibraryService.configuration.getApolloPort();
    }
}
