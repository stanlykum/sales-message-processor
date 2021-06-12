package com.jpmc.receiver;

import com.jpmc.domain.ApplicationEnum;
import com.jpmc.domain.Product;
import com.jpmc.domain.Report;
import com.jpmc.msg.Message;
import com.jpmc.msg.MessageImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to receive message and used as reporting tool.
 *
 * @author Stanly
 */
public class MessageReceiver {

    private final Logger logger = Logger.getLogger(MessageReceiver.class.getName());
    private final List<Product> productList = new ArrayList<Product>();
    private final List<Product> adjustList = new ArrayList<Product>();
    private final String padding = new String(new char[64]);
    private boolean isPause = false;

    public MessageReceiver() {
    }

    /**
     * Responsible for processing the message and generate the report.
     *
     * @param line
     * @throws FileNotFoundException
     */
    public void receive(String line) throws FileNotFoundException {

        //Process all messages
        Product p = process(line);

        if (p != null) {
            //Record all sales
            record(p);
            //report 10
            logPeriodicalReport();
            //report 50
            logAdjustmentReport(p);
        }
    }

    /**
     * Process the message line by line.
     *
     * @param line
     * @return
     */
    private Product process(String line) {

        logger.log(Level.INFO, "Message received:" + line);
        Product p = null;
        Message message = new MessageImpl(line);

        if (message.validateMessage()) {
            logger.log(Level.INFO, "Only valid message will be processed");
            p = message.buildMessage();
        }
        return p;
    }

    /**
     * All the messages are accumulated.
     *
     * @param p
     */
    private void record(Product p) {
        productList.add(p);
    }

    /**
     * This is used to print the report.
     */
    private void logPeriodicalReport() {

        if ((productList.size() % Integer.parseInt(ApplicationEnum.LOG_REPORT_PER_MESSAGE_RECEIVED.getEnumType()) == 0) && !isPause) {
            printPeriodicalReport();
        }
    }

    /**
     * This is used to print adjustment report
     *
     * @param p
     */
    private void logAdjustmentReport(Product p) {

        switch (p.getType()) {

            case ADDITION:
            case MULTIPLICATION:
            case SUBTRACTION:
                adjustList.add(p);
                break;
        }

        if (productList.size() == Integer.parseInt(ApplicationEnum.MAX_MESSAGES_TO_PAUSE_PROCESSING.getEnumType()) && !isPause) {
            pause();
            logAdjustmentReport();
        }
    }

    /**
     * Pause the message if the threshold of 50 messages are processed
     */
    private void pause() {

        isPause = true;
        logger.log(Level.INFO, "Reached today quota of " + ApplicationEnum.MAX_MESSAGES_TO_PAUSE_PROCESSING.getEnumType() + " messages. Pausing.................");
    }

    /**
     * Print the report in console
     */
    private void printPeriodicalReport() {

        Map<String, Report> reportMap = populateReportMap();
        System.out.println("After " + ApplicationEnum.LOG_REPORT_PER_MESSAGE_RECEIVED.getEnumType() + " messages received, the log report:");
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println("|        NAME                         |	 QUANTITY      |   TOTAL  |");
        System.out.println("+-------------------------------------+----------------+----------+");
        for (Map.Entry<String, Report> entry : reportMap.entrySet()) {

            String name = entry.getKey();
            Report r = entry.getValue();
            if (r.getName() != null) {
                System.out.println("|" + (name + padding).substring(0, 37) + "|" + (r.getQuantity() + padding).substring(0, 16) + "|" + ("£" + r.getTotalPrice() / 100 + padding).substring(0, 10) + "|");
            }
        }
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println();
        System.out.println();
    }

    /**
     * This is used to apply adjustment logic for the report
     */
    private void logAdjustmentReport() {

        logger.log(Level.INFO, "Adjustment received: " + adjustList.size());
        System.out.println("After " + ApplicationEnum.MAX_MESSAGES_TO_PAUSE_PROCESSING.getEnumType() + " messages received, the adjustment log report:");
        Map<String, Report> reportMap = populateReportMap();
        for (Product p : adjustList) {
            //First adjustment received
            if (p != null && p.getName() != null && p.getName().length() > 0)
                adjust(p, reportMap);
        }
        logFinalAdjustmentReport(reportMap);
    }

