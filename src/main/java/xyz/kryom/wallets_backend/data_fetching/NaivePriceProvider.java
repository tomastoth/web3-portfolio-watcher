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

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import xyz.kryom.crypto_common.Blockchain;
import xyz.kryom.crypto_common.price.PriceProvider;
import xyz.kryom.crypto_common.price.SymbolToken;
import xyz.kryom.crypto_common.price.TokenNotFoundError;

/**
 * @author Tomas Toth
 */
@Component
public class NaivePriceProvider implements PriceProvider {

  private static final SymbolToken ETH = new SymbolToken("WETH", Blockchain.ETHEREUM);
  private static final BigDecimal DEFAULT_ETH_PRICE = BigDecimal.valueOf(2000.0);

  /**
   * Fetches price of symbol token
   *
   * @param symbolToken contains symbol and blockchain to fetch
   * @return price
   */
  @Override
  public BigDecimal getPriceBySymbol(SymbolToken symbolToken) {
    if (symbolToken.equals(ETH)) {
      return DEFAULT_ETH_PRICE;
    }
    throw new TokenNotFoundError();
  }
}