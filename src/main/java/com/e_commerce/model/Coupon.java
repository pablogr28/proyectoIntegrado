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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "Coupon")
public class Coupon {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank(message = "El código del cupón es obligatoria")
    @Column(name = "Code")
    private String code;
	
	@NotBlank(message = "La descripción del cupón es obligatoria")
    @Column(name = "Description")
    private String description;
	
	@Positive(message = "El descuento debe ser positivo")
    @NotNull(message = "El descuento es obligatorio")
    @Column(name = "Discount")
    private Integer discount;
	
	@Column(name = "StartDate")
    private LocalDate startDate;
	
	@Column(name = "EndDate")
    private LocalDate endDate;
	
    @Column(name = "Active")
    private Boolean active;
    
    @OneToMany(mappedBy="coupon")
    @JsonManagedReference
    public List<CouponUser> coupons;


	public Coupon(Long id, @NotBlank(message = "El código del cupón es obligatoria") String code,
			@NotBlank(message = "La descripción del cupón es obligatoria") String description,
			@Positive(message = "El descuento debe ser positivo") @NotNull(message = "El descuento es obligatorio") Integer discount,
			LocalDate startDate, LocalDate endDate, Boolean active) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.discount = discount;
		this.startDate = LocalDate.now();
		this.endDate = endDate;
		this.active = active;
	}

	public Coupon() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
		Coupon other = (Coupon) obj;
		return Objects.equals(id, other.id);
	}
	
	
    
    

}
