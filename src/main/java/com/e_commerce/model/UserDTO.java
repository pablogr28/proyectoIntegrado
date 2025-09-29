package com.e_commerce.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {

	 @NotBlank(message = "El correo es obligatorio")
	    @Email
	    private String email;

	    @NotBlank(message = "El nombre es obligatorio")
	    private String name;

	    @NotBlank(message = "El usuario es obligatorio")
	    private String username;

	    @NotBlank(message = "La contraseña es obligatoria")
	    private String password;

	    private String gender;
	    private String address;
	    
	    

	    public UserDTO(@NotBlank(message = "El correo es obligatorio") @Email String email,
				@NotBlank(message = "El nombre es obligatorio") String name,
				@NotBlank(message = "El usuario es obligatorio") String username,
				@NotBlank(message = "La contraseña es obligatoria") String password, String gender, String address) {
			super();
			this.email = email;
			this.name = name;
			this.username = username;
			this.password = password;
			this.gender = gender;
			this.address = address;
		}
	    
	    
		public UserDTO() {
			super();
		}


		// Getters y Setters
	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }

	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public String getUsername() { return username; }
	    public void setUsername(String username) { this.username = username; }

	    public String getPassword() { return password; }
	    public void setPassword(String password) { this.password = password; }

	    public String getGender() { return gender; }
	    public void setGender(String gender) { this.gender = gender; }

	    public String getAddress() { return address; }
	    public void setAddress(String address) { this.address = address; }
}
