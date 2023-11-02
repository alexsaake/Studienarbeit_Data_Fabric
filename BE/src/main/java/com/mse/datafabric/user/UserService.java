package com.mse.datafabric.user;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.models.RatingDto;
import com.mse.datafabric.user.dto.UserDto;
import com.mse.datafabric.user.entities.UserEntity;
import com.mse.datafabric.user.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

    public UserResponseDto getCurrentUser() {
        UserEntity currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        return new UserResponseDto(currentUserEntity.getFirstname(), currentUserEntity.getLastname(), currentUserEntity.getUsername(), currentUserEntity.getEmail());
    }

    public void updateUser(UserDto user) {
        UserEntity currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

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

    public List<RatingDto> getUserRatings()
    {
        UserEntity currentUserEntity = myUserRepository.findByUsername(myAuthenticationService.getCurrentUserName()).get();

        String dataProductsSql = "SELECT dp.id, usr.userName, rate.title, rate.comment, rate.rating, rate.submitted, rate.isEdited FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE usr.userName = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(currentUserEntity.getUsername());
        List<Map<String, Object>> databaseUserRatings = myJdbcTemplate.queryForList(dataProductsSql);

        List<RatingDto> userRatings = new ArrayList<>();

        for (Map databaseUserRating : databaseUserRatings)
        {
            RatingDto dataProductRating = new RatingDto(
                    (Integer)databaseUserRating.get("id"),
                    (String)databaseUserRating.get("userName"),
                    (String)databaseUserRating.get("title"),
                    (String)databaseUserRating.get("comment"),
                    ((BigDecimal)databaseUserRating.get("rating")).intValue(),
                    new Date(((Timestamp) databaseUserRating.get("submitted")).getTime()),
                    (Boolean)databaseUserRating.get("isEdited")
            );
            userRatings.add(dataProductRating);
        }

        return userRatings;
    }
}
