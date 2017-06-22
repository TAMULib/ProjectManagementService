/* 
 * AppUser.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.tamu.app.enums.AppRole;
import edu.tamu.framework.model.AbstractCoreUser;
import edu.tamu.framework.model.IRole;

/**
 * Application User entity.
 * 
 */
@Entity
public class AppUser extends AbstractCoreUser {
    
    private static final long serialVersionUID = -4974106399870286015L;
    
    @Column(name = "role")
    private AppRole role;

    @Column(nullable = true)
    private String netid;

    @Column(nullable = true, unique = true)
    private String email;

    // encoded password
    @JsonIgnore
    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    /**
     * Constructor for the application user
     * 
     */
    public AppUser() {
        super();
    }

    /**
     * Constructor for application user with uin passed.
     * 
     * @param uin
     *            String
     * 
     */
    public AppUser(String uin) {
        super(uin);
    }

    /**
     * Constructor for application user with uin passed.
     * 
     * @param uin
     *            Long
     * 
     */
    public AppUser(String email, String firstName, String lastName, String role) {
        super();
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(AppRole.valueOf(role));
    }
    
    /**
     * @return the role
     */
    @JsonDeserialize(as = AppRole.class)
    public IRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    @JsonSerialize(as = AppRole.class)
    public void setRole(IRole role) {
        this.role = (AppRole) role;
    }

    /**
     * @return the netid
     */
    public String getNetid() {
        return netid;
    }

    /**
     * @param netid
     *            the netid to set
     */
    public void setNetid(String netid) {
        this.netid = netid;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * @return firstName
     * 
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            String
     * 
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return lastName
     * 
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            String
     * 
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return false;
    }
    
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return false;
    }
    
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return false;
    }
    
    @Override
    @JsonIgnore
    public String getUsername() {
        return getUin();
    }
    
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.getRole().toString());
        authorities.add(authority);
        return authorities;
    }

}
