package pers.cocoadel.learning.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.learning.mysql.dao.OrderDao;
import pers.cocoadel.learning.mysql.domain.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class InsertDataBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    OrderDao orderDao;


    public static void main(String[] args) {
        SpringApplication.run(InsertDataBootstrap.class,args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        long time = System.currentTimeMillis();
        for(int i = 0; i < 10; i++){
            List<Order> orders = createOrders(i * 1000000 + 1,100000);
            orderDao.batchSave(orders);
        }

//        List<Order> orders = createOrders(1,10);
//        orderDao.batchSave(orders);
        System.out.printf("插入完成,耗时：%s s\n",(System.currentTimeMillis() - time) / 1000);
    }

    private List<Order> createOrders(int start, int count){
        List<Order> list = new ArrayList<>();

        for(int i = start; i < start + count; i++){
            Order order = new Order();

            order.setId((long) i);
            order.setUserId(1 + (long)(Math.random() * 10000));
            order.setProductId(1 + (long)(Math.random() * 2000));
            order.setProductAmount((int) (1 + Math.random() * 10));
            order.setState(0);
            Date date = randDate();
            order.setCreateTime(date);
            order.setUpdateTime(date);

            list.add(order);
        }
        return list;
    }

    private Date randDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020,0,1);
        calendar.add(Calendar.DATE, (int) (Math.random() * 365));
        return  calendar.getTime();
    }
}
