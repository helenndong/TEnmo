package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable int userId) {
        return userDao.getUserById(userId);
    }


}