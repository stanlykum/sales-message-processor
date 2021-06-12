**The problem**

Implement a small message processing application that satisfies the below requirements for
processing sales notification messages. You should assume that an external company will be sending
you the input messages, but for the purposes of this exercise you are free to define the interfaces.

**Processing requirements**
1. All sales must be recorded
2. All messages must be processed
3. After every 10th message received your application should log a report detailing the number
of sales of each product and their total value.
4. After 50 messages your application should log that it is pausing, stop accepting new
messages and log a report of the adjustments that have been made to each sale type while
the application was running.
   
**Sales and Messages**
1. A sale has a product type field and a value – you should choose sensible types for these.
2. Any number of different product types can be expected. There is no fixed set.
3. A message notifying you of a sale could be one of the following types
4. Message Type 1 – contains the details of 1 sale E.g apple at 10p
5. Message Type 2 – contains the details of a sale and the number of occurrences of
that sale. E.g 20 sales of apples at 10p each.
6. Message Type 3 – contains the details of a sale and an adjustment operation to be
applied to all stored sales of this product type. Operations can be add, subtract, or
multiply e.g Add 20p apples would instruct your application to add 20p to each sale
of apples you have recorded.