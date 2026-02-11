package com.ht_cinema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "detail_films")
public class DetailFilms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Đạo diễn không được để trống")
	@Size(max = 200, message = "Đạo diễn không được quá 200 ký tự")
	@Column(columnDefinition = "nvarchar(200)")
	private String daoDien;

	@NotBlank(message = "Diễn viên không được để trống")
	@Size(max = 200, message = "Diễn viên không được quá 200 ký tự")
	@Column(columnDefinition = "nvarchar(200)")
	private String dienVien;

	@Column(columnDefinition = "nvarchar(200)")
	private String theLoai;

	@NotNull(message = "Ngày chiếu không được để trống")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Column(columnDefinition = "nvarchar(100)")
	private String rate;

	@Column(columnDefinition = "nvarchar(50)")
	private String ngonNgu;

	@NotNull(message = "Thời lượng không được để trống")
	@Min(value = 1, message = "Thời lượng phải lớn hơn 0")
	@Max(value = 500, message = "Thời lượng không hợp lệ")
	private Integer thoiLuong;

	@OneToOne
	@JoinColumn(name = "film_id", referencedColumnName = "id", unique = true)
	private Film film;

}