    /**
     * Final Adjustment report on the console
     *
     * @param reportMap
     */
    private void logFinalAdjustmentReport(Map<String, Report> reportMap) {
        System.out.println("+------------------------+");
        System.out.println("|Final Adjustment Report|");
        System.out.println("+------------------------+");
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println("|        NAME                         |	 QUANTITY      |   TOTAL  |");
        System.out.println("+-------------------------------------+----------------+----------+");
        for (Map.Entry<String, Report> entry : reportMap.entrySet()) {

            String name = entry.getKey();
            Report r = entry.getValue();
            if (r.getName() != null) {
                System.out.println("|" + (name + padding).substring(0, 37) + "|" + (r.getQuantity() + padding).substring(0, 16) + "|" + ("£" + r.getTotalPrice() / 100 + padding).substring(0, 10) + "|");
            }
        }
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println();
        System.out.println();
    }

    /**
     * Adjustment logic
     *
     * @param p
     * @param reportMap
     */
    private void adjust(Product p, Map<String, Report> reportMap) {

        for (Map.Entry<String, Report> entry : reportMap.entrySet()) {
            Report r = entry.getValue();
            if (r.getName() != null && p.getName().equals(r.getName())) {
                beforeAdjustment(r);
                afterAdjustment(r, p);
            }
        }
    }

    /**
     * Before Adjustment logic
     *
     * @param r
     */
    private void beforeAdjustment(Report r) {
        System.out.println("Before adjustment: " + r.getName());
        logAdjust(r);
    }

    /**
     * After adjustment report
     *
     * @param report
     * @param product
     */
    private void afterAdjustment(Report report, Product product) {
        System.out.println("After adjustment: " + product.getName() + " " + product.getType().getEnumType() + " " + product.getAdjustPrice() + "product");
        switch (product.getType()) {
            case ADDITION:
                report.setTotalPrice(report.getTotalPrice() + (report.getQuantity() * product.getAdjustPrice()));
                logAdjust(report);
                break;
            case MULTIPLICATION:
                report.setTotalPrice(report.getQuantity() * ((report.getTotalPrice() / report.getQuantity()) * product.getAdjustPrice()));
                logAdjust(report);
                break;
            case SUBTRACTION:
                report.setTotalPrice(report.getTotalPrice() - (report.getQuantity() * product.getAdjustPrice()));
                logAdjust(report);
                break;
        }
    }

    /**
     * This is used to print the report
     *
     * @param report
     */
    private void logAdjust(Report report) {
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println("|        NAME                         |	 QUANTITY      |   TOTAL  |");
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println("|" + (report.getName() + padding).substring(0, 37) + "|" + (report.getQuantity() + padding).substring(0, 16) + "|" + ("£" + report.getTotalPrice() / 100 + padding).substring(0, 10) + "|");
        System.out.println("+-------------------------------------+----------------+----------+");
        System.out.println();
    }

    /**
     * Main logic to populate the report
     *
     * @return
     */
    private Map<String, Report> populateReportMap() {

        Map<String, Report> reportMap = new HashMap<String, Report>();
        for (Product p : productList) {
            if (p != null && p.getName() != null && p.getName().length() > 0 && p.getPrice() > 0) {
                Report r = reportMap.get(p.getName());
                if (r != null) {
                    r.setQuantity(r.getQuantity() + p.getQuantity());
                    r.setTotalPrice(r.getTotalPrice() + (r.getQuantity() * p.getPrice()));
                } else {
                    r = new Report(p.getName(), p.getQuantity(), (p.getQuantity() * p.getPrice()));
                    reportMap.put(p.getName(), r);
                }
            }
        }
        return reportMap;
    }

    /**
     * To find the total number of messages
     *
     * @return
     */
    public int messageCount() {
        return productList.size();
    }

    /**
     * To find the total number of adjustment message
     *
     * @return
     */
    public int adjustmentCount() {
        return adjustList.size();
    }

    /**
     * This is used to find total quantity by the product name
     *
     * @param productName
     * @return
     */
    public int getQuantityByName(String productName) {
        int qty = 0;
        Map<String, Report> repMap = populateReportMap();
        Report r = repMap.get(productName);
        if (r != null && r.getName() != null) {
            qty = r.getQuantity();
        }
        return qty;
    }

    /**
     * This is used to find total Sales price by the product name
     *
     * @param productName
     * @return
     */
    public float getTotalSalePriceByName(String productName) {
        float sales = 0;
        Map<String, Report> repMap = populateReportMap();
        Report r = repMap.get(productName);
        if (r != null && r.getName() != null) {
            sales = r.getTotalPrice();
        }
        return sales;
    }
}