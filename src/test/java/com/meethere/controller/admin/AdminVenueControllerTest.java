package com.meethere.controller.admin;

import com.meethere.entity.Venue;
import com.meethere.service.VenueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminVenueController.class)
public class AdminVenueControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VenueService venueService;

    //测试函数String venue_manage(Model model)
    @Test
    public void return_venue_manage_html()throws Exception{
        //创建venue
        int venueID=1;
        String venue_name="venue";
        String description="this is description";
        int price=100;
        String picture="";
        String address="address";
        String open_time="08:00";
        String close_time="18:00";
        Venue venue=new Venue(venueID,venue_name,description,price,picture,address,open_time,close_time);
        List<Venue> venues=new ArrayList<>();
        venues.add(venue);
        Pageable pageable= PageRequest.of(0,10, Sort.by("venueID").ascending());
        //前提
        when(venueService.findAll(pageable)).thenReturn(new PageImpl<>(venues,pageable,1));
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/venue_manage"));

        ModelAndView mv = perform.andReturn().getModelAndView();
        //验证
        perform.andExpect(status().isOk());
        assertModelAttributeAvailable(mv,"total");
        verify(venueService).findAll(pageable);
        assertEquals(mv.getViewName(),"admin/venue_manage");
    }
    //测试函数String editVenue(Model model,int venueID)
    @Test
    public void return_edit_venue_html()throws Exception{
        //创建venue
        int venueID=1;
        String venue_name="venue";
        String description="this is description";
        int price=100;
        String picture="";
        String address="address";
        String open_time="08:00";
        String close_time="18:00";
        Venue venue=new Venue(venueID,venue_name,description,price,picture,address,open_time,close_time);
        List<Venue> venues=new ArrayList<>();
        venues.add(venue);
        //前提
        when(venueService.findByVenueID(venueID)).thenReturn(venue);
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/venue_edit").param("venueID","1"));

        ModelAndView mv=perform.andReturn().getModelAndView();
        //验证
        perform.andExpect(status().isOk());
        assertModelAttributeAvailable(mv,"venue");
        verify(venueService).findByVenueID(venueID);
        assertEquals(mv.getViewName(),"/admin/venue_edit");
    }
    //测试函数venue_add()
    @Test
    public void return_add_venue_html()throws Exception{
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/venue_add"));

        ModelAndView mv=perform.andReturn().getModelAndView();
        //验证
        perform.andExpect(status().isOk());
        assertEquals(mv.getViewName(),"/admin/venue_add");
    }
    //测试函数List<Venue> getVenueList(@RequestParam(value = "page",defaultValue = "1")int page)
    @Test
    public void return_venue_list() throws Exception{
        //创建venue
        int venueID=1;
        String venue_name="venue";
        String description="this is description";
        int price=100;
        String picture="";
        String address="address";
        String open_time="08:00";
        String close_time="18:00";
        Venue venue=new Venue(venueID,venue_name,description,price,picture,address,open_time,close_time);
        List<Venue> venues=new ArrayList<>();
        venues.add(venue);
        Pageable pageable= PageRequest.of(0,10, Sort.by("venueID").ascending());
        //前提
        when(venueService.findAll(pageable)).thenReturn(new PageImpl<>(venues,pageable,1));
        //模拟请求
        ResultActions perform=mockMvc.perform(get("/venueList.do").param("page","1"));
        //验证
        perform.andExpect(status().isOk());
        verify(venueService).findAll(pageable);
    }
    //测试函数void addVenue(String venueName, String address, String description,
    //                         int price, MultipartFile picture, String open_time,String close_time,HttpServletRequest request,
    //                         HttpServletResponse response)
    @Test
    public void add_venue_success_and_picture_is_empty() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        //前提
        when(venueService.create(any())).thenReturn(1);
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/addVenue.do")
                        .file(mockMultipartFile).param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_manage"));
        verify(venueService).create(any());
    }
    @Test
    public void add_venue_success_and_picture_exists() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","1.bmp",
                "picture", "1.bmp".getBytes());
        //前提
        when(venueService.create(any())).thenReturn(1);
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/addVenue.do")
                        .file(mockMultipartFile).param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_manage"));
        verify(venueService).create(any());
    }
    @Test
    public void add_venue_failed_and_picture_is_empty() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        //前提
        when(venueService.create(any())).thenReturn(-1);
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/addVenue.do")
                        .file(mockMultipartFile).param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_add"));
        verify(venueService).create(any());

    }
    @Test
    public void add_venue_failed_and_picture_exists() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "1.bmp".getBytes());
        //前提
        when(venueService.create(any())).thenReturn(-1);
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/addVenue.do")
                        .file(mockMultipartFile).param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_add"));
        verify(venueService).create(any());
    }
    //测试函数void modifyVenue(int venueID,String venueName, String address, String description,
    //                            int price, MultipartFile picture, String open_time,String close_time,HttpServletRequest request,
    //                            HttpServletResponse response)
    @Test
    public void modify_venue_and_picture_is_empty() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","",
                "picture", "".getBytes());
        //前提
        when(venueService.findByVenueID(anyInt())).thenReturn(new Venue());
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/modifyVenue.do")
                        .file(mockMultipartFile).param("venueID","1").param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_manage"));
        verify(venueService).findByVenueID(anyInt());
        verify(venueService).update(any());
    }
    @Test
    public void modify_venue_and_picture_exists() throws Exception{
        //创建照片
        MockMultipartFile mockMultipartFile = new MockMultipartFile("picture","1.bmp",
                "picture", "1.bmp".getBytes());
        //前提
        when(venueService.findByVenueID(anyInt())).thenReturn(new Venue());
        //模拟请求
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/modifyVenue.do")
                        .file(mockMultipartFile).param("venueID","1").param("venueName","venue").param("address","this is address")
                        .param("description","this is description").param("price","100")
                        .param("open_time","08:00").param("close_time","18:00");

        ResultActions perform=mockMvc.perform(builder);
        //验证
        perform.andExpect(redirectedUrl("venue_manage"));
        verify(venueService).findByVenueID(anyInt());
        verify(venueService).update(any());
    }
    //测试函数boolean delVenue(int venueID)
    @Test
    public void admin_del_venue() throws Exception {
        //模拟请求
        ResultActions perform=mockMvc.perform(post("/delVenue.do").param("venueID","1"));

        //验证
        perform.andExpect(content().string("true"));
        perform.andExpect(status().isOk());
        verify(venueService).delById(anyInt());
    }
    //测试函数boolean checkVenueName(String venueName)
    @Test
    public void check_venue_name_pass() throws Exception{
        when(venueService.countVenueName("venue")).thenReturn(0);
        ResultActions perform=mockMvc.perform(post("/checkVenueName.do").param("venueName","venue"));
        perform.andExpect(status().isOk());
        perform.andExpect(content().string("true"));
        verify(venueService).countVenueName("venue");
    }
    @Test
    public void check_venue_name_fail() throws Exception{
        when(venueService.countVenueName("venue")).thenReturn(1);
        ResultActions perform=mockMvc.perform(post("/checkVenueName.do").param("venueName","venue"));
        perform.andExpect(status().isOk());
        perform.andExpect(content().string("false"));
        verify(venueService).countVenueName("venue");
    }
}
