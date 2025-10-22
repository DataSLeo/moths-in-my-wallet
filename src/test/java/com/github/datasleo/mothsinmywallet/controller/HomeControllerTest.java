package com.github.datasleo.mothsinmywallet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;


@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

//-------------------------------------------------------------------------------------------
// Scenario 01: The HomeController using verb get must returns 200 and view called index.
//-------------------------------------------------------------------------------------------

    @Test
    public void MustReturnStatus200AndViewIndex() throws Exception {

        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));

    }

}
