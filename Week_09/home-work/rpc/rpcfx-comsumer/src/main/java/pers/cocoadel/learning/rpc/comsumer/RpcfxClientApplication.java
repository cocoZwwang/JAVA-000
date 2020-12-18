package pers.cocoadel.learning.rpc.comsumer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import pers.cocoade.learning.rpc.api.Order;
import pers.cocoade.learning.rpc.api.OrderService;
import pers.cocoade.learning.rpc.api.User;
import pers.cocoade.learning.rpc.api.UserService;
import pers.cocoadel.learning.rpcfx.client.Rpcfx;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RpcfxClientApplication {

    // 二方库
    // 三方库 lib
    // nexus, userserivce -> userdao -> user
    //

    public static void main(String[] args) {

        // UserService service = new xxx();
        // service.findById
        for(int i = 0; i < 100; i++){
            System.out.println("---------------------------------> " + i);
            UserService userService = Rpcfx.create(UserService.class, "http://localhost:8080/");
            User user = userService.findById(i);
            System.out.println("find user id=1 from server: " + user.getName());

            OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
            Order order = orderService.findOrderById(i);
            System.out.println(String.format("find order name=%s, amount=%f", order.getName(), order.getAmount()));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 新加一个OrderService

//		SpringApplication.run(RpcfxClientApplication.class, args);
    }

}
