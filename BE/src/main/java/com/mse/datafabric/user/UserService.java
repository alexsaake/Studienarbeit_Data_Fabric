package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.auth.repositories.UserRepository;
import com.mse.datafabric.dataProducts.payload.response.RatingReponse;
import com.mse.datafabric.user.payload.request.UserRequest;
import com.mse.datafabric.auth.models.User;
import com.mse.datafabric.user.payload.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final AuthenticationService myAuthenticationService;
    private final UserRepository myUserRepository;
    JdbcTemplate myJdbcTemplate;

    @Autowired
    public UserService(AuthenticationService authenticationService, UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        myAuthenticationService = authenticationService;
        myUserRepository = userRepository;
        myJdbcTemplate = jdbcTemplate;
    }

    public UserResponse getCurrentUser() {
        User currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        return new UserResponse(currentUserEntity.getFirstname(), currentUserEntity.getLastname(), currentUserEntity.getUsername(), currentUserEntity.getEmail());
    }

    public void updateUser(UserRequest user) {
        User currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        if(!currentUserEntity.getFirstname().equals(user.getFirstName())) {
            currentUserEntity.setFirstname(user.getFirstName());
        }
        if(!currentUserEntity.getLastname().equals(user.getLastName())) {
            currentUserEntity.setLastname(user.getLastName());
        }
        if(!currentUserEntity.getEmail().equals(user.getEmail())) {
            currentUserEntity.setEmail(user.getEmail());
        }

        myUserRepository.save(currentUserEntity);
    }

    public List<RatingReponse> getUserRatings()
    {
        User currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        String dataProductsSql = "SELECT rate.id, rate.id_dataProducts FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE usr.userName = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(currentUserEntity.getUsername());
        List<Map<String, Object>> databaseUserRatings = myJdbcTemplate.queryForList(dataProductsSql);

        List<RatingReponse> userRatings = new ArrayList<>();

        for (Map databaseUserRating : databaseUserRatings)
        {
            RatingReponse dataProductRating = new RatingReponse(
                    (Long)databaseUserRating.get("id"),
                    (Long)databaseUserRating.get("id_dataProducts")
            );
            userRatings.add(dataProductRating);
        }

        return userRatings;
    }
}
