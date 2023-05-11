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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.wallets_backend.data_fetching.HttpUtils;
import xyz.kryom.wallets_backend.web.dto.WalletDto;

import static xyz.kryom.wallets_backend.data_fetching.zapper.ZapperWalletInfoFetcher.BLOCKCHAIN;

/**
 * @author Tomas Toth
 */
@Component
public class ZapperHttpDataRequester implements WalletDataRequester{
  private static final String AUTH_HEADER_NAME = "Authorization";
  public static final String TOKENS_API = "https://api.zapper.xyz/v2/balances/tokens";
  @Value("${zapper_api_key}")
  private String zapperApiKey;
  @Override
  public CompletableFuture<String> requestWalletsTokens(Collection<WalletDto> wallets) {
    HashMap<String, Collection<String>> queryParams = new HashMap<>();
    queryParams.put("addresses", wallets.stream()
        .map(WalletDto::walletAddress)
        .toList());
    queryParams.put("networks", Set.of(mapBlockchainToZapperBlockchain(BLOCKCHAIN)));
    String url = buildUrl(TOKENS_API, queryParams);
    return HttpUtils.fetchUrl(url, Map.of(AUTH_HEADER_NAME, createAuthHeader()));
  }

  private String mapBlockchainToZapperBlockchain(Blockchain blockchain) {
    return switch (blockchain) {
      case ETHEREUM -> "ethereum";
    };
  }

  private String createAuthHeader() {
    return String.format("Basic %s", zapperApiKey);
  }

  private static String buildUrl(String urlStart, Map<String, Collection<String>> queryParams) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(String.format("%s?", urlStart));
    queryParams.forEach((paramName, paramValues) -> paramValues.forEach(
        paramValue -> urlBuilder.append(String.format("%s%%5B%%5D=%s&", paramName, paramValue))));
    return urlBuilder.toString();
  }

  public void setZapperApiKey(String zapperApiKey) {
    this.zapperApiKey = zapperApiKey;
  }
}
