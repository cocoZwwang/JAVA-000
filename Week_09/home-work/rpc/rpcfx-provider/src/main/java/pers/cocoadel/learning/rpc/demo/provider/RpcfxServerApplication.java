package pers.cocoadel.learning.rpc.demo.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.cocoade.learning.rpc.api.OrderService;
import pers.cocoade.learning.rpc.api.UserService;
import pers.cocoadel.learning.rpcfx.api.RpcfxRequest;
import pers.cocoadel.learning.rpcfx.api.RpcfxResolver;
import pers.cocoadel.learning.rpcfx.api.RpcfxResponse;
import pers.cocoadel.learning.rpcfx.server.RpcfxInvoker;

@SpringBootApplication
@RestController
public class RpcfxServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(RpcfxServerApplication.class, args);
	}

	@Autowired
	RpcfxInvoker invoker;

	@PostMapping("/")
	public RpcfxResponse invoke(@RequestBody RpcfxRequest request) {
		return invoker.invoke(request);
	}

	@Bean
	public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
		return new RpcfxInvoker(resolver);
	}

	@Bean
	public RpcfxResolver createResolver(){
		return new DemoResolver();
	}

	// 能否去掉name
	//
	@Bean(name = "pers.cocoade.learning.rpc.api.UserService")
	public UserService createUserService(){
		return new UserServiceImpl();
	}

	@Bean(name = "pers.cocoade.learning.rpc.api.OrderService")
	public OrderService createOrderService(){
		return new OrderServiceImpl();
	}

}
