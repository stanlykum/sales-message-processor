package com.jpmc.msg;

import com.jpmc.domain.ApplicationEnum;
import com.jpmc.domain.Product;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for below operations.
 * 1.Validate the incoming messages
 * 2.Applying adjustment with respect to Add,Subtract and Multiply
 *
 * @author Stanly
 */
public class MessageImpl implements Message {

    private final Logger logger = Logger.getLogger(MessageImpl.class.getName());
    private String msg = null;
    private List<String> listOfMsgType = null;

    public MessageImpl(String msg) {

        this.msg = msg;
        String[] validMsg = ApplicationEnum.MSG_VALID_TYPE.getEnumType().split(",");
        listOfMsgType = Arrays.asList(validMsg);
    }

    /**
     * Validating the message and captured in logs
     *
     * @return
     */
    @Override
    public boolean validateMessage() {

        if (msg == null) {
            logger.log(Level.WARNING, "Message is null");
            return false;
        }

        if (msg.length() < Integer.parseInt(ApplicationEnum.MIN_MSG_LENGTH.getEnumType())) {

            logger.log(Level.WARNING, "Invalid message length");
            return false;
        }

        if (!isValid(false)) {
            logger.log(Level.WARNING, "Message is not valid.");
            return false;
        }
        return true;
    }

    /**
     * Main logic to build the message and apply adjustment.
     *
     * @return Product domain
     */
    @Override
    public Product buildMessage() {

        Product p = new Product();
        addType(p);
        return p;
    }

    private void addType(Product p) {

        if (isStartWith(p)) {
            //Adjustment received.
        } else if (Character.isDigit(msg.charAt(0))) {
            p.setType(ApplicationEnum.RECORD);
            recordSales(p);
        } else if (isValid(false)) {
            p.setType(ApplicationEnum.LOG);
        } else {
            p.setType(ApplicationEnum.PROCESS);
        }
    }

    /**
     * This is used to record the quantity, price and name of the product
     *
     * @param p
     */
    private void recordSales(Product p) {

        StringTokenizer token = new StringTokenizer(msg, " ");
        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            if (isQty(s)) {
                p.setQuantity(Integer.parseInt(s));
            } else if (checkProductType(s)) {
                p.setName(s.substring(0, s.length() - 1));
            } else if (isPrice(s)) {
                float price = 0;
                try {
                    price = Float.parseFloat(s.substring(0, s.length() - 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                p.setPrice(price);
            }
        }
    }

    /**
     * Validating a string to find if it is number.
     *
     * @param s
     * @return
     */
    private boolean isQty(String s) {

        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }

    /**
     * Validate Product type
     *
     * @param token
     * @return boolean
     */
    private boolean checkProductType(String token) {

        for (String s : listOfMsgType) {
            if (token.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate the price to find if it is valid digit
     *
     * @param str
     * @return
     */
    private boolean isPrice(String str) {
        return Character.isDigit(str.charAt(0));
    }

    /**
     * Validate the message which are valid message type
     *
     * @param isStartWith
     * @return
     */
    private boolean isValid(boolean isStartWith) {

        for (String s : listOfMsgType) {
            if (isStartWith) {
                if (msg.startsWith(s)) {
                    return true;
                }
            } else {
                if (isContainKeyWord(msg, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This is used to compare the incoming message type with valid message type in
     * application constant.
     *
     * @param msg
     * @param match
     * @return
     */
    private boolean isContainKeyWord(String msg, String match) {
        String pattern = "\\b" + match + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(msg);
        return m.find();
    }

    /**
     * Adjustment logic with respect to operation type
     *
     * @param p
     * @return
     */
    private boolean isStartWith(Product p) {

        boolean flag = false;

        if (msg.startsWith(ApplicationEnum.ADDITION.getEnumType())) {
            p.setType(ApplicationEnum.ADDITION);
            adjustProduct(p);
            flag = true;
        } else if (msg.startsWith(ApplicationEnum.MULTIPLICATION.getEnumType())) {
            p.setType(ApplicationEnum.MULTIPLICATION);
            adjustProduct(p);
            flag = true;
        } else if (msg.startsWith(ApplicationEnum.SUBTRACTION.getEnumType())) {
            p.setType(ApplicationEnum.SUBTRACTION);
            adjustProduct(p);
            flag = true;
        }
        return flag;
    }

    /**
     * Adjustment logic
     *
     * @param p
     */
    private void adjustProduct(Product p) {

        StringTokenizer token = new StringTokenizer(msg, " ");
        token.nextToken();
        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            if (Character.isDigit(s.charAt(0))) {
                try {
                    p.setAdjustPrice(Float.parseFloat(s));
                    break;
                } catch (NumberFormatException e) {
                    try {
                        p.setAdjustPrice(Float.parseFloat(s.substring(0, s.length() - 1)));
                    } catch (NumberFormatException ne) {
                        if (p.getType().getEnumType().equals(ApplicationEnum.MULTIPLICATION.getEnumType()))
                            p.setAdjustPrice(1);
                        else
                            p.setAdjustPrice(0);
                    }
                }
            } else {
                p.setName(s.substring(0, s.length() - 1));
            }
        }
    }
}