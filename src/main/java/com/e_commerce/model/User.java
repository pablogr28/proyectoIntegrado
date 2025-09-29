package com.e_commerce.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="User")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank(message = "El correo el obligatorio")
    @Column(name = "Email")
    private String email;
	
	@NotBlank(message = "El nombre el obligatorio")
    @Column(name = "Name")
    private String name;
	
	@NotBlank(message = "El usuario el obligatorio")
    @Column(name = "Username")
    private String username;
	
	@NotBlank(message = "La contraseña el obligatorio")
    @Column(name = "Password")
    private String password;
	
	@NotBlank(message = "El género el obligatorio")
    @Column(name = "Gender")
    private String gender;
	
	@NotBlank(message = "La dirección el obligatorio")
    @Column(name = "Address")
    private String address;
	
    @Column(name = "Role")
    private String role;
    
    @Column(name = "Status")
    private String status;
    
    @Column(name = "RegistrationDate")
    private LocalDate registrationDate;
    
    @OneToMany(mappedBy="user")
    @JsonManagedReference
    public List<Review> myReviews;
    
    @OneToMany(mappedBy="user")
    @JsonManagedReference
    public List<Notification> notifications;
    
    @OneToMany(mappedBy="user")
    @JsonManagedReference
    public List<Order> orders;
    
    @OneToMany(mappedBy="user")
    @JsonManagedReference
    public List<CouponUser> couponsUsed;

	public User() {
		super();
	}

	public User(Long id, @NotBlank(message = "El correo el obligatorio") String email,
			@NotBlank(message = "El nombre el obligatorio") String name,
			@NotBlank(message = "El usuario el obligatorio") String username,
			@NotBlank(message = "La contraseña el obligatorio") String password,
			@NotBlank(message = "El género el obligatorio") String gender,
			@NotBlank(message = "La dirección el obligatorio") String address, String role, String status) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.username = username;
		this.password = password;
		this.gender = gender;
		this.address = address;
		this.role = role;
		this.status = status;
		this.registrationDate= LocalDate.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
