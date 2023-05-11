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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.kryom.crypto_common.BlockchainType;
import xyz.kryom.wallets_backend.TestUtils;
import xyz.kryom.wallets_backend.data_fetching.zapper.WalletDataRequester;
import xyz.kryom.wallets_backend.data_fetching.zapper.ZapperWalletInfoFetcher;
import xyz.kryom.wallets_backend.web.dto.WalletDto;
import xyz.kryom.wallets_backend.web.dto.WalletTokenDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Tomas Toth
 */
@ExtendWith(MockitoExtension.class)
class ZapperWalletInfoFetcherTest {

  private ZapperWalletInfoFetcher zapperWalletInfoFetcher;
  private NaivePriceProvider naivePriceProvider;
  @Mock
  private WalletDataRequester walletDataRequesterMock;

  @BeforeEach
  void setUp() {
    naivePriceProvider = new NaivePriceProvider();
    zapperWalletInfoFetcher = new ZapperWalletInfoFetcher(naivePriceProvider, walletDataRequesterMock);
  }

  @Test
  void whenGivenDataFromZapper_ThenWeShouldParseItCorrectly() throws URISyntaxException, IOException {
    String loadedData = TestUtils.loadTestData("ZapperWalletData");
    WalletDto wallet = new WalletDto("0x5c9e30def85334e587cf36eb07bdd6a72bf1452d", BlockchainType.EVM);
    when(walletDataRequesterMock.requestWalletsTokens(Set.of(wallet))).thenReturn(CompletableFuture.supplyAsync(() -> loadedData));
    Map<WalletDto, Collection<WalletTokenDto>> walletDtoSetMap =
        zapperWalletInfoFetcher.fetchWalletTokens(Set.of(wallet));
    Collection<WalletTokenDto> walletTokens = walletDtoSetMap.get(wallet);
    ArrayList<WalletTokenDto> walletTokensList = new ArrayList<>(walletTokens);
    WalletTokenDto firstToken = walletTokensList.get(0);
    assertThat(new BigDecimal("60.990"), comparesEqualTo(firstToken.tokenAmount().setScale(2, RoundingMode.DOWN)));
    assertThat(new BigDecimal("111126.57"), comparesEqualTo(firstToken.tokenValueUsd().setScale(2, RoundingMode.DOWN)));
    assertThat(new BigDecimal("1822.04"), comparesEqualTo(firstToken.tokenPriceUsd().setScale(2, RoundingMode.DOWN)));
    assertEquals("0x0000000000000000000000000000000000000000", firstToken.tokenAddress());
    assertEquals("ETH", firstToken.tokenSymbol());
  }
}