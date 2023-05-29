/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.kryom.wallets_backend.mapper.UserMapperImpl;
import xyz.kryom.wallets_backend.service.AppService;
import xyz.kryom.wallets_backend.web.dto.UserSaveDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tomas Toth
 */

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class UserControllerIT {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper = new ObjectMapper();
  @Mock
  private AppService appServiceMock;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new UserController(appServiceMock, new UserMapperImpl()))
        .build();
  }

  @Test
  void testCreatingUser() throws Exception {

    UserSaveDto userSaveDto = new UserSaveDto("tomas", "password");
    String json = objectMapper.writeValueAsString(userSaveDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users/")
            .content(json).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
    verify(appServiceMock, times(1)).saveUser(any());
  }
}