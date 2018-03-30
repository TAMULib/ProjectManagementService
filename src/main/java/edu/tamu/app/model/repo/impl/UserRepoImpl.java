package edu.tamu.app.model.repo.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.enums.Role;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.app.model.repo.custom.UserRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class UserRepoImpl extends AbstractWeaverRepoImpl<User, UserRepo> implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Override
    public synchronized User create(String uin, String email, String firstName, String lastName, Role role) {
        Optional<User> user = userRepo.findByUsername(uin);
        return user.isPresent() ? user.get() : userRepo.save(new User(uin, email, firstName, lastName, role));
    }

    @Override
    protected String getChannel() {
        return "/channel/users";
    }

}
