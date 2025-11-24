package org.udesa.giftcards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.facade.GifCardFacade;
import org.udesa.giftcards.model.User;
import org.udesa.giftcards.repository.UserRepository;

@Service
public class UserService extends ModelService<User, UserRepository> {

    protected void updateData(User existingObject, User updatedObject) {
        existingObject.setUsername(updatedObject.getUsername());
        existingObject.setPassword(updatedObject.getPassword());
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(GifCardFacade.InvalidUser));
    }
}

