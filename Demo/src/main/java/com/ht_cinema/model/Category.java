package com.ht_cinema.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NotBlank(message = "Tên danh mục không được để trống")
	@Size(max = 200, message = "Tên danh mục không vượt quá 200 ký tự")
	@Column(columnDefinition = "nvarchar(200)", unique = true)
	private String name;
	@OneToMany(mappedBy = "categories")
	private List<CategoriesFilms> CategoryFilm = new ArrayList<>();
}
