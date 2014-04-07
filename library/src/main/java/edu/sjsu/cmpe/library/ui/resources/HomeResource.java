package edu.sjsu.cmpe.library.ui.resources;

import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.views.HomeView;
import edu.sjsu.cmpe.library.ui.views.WebSocketsView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {
    private final BookRepositoryInterface bookRepository;

    public HomeResource(BookRepositoryInterface bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GET
    public HomeView getHome() {
        return new HomeView(bookRepository.getAllBooks());
    }

    @GET
    @Path("/websockets")
    public WebSocketsView getWebSockets(){
        return new WebSocketsView();
    }
}
