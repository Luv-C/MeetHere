package com.meethere.controller.user;


import com.meethere.entity.Message;
import com.meethere.entity.User;
import com.meethere.service.MessageService;
import com.meethere.service.MessageVoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MessageService messageService;
    @MockBean
    private MessageVoService messageVoService;

    //测试String message_list(Model model,HttpServletRequest request)函数
    @Test
    public void fail_to_return_message_list() throws Exception{
        //message创建
        int id=1;
        LocalDateTime ldt1=LocalDateTime.now().minusDays(1);
        Message message=new Message(id,"user","this is a leave message", ldt1,1);
        List<Message> messages = new ArrayList<>();
        Pageable message_pageable= PageRequest.of(0,5, Sort.by("time").descending());
        messages.add(message);
        //前提条件
        when(messageService.findPassState(any())).thenReturn(new PageImpl<>(messages,message_pageable,1));
        //判断是否触发断言以及断言内容是否正确
        assertThrows(NestedServletException.class,()->mockMvc.perform(get("/message_list")),"请登录！");
    }

    @Test
    public void succeed_to_return_message_list() throws Exception{
        //message创建
        int id=1;
        LocalDateTime ldt1=LocalDateTime.now().minusDays(1);
        Message message=new Message(id,"user","this is a leave message", ldt1,1);
        List<Message> messages=new ArrayList<>();
        messages.add(message);
        //user创建
        String userID="test";
        String password="123";
        String email="test@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="TEST";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);
        //前提条件
        Pageable message_pageable=PageRequest.of(0,5,Sort.by("time").descending());
        Pageable user_message_pageable=PageRequest.of(0,5,Sort.by("time").descending());
        when(messageService.findPassState(any())).thenReturn(new PageImpl<>(messages,message_pageable,1));
        when(messageVoService.returnVo(any())).thenReturn(null);
        when(messageService.findByUser(userID,user_message_pageable)).thenReturn(new PageImpl<>(messages,message_pageable,1));
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/message_list").sessionAttr("user",user));
        ModelAndView mv=perform.andReturn().getModelAndView();
        //验证
        perform.andExpect(status().isOk());
        assertModelAttributeAvailable(mv,"total");
        assertModelAttributeAvailable(mv,"user_total");
        verify(messageService).findPassState(message_pageable);
        verify(messageVoService).returnVo(any());
        verify(messageService).findByUser(userID,user_message_pageable);
        assertEquals(mv.getViewName(),"message_list");
    }
    //测试List<MessageVo> message_list(@RequestParam(value = "page",defaultValue = "1")int page)函数
    @Test
    public void return_pass_state_message_list() throws Exception{
        //message创建
        int id=1;
        LocalDateTime ldt1=LocalDateTime.now().minusDays(1);
        Message message=new Message(id,"user","this is a leave message", ldt1,1);
        List<Message> messages=new ArrayList<>();
        messages.add(message);
        //前提条件
        Pageable message_pageable= PageRequest.of(0,10, Sort.by("time").descending());
        when(messageService.findPassState(any())).thenReturn(new PageImpl<>(messages,message_pageable,1));
        when(messageVoService.returnVo(messages)).thenReturn(null);
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/message/getMessageList"));
        //验证
        perform.andExpect(status().isOk());
        verify(messageService).findPassState(any());
        verify(messageVoService).returnVo(any());
    }
    //测试List<MessageVo> user_message_list(@RequestParam(value = "page",defaultValue = "1")int page,HttpServletRequest request)函数
    @Test
    public void fail_to_return_user_message_list()throws Exception{
        //message创建
        int id=1;
        LocalDateTime ldt1=LocalDateTime.now().minusDays(1);
        Message message=new Message(id,"user","this is a leave message", ldt1,1);
        List<Message> messages = new ArrayList<>();
        Pageable message_pageable= PageRequest.of(0,5, Sort.by("time").descending());
        messages.add(message);
        //前提条件
        when(messageService.findPassState(any())).thenReturn(new PageImpl<>(messages,message_pageable,1));
        //判断是否触发断言以及断言内容是否正确
        assertThrows(NestedServletException.class,()->mockMvc.perform(get("/message/findUserList")),"请登录！");
    }
    @Test
    public void succeed_to_return_user_message_list() throws Exception{
        //message创建
        int id=1;
        LocalDateTime ldt1=LocalDateTime.now().minusDays(1);
        Message message=new Message(id,"user","this is a leave message", ldt1,1);
        List<Message> messages=new ArrayList<>();
        messages.add(message);
        //user创建
        String userID="test";
        String password="123";
        String email="test@qq.com";
        String phone="12345678901";
        int isadmin=0;
        String user_name="TEST";
        String picture="picture";
        User user=new User(id,userID,user_name,password,email,phone,isadmin,picture);
        //前提条件
        Pageable user_message_pageable=PageRequest.of(0,5,Sort.by("time").descending());
        when(messageVoService.returnVo(any())).thenReturn(null);
        when(messageService.findByUser(userID,user_message_pageable)).thenReturn(new PageImpl<>(messages,user_message_pageable,1));
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/message/findUserList").sessionAttr("user",user));
        //验证
        perform.andExpect(status().isOk());
        verify(messageVoService).returnVo(any());
        verify(messageService).findByUser(userID,user_message_pageable);
    }
    //测试void sendMessage(String userID, String content, HttpServletResponse response)函数
    @Test
    public void user_add_new_message() throws Exception{
        when(messageService.create(any())).thenReturn(1);
        ResultActions perform=mockMvc.perform(post("/sendMessage").param("userID","user").param("content","this is content"));
        perform.andExpect(redirectedUrl("/message_list"));
        verify(messageService).create(any());
    }
    //测试boolean modifyMessage(int messageID,String content, HttpServletResponse response)函数
    @Test
    public void user_modify_message()throws Exception {
        when(messageService.findById(anyInt())).thenReturn(new Message());
        ResultActions perform=mockMvc.perform(post("/modifyMessage.do").param("messageID","1").param("userID","user").param("content","this is content"));
        perform.andExpect(content().string("true"));
        verify(messageService).update(any());
    }
    //测试boolean delMessage(int messageID)函数
    @Test
    public void user_del_message()throws Exception {
        ResultActions perform=mockMvc.perform(post("/delMessage.do").param("messageID","1"));
        perform.andExpect(content().string("true"));
        verify(messageService).delById(anyInt());
    }

}
