/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kryom.wallets_backend.model.User;
import xyz.kryom.wallets_backend.repository.UserRepository;
import xyz.kryom.wallets_backend.service.AppService;

/**
 * @author Tomas Toth
 */
@Service
public class AppServiceImpl implements AppService {

  private final UserRepository userRepository;

  public AppServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findUserById(long userId) {
    return userRepository.findById(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
