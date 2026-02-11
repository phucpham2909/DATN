package com.ht_cinema.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "products", schema = "dbo")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 200, message = "Đường dẫn ảnh không được vượt quá 200 ký tự")
    private String images;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(columnDefinition = "nvarchar(200)")
    @Size(max = 200, message = "Tên sản phẩm không được vượt quá 200 ký tự")
    private String name;

    private boolean active;
    @Column(columnDefinition = "nvarchar(200)")
    @Size(max = 200, message = "Mô tả không được vượt quá 200 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 1, message = "Giá phải lớn hơn 0")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "category_product_id", nullable = false)
    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private CategoryProduct categoryProduct;

    @OneToMany(mappedBy = "product")
    private List<BookingProduct> bookingProducts = new ArrayList<>();

	
}
