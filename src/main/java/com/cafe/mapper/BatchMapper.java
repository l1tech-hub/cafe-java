package com.cafe.mapper;

import com.cafe.dto.BatchDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Product;

public class BatchMapper {

  private BatchMapper() {
  }


  public static BatchDto toDto(Batch batch) {
    return new BatchDto(
        batch.getId(),
        batch.getProduct().getId(),
        batch.getPrice(),
        batch.getQuantity(),
        batch.getManufactureDate(),
        batch.getExpiryDate()
    );
  }

  public static Batch toEntity(BatchDto dto, Product product) {

    Batch batch = new Batch();

    batch.setProduct(product);
    batch.setPrice(dto.getPrice());
    batch.setQuantity(dto.getQuantity());
    batch.setManufactureDate(dto.getManufactureDate());
    batch.setExpiryDate(dto.getExpiryDate());

    return batch;
  }
}
