package com.kraken.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.kraken.models.Order;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OrderBook
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-04-22T15:56:17.982854+03:00[Europe/Sofia]", comments = "Generator version: 7.5.0")
public class OrderBook {

  @Valid
  private List<@Valid Order> bids = new ArrayList<>();

  @Valid
  private List<@Valid Order> asks = new ArrayList<>();

  public OrderBook bids(List<@Valid Order> bids) {
    this.bids = bids;
    return this;
  }

  public OrderBook addBidsItem(Order bidsItem) {
    if (this.bids == null) {
      this.bids = new ArrayList<>();
    }
    this.bids.add(bidsItem);
    return this;
  }

  /**
   * Get bids
   * @return bids
  */
  @Valid 
  @Schema(name = "bids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bids")
  public List<@Valid Order> getBids() {
    return bids;
  }

  public void setBids(List<@Valid Order> bids) {
    this.bids = bids;
  }

  public OrderBook asks(List<@Valid Order> asks) {
    this.asks = asks;
    return this;
  }

  public OrderBook addAsksItem(Order asksItem) {
    if (this.asks == null) {
      this.asks = new ArrayList<>();
    }
    this.asks.add(asksItem);
    return this;
  }

  /**
   * Get asks
   * @return asks
  */
  @Valid 
  @Schema(name = "asks", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("asks")
  public List<@Valid Order> getAsks() {
    return asks;
  }

  public void setAsks(List<@Valid Order> asks) {
    this.asks = asks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderBook orderBook = (OrderBook) o;
    return Objects.equals(this.bids, orderBook.bids) &&
        Objects.equals(this.asks, orderBook.asks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bids, asks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderBook {\n");
    sb.append("    bids: ").append(toIndentedString(bids)).append("\n");
    sb.append("    asks: ").append(toIndentedString(asks)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

