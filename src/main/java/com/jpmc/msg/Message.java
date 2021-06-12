package com.jpmc.msg;

import com.jpmc.domain.Product;

/**
 * This is a contract for Message
 *
 * @author Stanly
 */
public interface Message {

    Product buildMessage();

    boolean validateMessage();
}
