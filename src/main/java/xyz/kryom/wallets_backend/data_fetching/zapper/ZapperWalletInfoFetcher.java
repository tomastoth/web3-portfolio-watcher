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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.crypto_common.price.PriceProvider;
import xyz.kryom.crypto_common.price.SymbolToken;
import xyz.kryom.wallets_backend.data_fetching.DataError;
import xyz.kryom.wallets_backend.data_fetching.HttpUtils;
import xyz.kryom.wallets_backend.data_fetching.WalletInfoFetcher;
import xyz.kryom.wallets_backend.data_fetching.zapper.data.Token;
import xyz.kryom.wallets_backend.data_fetching.zapper.data.TokenUpdate;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;
import xyz.kryom.wallets_backend.web.dto.WalletDto;

/**
 * @author Tomas Toth
 */
@Service
@Log4j2
public class ZapperWalletInfoFetcher implements WalletInfoFetcher {

  public static final String TOKENS_API = "https://api.zapper.xyz/v2/balances/tokens";
  private static final Blockchain BLOCKCHAIN = Blockchain.ETHEREUM;
  private static final String AUTH_HEADER_NAME = "Authorization";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static final BlockchainType BLOCKCHAIN_TYPE = BlockchainType.EVM;

  @Value("${zapper_api_key}")
  private String zapperApiKey;
  private final PriceProvider priceProvider;

  public ZapperWalletInfoFetcher(PriceProvider priceProvider) {
    this.priceProvider = priceProvider;
  }

  private static String buildUrl(String urlStart, Map<String, Collection<String>> queryParams) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(String.format("%s?", urlStart));
    queryParams.forEach((paramName, paramValues) -> paramValues.forEach(
        paramValue -> urlBuilder.append(String.format("%s%%5B%%5D=%s&", paramName, paramValue))));
    return urlBuilder.toString();
  }

  /**
   * @param walletsDto wallets to run
   * @return fetched set of tokens per wallet
   * @throws DataError when we can't fetch process data, we throw DataError
   */
  @Override
  public Map<WalletDto, Collection<WalletTokenDto>> fetchWalletTokens(Collection<WalletDto> walletsDto) {
    HashMap<String, Collection<String>> queryParams = new HashMap<>();
    queryParams.put("addresses", walletsDto.stream()
        .map(WalletDto::walletAddress)
        .toList());
    queryParams.put("networks", Set.of(mapBlockchainToZapperBlockchain(BLOCKCHAIN)));
    String url = buildUrl(TOKENS_API, queryParams);
    return HttpUtils.fetchUrl(url, Map.of(AUTH_HEADER_NAME, createAuthHeader()))
        .thenApply(this::parseZapperBalanceUpdate)
        .join();
  }

  private String mapBlockchainToZapperBlockchain(Blockchain blockchain) {
    return switch (blockchain) {
      case ETHEREUM -> "ethereum";
    };
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

    Set<WalletTokenDto> walletTokens = new HashSet<>();
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
    BigDecimal valueEth = priceProvider.getPriceBySymbol(new SymbolToken("WETH", BLOCKCHAIN));
    return new WalletTokenDto(wallet, token.getAddress(), token.getBalance(), tokenPrice, token.getBalanceUsd(),
        valueEth);
  }

  private String createAuthHeader() {
    return String.format("Basic %s", zapperApiKey);
  }

  public void setZapperApiKey(String zapperApiKey) {
    this.zapperApiKey = zapperApiKey;
  }

  private record WalletTokensWrapper(WalletDto wallet, Set<WalletTokenDto> tokens) {

  }
}
