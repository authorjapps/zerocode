package org.jsmart.zerocode.zerocodejavaexec;

import org.jsmart.zerocode.zerocodejavaexec.pojo.Order;

public class OrderCreator {

    // --------------------------------------------------------------------
    // 'Order' pojo can hold as many parameters/fields,
    // then from the test case, you pass-
    // "request":{
    //     "itemName" : "Mango",
    //     "quantity" : 15000
    //     "param1" : "value1"
    //     "param2" : "value2"
    // }
    // -or-
    // Also you can simply pass a Map or HashMap too holding as many params
    // --------------------------------------------------------------------
    public Order createOrder(Order order){

        /**
         * TODO- Suppose you process the "order" received, and finally return the "orderProcessed".
         * Here it is hardcoded for simplicity and understanding purpose only
         */
        Order orderProcessed = new Order(1020301, order.getItemName(), order.getQuantity());

        return orderProcessed;
    }


}
