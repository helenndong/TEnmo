package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> findAllUsers(){
        return userDao.findAll();
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.GET)
    public User findByUsername(String username){
        return userDao.findByUsername(username);
    }

    @RequestMapping(path = "/username/{id}", method = RequestMethod.GET)
    public int findIdByUsername(String username){
        return userDao.findIdByUsername(username);
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable int userId) {
        return userDao.getUserById(userId);
    }

//    @GetMapping("/users/{userId}/username")
//    public String getUsernameById(@PathVariable int userId) {
//        // Retrieve the username for the given userId from your data source (e.g., database)
//        // Return the username
//
//    }

}