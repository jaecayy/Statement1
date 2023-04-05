package com.poc1.orders.controller;

import com.poc1.orders.entities.Orders;
import com.poc1.orders.entities.Users;
import com.poc1.orders.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public List<Orders> getAllOrders(){
        
        return ordersRepository.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<Orders> createOrder(@RequestBody Orders order){

        ResponseEntity<Users> response = restTemplate.getForEntity("http://localhost:8080/users/" + order.getUserId(), Users.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ordersRepository.save(order));
        }
        else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Orders> getOrder(@PathVariable("orderId") Integer orderId){

        Optional<Orders> optionalOrder = ordersRepository.findById(orderId);
        if(!optionalOrder.isPresent()){
           return ResponseEntity.notFound().build();
        }

        Orders order = optionalOrder.get();
        return ResponseEntity.ok(order);

    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Orders>> getOrder(@PathVariable("userId") String userId){

        Optional<List<Orders>> optionalOrder = ordersRepository.findByUserId(userId);
        if(!optionalOrder.isPresent()){
            return  ResponseEntity.notFound().build();
        }

        List<Orders> orders = optionalOrder.get();
        return ResponseEntity.ok(orders);

    }

    //update the user by using order
    @PutMapping("/{orderId}")
    public ResponseEntity<Users> updateUserDetailsUsingOrderId(@PathVariable("orderId") Integer orderId, @RequestBody Users user){

        Optional<Orders> optionalOrder = ordersRepository.findById(orderId);
        if(!optionalOrder.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Orders order = optionalOrder.get();

        ResponseEntity<Users> response = restTemplate.getForEntity("http://localhost:8080/users/" + order.getUserId(), Users.class);
        if(response.getStatusCode() == HttpStatus.OK) {
//            Users responseUser = response.getBody();
            Users newUser = Users.builder().name(user.getName()).email(user.getEmail()).about(user.getAbout()).build();

            restTemplate.put("http://localhost:8080/users/"+ order.getUserId(),newUser);
            return new ResponseEntity<>(newUser,HttpStatus.CREATED);
        }
        else{
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId){

        Integer noOfRowsDeleted = ordersRepository.retainDataBeforeOrderIdDelete(orderId);
        if (noOfRowsDeleted.intValue()!=1) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>("Order related to this Order Id :"+orderId+" is deleted successfully", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteOrderByUserId(@PathVariable("userId") String userId){

       Optional<List<Orders>> listOrders = ordersRepository.findByUserId(userId);
       if(listOrders.get().size()!=0) {
           Integer noOfRowsDeleted = ordersRepository.retainDataBeforeUserIdDelete(userId);
           if (noOfRowsDeleted.intValue() == 0) {
               return ResponseEntity.notFound().build();
           }else{
               return new ResponseEntity<>("Orders with this User Id: "+userId+" is deleted successfully", HttpStatus.OK);
           }
       }
        return new ResponseEntity<>("Orders with given user id not exists!!",HttpStatus.NOT_FOUND);
    }


}
