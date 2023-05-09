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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Tomas Toth
 */
@Entity
@Table(name="wallet_assets")
@NoArgsConstructor
@Getter
public class WalletAsset extends BaseEntity implements Serializable {
  @ManyToOne
  @JoinColumn(name="wallet_id")
  private Wallet wallet;
  @ManyToOne
  @JoinColumn(name="price_asset_id")
  private PriceAsset priceAsset;
  @NotNull
  @Column(name="value_eth", nullable = false)
  private BigDecimal valueEth;

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
    WalletAsset that = (WalletAsset) o;
    return Objects.equals(wallet, that.wallet) && Objects.equals(priceAsset, that.priceAsset) && Objects.equals(
        valueEth, that.valueEth);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), wallet, priceAsset, valueEth);
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  public void setPriceAsset(PriceAsset priceAsset) {
    this.priceAsset = priceAsset;
  }

  public void setValueEth(BigDecimal valueEth) {
    this.valueEth = valueEth;
  }
}
