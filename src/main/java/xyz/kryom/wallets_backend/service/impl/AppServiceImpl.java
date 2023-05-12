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

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.wallets_backend.model.PriceToken;
import xyz.kryom.wallets_backend.model.Token;
import xyz.kryom.wallets_backend.model.User;
import xyz.kryom.wallets_backend.model.Wallet;
import xyz.kryom.wallets_backend.model.WalletToken;
import xyz.kryom.wallets_backend.repository.BlockchainRepository;
import xyz.kryom.wallets_backend.repository.PriceTokenRepository;
import xyz.kryom.wallets_backend.repository.TokenRepository;
import xyz.kryom.wallets_backend.repository.UserRepository;
import xyz.kryom.wallets_backend.repository.WalletRepository;
import xyz.kryom.wallets_backend.repository.WalletTokenRepository;
import xyz.kryom.wallets_backend.service.AppService;

/**
 * @author Tomas Toth
 */
@Service
public class AppServiceImpl implements AppService {

  private final UserRepository userRepository;
  private final WalletTokenRepository walletTokenRepository;
  private final WalletRepository walletRepository;
  private final TokenRepository tokenRepository;
  private final BlockchainRepository blockchainRepository;
  private final PriceTokenRepository priceTokenRepository;

  public AppServiceImpl(
      UserRepository userRepository, WalletTokenRepository walletTokenRepository,
      WalletRepository walletRepository, TokenRepository tokenRepository, BlockchainRepository blockchainRepository,
      PriceTokenRepository priceTokenRepository) {
    this.userRepository = userRepository;
    this.walletTokenRepository = walletTokenRepository;
    this.walletRepository = walletRepository;
    this.tokenRepository = tokenRepository;
    this.blockchainRepository = blockchainRepository;
    this.priceTokenRepository = priceTokenRepository;
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

  @Override
  @Transactional
  public Wallet saveWallet(Wallet wallet) {
    walletRepository.save(wallet);
    return wallet;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Wallet> findWalletByAddressAndBlockchainType(String address, BlockchainType blockchainType) {
    return walletRepository.findByAddressAndBlockchainType(address, blockchainType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Wallet> findAllWallets() {
    return walletRepository.findAll();
  }

  @Override
  @Transactional
  public WalletToken saveWalletToken(WalletToken walletToken) {
    return walletTokenRepository.save(walletToken);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Token> findTokenByAddressAndBlockchain(String address, Blockchain blockchain) {
    return tokenRepository.findTokenByAddressAndBlockchain(address, blockchain.name());
  }

  @Override
  @Transactional
  public Token saveToken(Token token) {
    return tokenRepository.save(token);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<xyz.kryom.wallets_backend.model.Blockchain> findBlockchainByName(String name) {
    return blockchainRepository.findByName(name);
  }

  @Override
  @Transactional
  public xyz.kryom.wallets_backend.model.Blockchain saveBlockchain(xyz.kryom.wallets_backend.model.Blockchain blockchain) {
    blockchainRepository.save(blockchain);
    return blockchain;
  }

  @Override
  @Transactional
  public PriceToken savePriceToken(PriceToken priceToken) {
    priceTokenRepository.save(priceToken);
    return priceToken;
  }
}
