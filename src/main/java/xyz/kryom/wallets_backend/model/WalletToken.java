/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Tomas Toth
 */
@Entity
@Table(name="wallet_tokens")
@NoArgsConstructor
@Getter
public class WalletToken extends BaseEntity implements Serializable {
  @ManyToOne()
  @JoinColumn(name="wallet_id")
  private Wallet wallet;
  @ManyToOne
  @JoinColumn(name="price_token_id")
  private PriceToken priceToken;
  @NotNull
  @Column(name="balance",nullable = false)
  private BigDecimal amount;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    WalletToken that = (WalletToken) o;
    return Objects.equals(wallet, that.wallet) && Objects.equals(priceToken, that.priceToken) &&
        Objects.equals(amount, that.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), wallet, priceToken, amount);
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  public void setPriceToken(PriceToken priceToken) {
    this.priceToken = priceToken;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
