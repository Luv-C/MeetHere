package com.meethere.UnitTest.Service.impl;

import com.meethere.MeetHereApplication;
import com.meethere.dao.UserDao;
import com.meethere.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MeetHereApplication.class)
public class UserServiceImplTest {
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;

    //findByUserID方法测试
    @Test
    void return_user_by_UserID(){
        //待测数据
        int id=1;
        String userID="user";
        String password="password";
        String email="222@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="nickname";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);

        //设定mock-UserDao的findByUserID函数
        when(userDao.findByUserID(userID)).thenReturn(user);

        User res=userService.findByUserID(userID);

        //断言处理
        assertAll("test find by userID",()->assertEquals(id,res.getId()),
                ()->assertEquals(userID,res.getUserID()),
                ()->assertEquals(password,res.getPassword()),
                ()->assertEquals(email,res.getEmail()),
                ()->assertEquals(phone,res.getPhone()),
                ()->assertEquals(isadmin,res.getIsadmin()),
                ()->assertEquals(user_name,res.getUserName()),
                ()->assertEquals(picture,res.getPicture()));
        //验证我们设定的userDao.findByUserID是否被调用
        verify(userDao).findByUserID(userID);
    }

    //findById方法测试
    @Test
    void return_User_by_id(){
        int id=2;
        String userID="user";
        String password="password";
        String email="222@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="u";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);

        //设定mock-UserDao的findByID函数
        when(userDao.findById(id)).thenReturn(user);

        User res=userService.findById(id);

        //断言处理
        assertAll("test find by id",()->assertEquals(id,res.getId()),
                ()->assertEquals(userID,res.getUserID()),
                ()->assertEquals(password,res.getPassword()),
                ()->assertEquals(email,res.getEmail()),
                ()->assertEquals(phone,res.getPhone()),
                ()->assertEquals(isadmin,res.getIsadmin()),
                ()->assertEquals(user_name,res.getUserName()),
                ()->assertEquals(picture,res.getPicture()));
        //验证我们设定的userDao.findByUserID是否被调用
        verify(userDao).findById(id);
    }

    //findByUserID（pageable）方法测试
    @Test
    void return_user_list_paged() {
        Pageable pageable= PageRequest.of(0,10);
        when(userDao.findAllByIsadmin(0,pageable)).thenReturn(null);
        userService.findByUserID(pageable);
        verify(userDao).findAllByIsadmin(0,pageable);
    }
    //checkLogin方法测试
    @Test
    void check_userID_password_matched(){
        int id=2;
        String userID="user";
        String password="password";
        String email="222@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="u";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);

        when(userDao.findByUserIDAndPassword(userID,password)).thenReturn(user);

        User res=userService.checkLogin(userID,password);

        assertAll("test find by userID",()->assertEquals(id,res.getId()),
                ()->assertEquals(userID,res.getUserID()),
                ()->assertEquals(password,res.getPassword()),
                ()->assertEquals(email,res.getEmail()),
                ()->assertEquals(phone,res.getPhone()),
                ()->assertEquals(isadmin,res.getIsadmin()),
                ()->assertEquals(user_name,res.getUserName()),
                ()->assertEquals(picture,res.getPicture()));
        verify(userDao).findByUserIDAndPassword(userID,password);
    }

    //create方法测试
    @Test
    void user_register(){
        int id=1;
        String userID="user";
        String password="password";
        String email="222@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="nickname";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);

        when(userDao.save(user)).thenReturn(null);
        userService.create(user);
        verify(userDao).save(user);
        verify(userDao).findAll();
    }
    //delByID(int id)方法测试
    @Test
    void del_user_by_id() {
        userService.delByID(1);
        verify(userDao).deleteById(1);

        userService.delByID(-1);
        verify(userDao).deleteById(-1);

        verify(userDao,times(2)).deleteById(anyInt());
    }
    //updateUser(User user)方法测试
    @Test
    void update_user_info() {
        int id=1;
        String userID="user";
        String password="password";
        String email="222@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="nickname";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);

        when(userDao.save(user)).thenReturn(null);
        userService.updateUser(user);
        verify(userDao).save(user);
    }
    //countUserID(String userID)方法测试
    @Test
    void return_number_of_same_userID() {
        String userID="user";
        when(userDao.countByUserID(userID)).thenReturn(1).thenReturn(2);
        int res1=userService.countUserID(userID);
        assertEquals(1,res1);
        int res2=userService.countUserID(userID);
        assertEquals(2,res2);
        verify(userDao,times(2)).countByUserID(userID);
    }

}
