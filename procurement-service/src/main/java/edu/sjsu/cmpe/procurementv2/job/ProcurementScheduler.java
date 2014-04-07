package edu.sjsu.cmpe.procurementv2.job;

import edu.sjsu.cmpe.procurementv2.dto.Book;
import edu.sjsu.cmpe.procurementv2.dto.ShippedBooks;
import edu.sjsu.cmpe.procurementv2.services.StompService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * User: maksim
 * Date: 3/29/14 - 7:52 PM
 */
@Service
public class ProcurementScheduler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${apolloHost}")
    String apolloHost;
    @Value("${apolloPort}")
    String stompPort;
    @Value("${apolloUser}")
    String stompAdmin;
    @Value("${apolloPassword}")
    String stompPassw;
    @Value("${stompQueueName}")
    String stompQueueName;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StompService stompService;

//    @Scheduled(fixedRate = 10000)// 10 sec
    @Scheduled(fixedRate = 300000) // 5 min
    public void lostBookSchedule() throws JMSException {

        log.info("============================");
        log.info(" Scheduler " + new Date() + ": Lost Book Schedule");
        log.info("============================");

        List<String> lostBooks = new ArrayList<String>();

        /**
         * Let's collect all queues
         */
        long waitUntil = 5000; // wait for 5 sec
        while (true) {
            log.info("Start checking queue for Lost books");
            String message = stompService.getMessageFromQueue(waitUntil);

            if (message != null) {
                String[] parts = message.split(":");
                String library = parts[0]; // library-a
                String id = parts[1]; // 1234

                log.info("Got a lost book from library=" + library + ", isbn=" + id + ". Adding to the array to send to publisher");
                lostBooks.add(id);
            } else {
                break;
            }
        }

        /**
         * Submit lost books to publisher (if lostBooks.size > 0)
         */
        if (lostBooks.size() > 0) {
            String publisherBookOrderUrl = "http://" + apolloHost + ":9000/orders";

            log.info("There are " + lostBooks.size() + " books that are lost. Sending request to publisher [" + publisherBookOrderUrl + "]");
            HashMap<String, Object> bookOrder = new HashMap<String, Object>();
            bookOrder.put("id", "53730");
            bookOrder.put("order_book_isbns", lostBooks);

            HashMap<String, String> hashMap = restTemplate.postForObject(publisherBookOrderUrl, bookOrder, HashMap.class);

            log.info("Response from the server: [" + hashMap.get("msg") + "]");
        } else {
            log.info("There were no lost books");
        }
    }

    //    @Scheduled(fixedRate = 10000)// 10 sec
    @Scheduled(fixedRate = 300000) // 5 min
    public void newBooksFromPublisher() throws JMSException {

        log.info("============================");
        log.info(" Scheduler " + new Date() + ": New Books from Publisher");
        log.info("============================");

        ShippedBooks shipppedBooksJsonString = restTemplate.getForObject("http://" + apolloHost + ":9000/orders/53730", ShippedBooks.class);

        log.info("Got [" + shipppedBooksJsonString.getShipped_books().toString().length() + "] from the publisher.");
        for (Book book : shipppedBooksJsonString.getShipped_books()) {

            long isbn = book.getIsbn();
            String title = book.getTitle();
            String category = book.getCategory();
            String coverimage = book.getCoverimage();

            String topicName = "53730.book." + category; // 53730.book.computer
            // 123:"Restful Web Services":"computer":"http://goo.gl/ZGmzoJ"
            String topicMessage = String.format("%d:\"%s\":\"%s\":\"%s\"", isbn, title, category, coverimage);

            boolean isSent = stompService.sendMessageToTopic(topicName, topicMessage);

            log.info(isSent ? "Added new book to the topic [" + topicName + "], message=[" + topicMessage + "]" : "Not Sent");
        }
    }


}
