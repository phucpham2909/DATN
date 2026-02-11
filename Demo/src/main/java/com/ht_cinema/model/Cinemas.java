package com.ht_cinema.model;

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
@Table(name = "cinemas")
public class Cinemas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NotBlank(message = "Tên rạp không được để trống")
	@Size(max = 80, message = "Tên rạp tối đa 80 ký tự")
	@Column(columnDefinition = "nvarchar(200)")
	private String name;

	@NotBlank(message = "Địa chỉ không được để trống")
	@Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
	@Column(columnDefinition = "nvarchar(255)")
	private String dress;

	@OneToMany(mappedBy = "cinema")
	private List<Rooms> rooms;
}