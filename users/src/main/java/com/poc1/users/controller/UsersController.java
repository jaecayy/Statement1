package com.poc1.users.controller;

import com.poc1.users.entities.Orders;
import com.poc1.users.entities.Users;
import com.poc1.users.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public List<Users> getAllUsers(){

        return  usersRepository.findAll();
    }

    @GetMapping("/{usersId}")
    public ResponseEntity<Users> getAllUsers(@PathVariable("usersId") String userId){
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if(!optionalUser.isPresent()){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(optionalUser.get(),HttpStatus.OK);
    }

    @GetMapping("/completeInfo")
    public ResponseEntity<List<Users>> completeInfo(){
       List<Users> listUsers = usersRepository.findAll();
       for(Users user : listUsers){
           System.out.println("user id : ==> "+user.getUserId());
           Orders[] ordersArr = new Orders[0];
           try {
               ordersArr = restTemplate.getForObject("http://localhost:8081/orders/user/" + user.getUserId(), Orders[].class);
           }catch(Exception e){
               e.printStackTrace();
           }
           if(ordersArr.length != 0) {
               List<Orders> listOrders = (List<Orders>) Arrays.stream(ordersArr).collect(Collectors.toList());
               user.setListOrders(listOrders);
           }
       }
       return new ResponseEntity<>(listUsers,HttpStatus.OK);
    }

    @PostMapping("/")
    public Users createUser(@RequestBody Users user){
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        return usersRepository.save(user);
    }

    @PutMapping("/{usersId}")
    public ResponseEntity<Users> updateUserByUserId(@PathVariable("usersId") String userId, @RequestBody Users user){
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if(!optionalUser.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Users getUser = optionalUser.get();
        getUser.setName(user.getName());
        getUser.setAbout(user.getAbout());
        getUser.setEmail(user.getEmail());
        usersRepository.save(getUser);
        return ResponseEntity.ok(getUser);
    }

    @DeleteMapping("/{usersId}")
    public ResponseEntity<String> deleteUserByUsersId(@PathVariable("usersId") String usersId){

        Optional<Users> optionalUser = usersRepository.findById(usersId);
        if(!optionalUser.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Users getUser = optionalUser.get();

        try {
            restTemplate.delete("http://localhost:8081/orders/user/" + getUser.getUserId());
        }catch(Exception e){
            e.printStackTrace();
        }


        int noOfRowsAffected =  usersRepository.retainDataBeforeUserIdDelete(usersId);
        if(noOfRowsAffected ==1){
            return new ResponseEntity<>("Users with this User Id: "+usersId+" is deleted successfully", HttpStatus.OK);
        }else{
            return ResponseEntity.notFound().build();
        }

    }


}
