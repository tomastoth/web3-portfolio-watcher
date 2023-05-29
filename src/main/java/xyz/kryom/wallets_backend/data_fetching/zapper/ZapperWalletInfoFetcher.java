/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.data_fetching.zapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.crypto_common.price.PriceProvider;
import xyz.kryom.wallets_backend.data_fetching.DataError;
import xyz.kryom.wallets_backend.data_fetching.WalletInfoFetcher;
import xyz.kryom.wallets_backend.data_fetching.zapper.data.Token;
import xyz.kryom.wallets_backend.data_fetching.zapper.data.TokenUpdate;
import xyz.kryom.wallets_backend.web.dto.WalletDto;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;

/**
 * @author Tomas Toth
 */
@Service
@Log4j2
public class ZapperWalletInfoFetcher implements WalletInfoFetcher {

  static final Blockchain BLOCKCHAIN = Blockchain.ETHEREUM;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static final BlockchainType BLOCKCHAIN_TYPE = BlockchainType.EVM;

  private final PriceProvider priceProvider;
  private final WalletDataRequester walletDataRequester;

  public ZapperWalletInfoFetcher(PriceProvider priceProvider, WalletDataRequester walletDataRequester) {
    this.priceProvider = priceProvider;
    this.walletDataRequester = walletDataRequester;
  }

  /**
   * @param walletsDto wallets to run
   * @return fetched set of tokens per wallet
   * @throws DataError when we can't fetch process data, we throw DataError
   */
  @Override
  public Map<WalletDto, Collection<WalletTokenDto>> fetchWalletTokens(Collection<WalletDto> walletsDto) {
    return walletDataRequester.requestWalletsTokens(walletsDto).thenApply(this::parseZapperBalanceUpdate).join();
  }

  private Map<WalletDto, Collection<WalletTokenDto>> parseZapperBalanceUpdate(String update) {
    Map<WalletDto, Collection<WalletTokenDto>> allWalletsTokens = new HashMap<>();
    try {
      JsonNode jsonNode = OBJECT_MAPPER.readTree(update);
      Iterator<Entry<String, JsonNode>> singleWalletTokenUpdates = jsonNode.fields();
      while (singleWalletTokenUpdates.hasNext()) {
        Entry<String, JsonNode> singleWalletUpdate = singleWalletTokenUpdates.next();
        parseSingleWalletTokens(allWalletsTokens, singleWalletUpdate);
      }
    } catch (JsonProcessingException e) {
      throw new DataError(e.getLocalizedMessage());
    }
    return allWalletsTokens;
  }

  private void parseSingleWalletTokens(
      Map<WalletDto, Collection<WalletTokenDto>> allWalletsTokens,
      Entry<String, JsonNode> singleWalletUpdate) {
    try {
      WalletTokensWrapper walletTokensWrapper = extractSingleWalletTokens(singleWalletUpdate);
      allWalletsTokens.put(walletTokensWrapper.wallet, walletTokensWrapper.tokens);
    } catch (JsonProcessingException e) {
      log.error("", e);
      log.warn("Could not parse tokens for wallet: {}", singleWalletUpdate.getKey());
    }
  }

  private WalletTokensWrapper extractSingleWalletTokens(Entry<String, JsonNode> singleWalletUpdate)
      throws JsonProcessingException {
    List<WalletTokenDto> walletTokens = new ArrayList<>();
    JsonNode walletTokenBalances = singleWalletUpdate.getValue();
    WalletDto walletDto = new WalletDto(singleWalletUpdate.getKey(), BLOCKCHAIN_TYPE);
    for (JsonNode singleTokenBalance : walletTokenBalances) {
      TokenUpdate singleTokenUpdate = OBJECT_MAPPER.readValue(singleTokenBalance.toString(), TokenUpdate.class);
      walletTokens.add(convertTokenUpdateToWalletToken(singleTokenUpdate, walletDto));
    }
    return new WalletTokensWrapper(walletDto, walletTokens);
  }

  private WalletTokenDto convertTokenUpdateToWalletToken(TokenUpdate singleTokenUpdate, WalletDto wallet) {
    Token token = singleTokenUpdate.getToken();
    BigDecimal tokenPrice = token.getBalanceUsd()
        .divide(token.getBalance(), MathContext.DECIMAL128);
    BigDecimal priceEth = priceProvider.getPriceBySymbol("ETH");
    BigDecimal valueEth = priceEth.divide(token.getBalanceUsd(), MathContext.DECIMAL128);
    return new WalletTokenDto(wallet, token.getAddress(), token.getSymbol(), token.getName(), token.getBalance(),
        tokenPrice,
        token.getBalanceUsd(),
        valueEth);
  }

  private record WalletTokensWrapper(WalletDto wallet, List<WalletTokenDto> tokens) {
  }
}
