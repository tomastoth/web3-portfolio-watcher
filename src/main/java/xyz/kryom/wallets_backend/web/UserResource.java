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

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kryom.wallets_backend.exceptions.InvalidUserError;
import xyz.kryom.wallets_backend.exceptions.UserAlreadyExistsError;
import xyz.kryom.wallets_backend.mapper.UserMapper;
import xyz.kryom.wallets_backend.model.User;
import xyz.kryom.wallets_backend.service.AppService;
import xyz.kryom.wallets_backend.web.dto.UserDto;
import xyz.kryom.wallets_backend.web.dto.UserSaveDto;

/**
 * @author Tomas Toth
 */

@RestController
@RequestMapping("/api")
public class UserResource {

  private final AppService appService;
  private final UserMapper userMapper;


  public UserResource(AppService appService, UserMapper userMapper) {
    this.appService = appService;
    this.userMapper = userMapper;
  }

  @GetMapping("/{user_id}")
  public ResponseEntity<UserDto> findUserById(@PathVariable long userId) {
    Optional<User> optionalUser = appService.findUserById(userId);
    if (optionalUser.isEmpty()) {
      throw new InvalidUserError();
    }
    UserDto userDto = userMapper.toDto(optionalUser.get());
    return new ResponseEntity<>(userDto, HttpStatus.OK);
  }

  @PostMapping("/users")
  public ResponseEntity<UserDto> saveUser(@RequestBody UserSaveDto userSaveDto){
    if(appService.findUserByUsername(userSaveDto.username()).isPresent()){
      throw new UserAlreadyExistsError();
    }
    User user = userMapper.fromUserSaveDto(userSaveDto);
    appService.saveUser(user);
    UserDto userDto = userMapper.toDto(user);
    return new ResponseEntity<>(userDto, HttpStatus.CREATED);
  }

}
