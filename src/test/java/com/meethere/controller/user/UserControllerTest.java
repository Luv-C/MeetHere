package com.meethere.controller.user;


import com.meethere.entity.User;
import com.meethere.service.UserService;
//import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    //对Http请求的模拟，能够直接使用网络的形式，转换到Controller的调用
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    //测试signup()函数
    @Test
    public void return_sign_up_html() throws Exception{
        ResultActions perform=mockMvc.perform(get("/signup"));
        perform.andExpect(status().isOk());
        ModelAndView mv = perform.andReturn().getModelAndView();
        assertEquals(mv.getViewName(),"signup");
    }
    //测试login()函数
    @Test
    public void return_login_html() throws Exception{
        //mockMvc.perform(get("/login")).andExpect(status().isOk());
        ResultActions perform=mockMvc.perform(get("/login"));
        perform.andExpect(status().isOk());
        ModelAndView mv = perform.andReturn().getModelAndView();
        assertEquals(mv.getViewName(),"login");
    }
    //测试login(String userID,String password, HttpServletRequest request)函数
    @Test
    public void user_does_not_exist_or_wrong_password() throws Exception{
        when(userService.checkLogin(anyString(),anyString())).thenReturn(null);
        ResultActions perform=mockMvc.perform(post("/loginCheck.do").param("userId","noUser").param("password","noPassword"));
        perform.andExpect(status().isOk()).andExpect(content().string("false"));
    }
    @Test
    public void user_login_succeeded() throws Exception{
        User user=new User();
        user.setUserID("test");
        user.setPassword("test");
        user.setIsadmin(0);
        when(userService.checkLogin(anyString(),anyString())).thenReturn(user);
        ResultActions perform=mockMvc.perform(post("/loginCheck.do").param("userID","test").param("password","test"));
        perform.andExpect(status().isOk()).andExpect(content().string("/index"));
    }
    @Test
    public void admin_login_succeeded() throws Exception{
        User user=new User();
        user.setUserID("admin");
        user.setPassword("admin");
        user.setIsadmin(1);
        when(userService.checkLogin(anyString(),anyString())).thenReturn(user);
        ResultActions perform=mockMvc.perform(post("/loginCheck.do").param("userID","admin").param("password","admin"));
        perform.andExpect(status().isOk()).andExpect(content().string("/admin_index"));
    }
    @Test
    public void invalid_Isadmin() throws Exception{
        User user=new User();
        user.setUserID("admin");
        user.setPassword("admin");
        user.setIsadmin(5);
        when(userService.checkLogin(anyString(),anyString())).thenReturn(user);
        ResultActions perform=mockMvc.perform(post("/loginCheck.do").param("userID","admin").param("password","admin"));
        perform.andExpect(status().isOk()).andExpect(content().string("false"));
    }
    //测试register(String userID,String userName, String password, String email, String phone,HttpServletResponse response)函数
    @Test
    public void register_a_new_user() throws Exception{
        ResultActions perform=mockMvc.perform(post("/register.do").param("userID","user").param("userName","name").param("password","password")
                .param("email","email").param("phone","phone"));
        perform.andExpect(redirectedUrl("/login"));
        verify(userService).create(any());
    }
    //测试logout(HttpServletRequest request, HttpServletResponse response)函数
    @Test
    public void user_logout() throws Exception{
        ResultActions perform=mockMvc.perform(get("/logout.do"));
        perform.andExpect(redirectedUrl("/index"));
    }
    //测试quit(HttpServletRequest request, HttpServletResponse response)函数
    @Test
    public void admin_quit() throws Exception{
        ResultActions perform=mockMvc.perform(get("/quit.do"));
        perform.andExpect(redirectedUrl("/index"));
    }
    //测试updateUser(String userName, String userID, String passwordNew,String email, String phone, MultipartFile picture,HttpServletRequest request, HttpServletResponse response)函数
    @Test
    public void user_update_passwordNew_is_null_and_picture_is_null() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", (String) null)
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    @Test
    public void user_update_passwordNew_is__and_picture_is_null() throws Exception{
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", "")
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    @Test
    public void user_update_password_exists_and_picture_is_null() throws Exception{
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", "passwordNew")
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    @Test
    public void user_update_passwordNew_is_null_and_picture_exists() throws Exception{
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","1.bmp",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", (String) null)
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    @Test
    public void user_update_passwordNew_is__and_picture_exists() throws Exception{
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","1.bmp",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", "")
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    @Test
    public void user_update_passwordNew_and_picture_exist() throws Exception{
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","1.bmp",
                "picture", "".getBytes());
        when(userService.findByUserID(anyString())).thenReturn(new User());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/updateUser.do").file(mockMultipartFile).param("userID","user").param("userName","name")
                        .param("passwordNew", "passwordNew")
                        .param("email","email").param("phone","phone");

        ResultActions perform=mockMvc.perform(builder);
        perform.andExpect(redirectedUrl("user_info"));
        verify(userService).findByUserID(anyString());
        verify(userService).updateUser(any());
    }
    //测试checkPassword函数
    @Test
    public void true_password() throws Exception{
        User user=new User();
        user.setUserID("User");
        user.setPassword("password");
        when(userService.findByUserID("User")).thenReturn(user);
        ResultActions perform=mockMvc.perform(get("/checkPassword.do").param("userID","User").param("password","password"));
        perform.andExpect(status().isOk()).andExpect(content().string("true"));
    }
    @Test
    public void wrong_password() throws Exception{
        User user=new User();
        user.setUserID("User");
        user.setPassword("password");
        when(userService.findByUserID("User")).thenReturn(user);
        ResultActions perform=mockMvc.perform(get("/checkPassword.do").param("userID","User").param("password","pass"));
        perform.andExpect(status().isOk()).andExpect(content().string("false"));
    }
    //测试user_info函数
    @Test
    public void return_user_info_html() throws Exception{
        mockMvc.perform(get("/user_info").sessionAttr("user",new User())).andExpect(status().isOk());
    }

}
