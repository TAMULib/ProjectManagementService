package edu.tamu.app.model.repo.custom;

import edu.tamu.app.enums.Role;
import edu.tamu.app.model.User;

public interface UserRepoCustom {

    public User create(String uin, String email, String firstName, String lastName, Role role);

}
