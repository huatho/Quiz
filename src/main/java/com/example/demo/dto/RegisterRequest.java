package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
	

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
