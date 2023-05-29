/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.service;

/**
 * @author Tomas Toth
 */

import java.util.List;
import java.util.Optional;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.wallets_backend.model.PriceToken;
import xyz.kryom.wallets_backend.model.Token;
import xyz.kryom.wallets_backend.model.User;
import xyz.kryom.wallets_backend.model.Wallet;
import xyz.kryom.wallets_backend.model.WalletToken;
import xyz.kryom.wallets_backend.web.dto.WalletDto;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;

/**
 * Facade to work with data from the application
 */
public interface AppService {
  User saveUser(User user);

  Optional<User> findUserById(long userId);

  Optional<User> findUserByUsername(String username);

  Wallet saveWallet(Wallet wallet);

  Optional<Wallet> findWalletByAddressAndBlockchainType(String address, BlockchainType blockchainType);
  List<Wallet> findAllWallets();
  WalletToken saveWalletToken(WalletToken walletToken);

  Optional<Token> findTokenByAddressAndBlockchain(String address, Blockchain blockchain);
  Token saveToken(Token token);

  Optional<xyz.kryom.wallets_backend.model.Blockchain> findBlockchainByName(String name);

  xyz.kryom.wallets_backend.model.Blockchain saveBlockchain(xyz.kryom.wallets_backend.model.Blockchain blockchain);
  PriceToken savePriceToken(PriceToken priceToken);
  xyz.kryom.wallets_backend.model.Blockchain fetchOrCreateBlockchain(
      Blockchain blockchainEnum,
      Optional<xyz.kryom.wallets_backend.model.Blockchain> blockchainOpt);
  Wallet fetchOrCreateWallet(WalletDto walletDto);
  Token fetchOrCreateToken(
      Blockchain blockchainEnum,
      WalletTokenDto walletTokenDto,
      xyz.kryom.wallets_backend.model.Blockchain blockchain);
}
