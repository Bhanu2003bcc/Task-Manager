package com.bsingh.doto.Task.Manager.Repository;

import com.bsingh.doto.Task.Manager.Entity.User;
import com.bsingh.doto.Task.Manager.Entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface VerificationTokenRepo extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserEmailAndTokenType(String email, VerificationToken.TokenType tokenType);

    @Query("{ 'user.$id': ?0, 'tokenType': ?1 }")
    void deleteByUserAndTokenType(User user, VerificationToken.TokenType tokenType);
}
