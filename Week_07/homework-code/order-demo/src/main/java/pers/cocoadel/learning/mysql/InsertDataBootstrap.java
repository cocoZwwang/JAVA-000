package pers.cocoadel.learning.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.learning.mysql.dao.OrderDao;
import pers.cocoadel.learning.mysql.dao.OrderMonthSalesDao;
import pers.cocoadel.learning.mysql.domain.Order;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class InsertDataBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderMonthSalesDao orderMonthSalesDao;


    public static void main(String[] args) {
        SpringApplication.run(InsertDataBootstrap.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
//        insertOrders();
//        statOrders();
        createInsertSqlFile();
    }

    private void statOrders() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2020);
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.MONTH, i);
            Date startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            Date endDate = calendar.getTime();
            orderMonthSalesDao.statOrders(startDate, endDate);
        }
        System.out.printf("统计完成,耗时：%s s\n", (System.currentTimeMillis() - time) / 1000);
    }

    private void insertOrders() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            List<Order> orders = createOrders(i * 1000000 + 1, 100000);
            orderDao.batchSave(orders);
        }
        System.out.printf("插入完成,耗时：%s s\n", (System.currentTimeMillis() - time) / 1000);
    }

    private void createInsertSqlFile() {
        long time = System.currentTimeMillis();
        String filePath = System.getProperty("user.dir") + "/orders_insert.csv";
        System.out.println("filePath: " + filePath);
        try (FileWriter fileWriter = new FileWriter(new File(filePath));) {
//            fileWriter.write("\"id\",\"user_id\",\"product_id\",\"product_amount\",\"state\",\"create_time\",\"update_time\"\n");
            List<Order> orders = createOrders(1, 10000000);
            for (Order order : orders) {
                fileWriter.write(toInsertSql(order));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("生成数据文件完成,耗时：%s s\n", (System.currentTimeMillis() - time) / 1000);

    }

    private String toInsertSql(Order order) {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                order.getId(), order.getUserId(), order.getProductId(), order.getProductAmount(), order.getState(),
                timeToString(order.getCreateTime()), timeToString(order.getUpdateTime()));
    }

    private String timeToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }

    private List<Order> createOrders(int start, int count) {
        List<Order> list = new ArrayList<>();

        for (int i = start; i < start + count; i++) {
            Order order = new Order();

            order.setId((long) i);
            order.setUserId(1 + (long) (Math.random() * 10000));
            order.setProductId(1 + (long) (Math.random() * 2000));
            order.setProductAmount((int) (1 + Math.random() * 10));
            order.setState(0);
            Date date = randDate();
            order.setCreateTime(date);
            order.setUpdateTime(date);

            list.add(order);
        }
        return list;
    }

    private Date randDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 0, 1);
        calendar.add(Calendar.DATE, (int) (Math.random() * 365));
        return calendar.getTime();
    }

    /**
     * 统计每月的销售额，并且存储到中间表
     */
    private void statOrderMonthSales() {

    }
}
