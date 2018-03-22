package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.custom.UserRepoCustom;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;

/**
 * Application User repository.
 * 
 */
@Repository
public interface UserRepo extends AbstractWeaverUserRepo<User>, UserRepoCustom {

}
