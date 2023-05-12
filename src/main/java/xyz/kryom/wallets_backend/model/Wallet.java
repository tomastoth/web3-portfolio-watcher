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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.kryom.crypto_common.BlockchainType;

/**
 * @author Tomas Toth
 */

@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "wallets")
public class Wallet extends BaseEntity implements Serializable {

  @NotEmpty
  @Column(name = "address", nullable = false)
  private String address;
  @NotNull
  @Column(name = "blockchain_type", nullable = false)
  private BlockchainType blockchainType;
  @OneToMany(mappedBy = "wallet")
  private Set<WalletToken> walletTokens = new HashSet<>();

  public void addWalletToken(WalletToken walletToken) {
    walletTokens.add(walletToken);
    walletToken.setWallet(this);

  }

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
    Wallet wallet = (Wallet) o;
    return Objects.equals(address, wallet.address) && blockchainType == wallet.blockchainType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), address, blockchainType);
  }
}
