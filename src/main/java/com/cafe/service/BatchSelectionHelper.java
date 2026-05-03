package com.cafe.service;

import com.cafe.dto.BatchOrder;
import com.cafe.entity.Batch;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BatchSelectionHelper {

  private BatchSelectionHelper() {
  }

  public static void sortByOrder(List<Batch> batches, BatchOrder order) {
    Comparator<Batch> cmp = switch (order) {
      case PRICE_ASC -> Comparator.comparing(Batch::getPrice, Comparator.nullsLast(Double::compareTo));
      case PRICE_DESC -> Comparator.comparing(
          Batch::getPrice, Comparator.nullsLast(Double::compareTo)).reversed();
      case EXPIRY_ASC -> Comparator.comparing(
          Batch::getExpiryDate, Comparator.nullsLast(LocalDate::compareTo));
      case EXPIRY_DESC -> Comparator.comparing(
          Batch::getExpiryDate, Comparator.nullsLast(LocalDate::compareTo)).reversed();
    };
    batches.sort(cmp);
  }


  public static List<Batch> orderForCooking(
      List<Batch> positiveQuantityBatches,
      LocalDate today,
      boolean allowExpiredProducts,
      BatchOrder order) {

    List<Batch> expired = new ArrayList<>();
    List<Batch> valid = new ArrayList<>();
    for (Batch b : positiveQuantityBatches) {
      if (b.getQuantity() == null || b.getQuantity() <= 0) {
        continue;
      }
      if (b.getExpiryDate().isBefore(today)) {
        expired.add(b);
      } else {
        valid.add(b);
      }
    }
    sortByOrder(valid, order);
    sortByOrder(expired, order);

    List<Batch> result = new ArrayList<>();
    if (allowExpiredProducts) {
      result.addAll(expired);
      result.addAll(valid);
    } else {
      result.addAll(valid);
      result.addAll(expired);
    }
    return result;
  }
}
