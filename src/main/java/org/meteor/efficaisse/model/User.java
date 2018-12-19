package org.meteor.efficaisse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User implements UserDetails,Serializable{

	private static final long serialVersionUID = 1L;
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotEmpty
	private String name;

	@NotEmpty
	@Column(unique = true, nullable = false)
	private String username;

	@Column
	private String email;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotEmpty
	private String password;


	private boolean enabled;


	@JsonIgnore
	@NotEmpty
	private String activationCode;




	@OneToOne(mappedBy = "manager",fetch = FetchType.EAGER)
	private Store store;


	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<Role>();





    public User() {
		super();
		enabled = false;
	}





	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}


	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles();
	}

	@Override
	public String getUsername() {
		return username;
	}


	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() { return getRoles().contains(new Role("ADMIN")) || DateUtils.addMonths(getStore().getPayDate(), getStore().getLicence().getLength()).after(new Date()); }

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}


	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return enabled;
	}


}
