/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.data_fetching;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.wallets_backend.PropertiesReader;
import xyz.kryom.wallets_backend.data_fetching.zapper.ZapperWalletInfoFetcher;
import xyz.kryom.wallets_backend.web.dto.WalletDto;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Tomas Toth
 */
class ZapperWalletInfoFetcherTest {

  private ZapperWalletInfoFetcher zapperWalletInfoFetcher;
  private NaivePriceProvider naivePriceProvider;

  @BeforeEach
  void setUp() {
    naivePriceProvider = new NaivePriceProvider();
    zapperWalletInfoFetcher = new ZapperWalletInfoFetcher(naivePriceProvider);
    zapperWalletInfoFetcher.setZapperApiKey(PropertiesReader.getProperty("zapper_api_key"));
  }

  @Test
  void testFetchingZapperTokens() {
    WalletDto wallet = new WalletDto("0x5c9e30def85334e587cf36eb07bdd6a72bf1452d", BlockchainType.EVM);
    Map<WalletDto, Collection<WalletTokenDto>> walletDtoSetMap =
        zapperWalletInfoFetcher.fetchWalletTokens(Set.of(wallet));
    assertFalse(walletDtoSetMap.isEmpty());
  }
}