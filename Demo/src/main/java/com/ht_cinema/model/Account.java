package com.ht_cinema.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Email không được để trống")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email không hợp lệ")
	@Column(length = 50, unique = true)
	private String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
	@Column(length = 255)
	private String password;

	@NotBlank(message = "Họ và tên không được để trống")
	@Column(length = 100,columnDefinition = "nvarchar(200)")
	private String fullname;

	@NotBlank(message = "Số điện thoại không được để trống")
	@Pattern(regexp = "0\\d{9,10}", message = "Số điện thoại không hợp lệ")
	@Column(length = 12)
	private String phone;

	@Column(length = 255)
	private String avatar; 
	private boolean gender;

	private boolean role = false;
	private boolean status = true;

	@OneToMany(mappedBy = "account")
	private List<Booking> bookings;
}
