package com.ht_cinema.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "films")
public class Film {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(columnDefinition = "nvarchar(200)")
	@NotBlank(message = "Tên phim không được để trống")
	@Size(max = 200, message = "Tên phim không được vượt quá 200 ký tự")
	private String name;
	
	@NotNull(message = "Vui lòng chọn trạng thái")
	private Integer status;

	@Column(columnDefinition = "nvarchar(500)")
	@NotBlank(message = "Trailer không được để trống")
	@Size(max = 500, message = "Link trailer không được vượt quá 500 ký tự")
	private String trailer;
	
	@NotNull(message = "Vui lòng chọn loại phim")
	private Integer type;

	@Column(columnDefinition = "nvarchar(200)")
	private String avatar;

	@Column(columnDefinition = "varchar(200)")
	private String poster;
	@Min(value = 0, message = "Doanh thu không được âm")
	@Column(name = "doanh_thu")
	private Double doanhThu;
	@Valid
	@OneToOne(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	private DetailFilms detailFilm;

	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CategoriesFilms> CategoryFilm = new ArrayList<>();

	@Transient
	private List<Integer> categoryIds = new ArrayList<>();

	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SuatChieu> suatChieus = new ArrayList<>();

	public DetailFilms getDetailFilm() {
		return detailFilm;
	}

	public void setDetailFilm(DetailFilms detailFilm) {
		this.detailFilm = detailFilm;
		if (detailFilm != null) {
			detailFilm.setFilm(this);
		}
	}

}
