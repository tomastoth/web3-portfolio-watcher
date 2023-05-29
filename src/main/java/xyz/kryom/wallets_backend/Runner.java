/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.crypto_common.price.PriceProvider;
import xyz.kryom.wallets_backend.data_fetching.WalletInfoFetcher;
import xyz.kryom.wallets_backend.mapper.WalletMapper;
import xyz.kryom.wallets_backend.model.PriceToken;
import xyz.kryom.wallets_backend.model.Token;
import xyz.kryom.wallets_backend.model.Wallet;
import xyz.kryom.wallets_backend.model.WalletToken;
import xyz.kryom.wallets_backend.service.AppService;
import xyz.kryom.wallets_backend.web.dto.WalletDto;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;

/**
 * @author Tomas Toth
 */
@Component
public class Runner implements CommandLineRunner {
  private final WalletInfoFetcher walletInfoFetcher;
  private final AppService appService;

  private final PriceProvider priceProvider;
  private final WalletMapper walletMapper;

  public Runner(
      WalletInfoFetcher walletInfoFetcher, AppService appService,
      PriceProvider priceProvider, WalletMapper walletMapper) {
    this.walletInfoFetcher = walletInfoFetcher;
    this.appService = appService;
    this.priceProvider = priceProvider;
    this.walletMapper = walletMapper;
  }

  @Override
  public void run(String... args) {
    fetchTokenUpdatesForAllWallets();
  }

  private void fetchTokenUpdatesForAllWallets() {
    List<Wallet> wallets = appService.findAllWallets();
    List<WalletDto> walletDtos = walletMapper.toDtos(wallets);
    Map<Token, PriceToken> currentPriceTokens = new HashMap<>();
    Map<WalletDto, Collection<WalletTokenDto>> walletUpdate =
        walletInfoFetcher.fetchWalletTokens(walletDtos);
    walletUpdate.forEach((walletDto, walletTokens) -> saveTokensForSingleWallet(
        currentPriceTokens,
        walletDto,
        walletTokens));
  }

  private void saveTokensForSingleWallet(
      Map<Token, PriceToken> currentPriceTokens,
      WalletDto walletDto,
      Collection<WalletTokenDto> walletTokens) {
    Blockchain blockchainEnum = Blockchain.ETHEREUM;
    Wallet wallet = appService.fetchOrCreateWallet(walletDto);
    Optional<xyz.kryom.wallets_backend.model.Blockchain> blockchainOpt =
        appService.findBlockchainByName(Blockchain.ETHEREUM.name());
    xyz.kryom.wallets_backend.model.Blockchain blockchain = appService.fetchOrCreateBlockchain(blockchainEnum,
        blockchainOpt);
    for (WalletTokenDto walletTokenDto : walletTokens) {
      WalletToken walletToken = new WalletToken();
      Token token = appService.fetchOrCreateToken(blockchainEnum, walletTokenDto, blockchain);
      PriceToken priceToken;
      BigDecimal ethPrice = priceProvider.getPriceBySymbol("ETH");
      if (currentPriceTokens.containsKey(token)) {
        priceToken = currentPriceTokens.get(token);
      } else {
        priceToken = createNewPriceToken(walletTokenDto, token, ethPrice);
        currentPriceTokens.put(token, priceToken);
      }
      walletToken.setWallet(wallet);
      walletToken.setPriceToken(priceToken);
      walletToken.setAmount(walletTokenDto.tokenAmount());
      walletToken.setWallet(wallet);
      appService.saveWalletToken(walletToken);
    }
    appService.saveWallet(wallet);
  }

  private PriceToken createNewPriceToken(WalletTokenDto walletTokenDto, Token token, BigDecimal ethPrice) {
    PriceToken priceToken;
    priceToken = new PriceToken();
    priceToken.setPriceUsd(walletTokenDto.tokenPriceUsd());
    priceToken.setToken(token);
    BigDecimal priceInEth = walletTokenDto.tokenPriceUsd().divide(ethPrice, MathContext.DECIMAL128);
    priceToken.setPriceEth(priceInEth);
    appService.savePriceToken(priceToken);
    return priceToken;
  }


}
