//package edu.sjsu.cmpe.library.jobs;
//
//import de.spinscale.dropwizard.jobs.Job;
//import de.spinscale.dropwizard.jobs.annotations.Every;
//import edu.sjsu.cmpe.library.service.StompService;
//
///**
// * User: maksim
// * Date: 4/2/14 - 6:17 PM
// */
//@Every("10s")
//public class SchedulerJob extends Job{
//
//    StompService stompService = new StompService();
//
//    @Override
//    public void doJob() {
////        System.out.println(" -> JOB " + new Date());
//
//
////        try {
////            stompService.sendMessageToTopic("HIii @ " + new Date());
////
////            Thread.sleep(5000);
////
////            String messageFromTopic = stompService.getMessageFromTopic();
////
////            System.out.println("Message from TOPIC: " + messageFromTopic);
////        } catch (JMSException e) {
////            e.printStackTrace();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//    }
//}
