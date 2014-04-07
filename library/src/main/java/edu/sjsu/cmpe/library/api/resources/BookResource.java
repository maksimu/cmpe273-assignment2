package edu.sjsu.cmpe.library.api.resources;

import com.codahale.metrics.annotation.Timed;
import edu.sjsu.cmpe.library.LibraryService;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.dto.BookDto;
import edu.sjsu.cmpe.library.dto.BooksDto;
import edu.sjsu.cmpe.library.dto.LinkDto;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.service.StompService;
import io.dropwizard.jersey.params.LongParam;

import javax.jms.JMSException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    /**
     * bookRepository instance
     */
    private final BookRepositoryInterface bookRepository;

    StompService stompService;

    /**
     * BookResource constructor
     *
     * @param bookRepository a BookRepository instance
     */
    public BookResource(BookRepositoryInterface bookRepository) throws JMSException {
        this.bookRepository = bookRepository;
        this.stompService = new StompService();

    }

    @GET
    @Path("/{isbn}")
    @Timed(name = "view-book")
    public BookDto getBookByIsbn(@PathParam("isbn") LongParam isbn) {
        Book book = bookRepository.getBookByISBN(isbn.get());
        BookDto bookResponse = new BookDto(book);
        bookResponse.addLink(new LinkDto("view-book", "/books/" + book.getIsbn(),
                "GET"));
        bookResponse.addLink(new LinkDto("update-book-status", "/books/"
                + book.getIsbn(), "PUT"));
        // add more links

        return bookResponse;
    }

    @POST
    @Timed(name = "create-book")
    public Response createBook(@Valid Book request) {
        // Store the new book in the BookRepository so that we can retrieve it.
        Book savedBook = bookRepository.saveBook(request);

        String location = "/books/" + savedBook.getIsbn();
        BookDto bookResponse = new BookDto(savedBook);
        bookResponse.addLink(new LinkDto("view-book", location, "GET"));
        bookResponse
                .addLink(new LinkDto("update-book-status", location, "PUT"));

        return Response.status(201).entity(bookResponse).build();
    }

    @GET
    @Path("/")
    @Timed(name = "view-all-books")
    public BooksDto getAllBooks() {
        BooksDto booksResponse = new BooksDto(bookRepository.getAllBooks());
        booksResponse.addLink(new LinkDto("create-book", "/books", "POST"));

        return booksResponse;
    }

    @PUT
    @Path("/{isbn}")
    @Timed(name = "update-book-status")
    public Response updateBookStatus(@PathParam("isbn") LongParam isbn,
                                     @DefaultValue("available") @QueryParam("status") Status status) throws JMSException {

        Book book = bookRepository.getBookByISBN(isbn.get());
        book.setStatus(status);

        BookDto bookResponse = new BookDto(book);
        String location = "/books/" + book.getIsbn();
        bookResponse.addLink(new LinkDto("view-book", location, "GET"));

        if(status.equals(Status.lost)){

            String message = LibraryService.configuration.getLibraryName() + ":" + isbn;

            stompService.sendMessageToQueue(message);

        }


        return Response.status(200).entity(bookResponse).build();
    }

    @POST
    @Path("/frompublisher")
    @Consumes("application/json")
    @Produces("application/json")
    public Response bookFromPublisher(Book bookFromPublisher){

        System.out.println("ISBN: " + bookFromPublisher.getIsbn());

        Book existingBook = bookRepository.getBookByISBN(bookFromPublisher.getIsbn());

        if(existingBook != null){
            System.out.println("Existing book");
            if(! existingBook.getStatus().equals(bookFromPublisher.getStatus())){
                System.out.println("This book was lost, but now it is found. Amazing grace!");

                existingBook.setStatus(Status.available);
            }
        } else {
            System.out.println("New book that we don't have in our DB");
            existingBook = bookRepository.saveBook(bookFromPublisher);
        }

        return Response.ok(existingBook).build();
    }

    @DELETE
    @Path("/{isbn}")
    @Timed(name = "delete-book")
    public BookDto deleteBook(@PathParam("isbn") LongParam isbn) {
        bookRepository.delete(isbn.get());
        BookDto bookResponse = new BookDto(null);
        bookResponse.addLink(new LinkDto("create-book", "/books", "POST"));

        return bookResponse;
    }
}

