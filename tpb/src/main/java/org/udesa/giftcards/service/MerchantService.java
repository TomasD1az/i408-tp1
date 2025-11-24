package org.udesa.giftcards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.facade.GifCardFacade;
import org.udesa.giftcards.model.Merchant;
import org.udesa.giftcards.repository.MerchantRepository;

@Service
public class MerchantService extends ModelService<Merchant, MerchantRepository> {

    protected void updateData(Merchant existingObject, Merchant updatedObject) {
        existingObject.setCode(updatedObject.getCode());
    }

    @Transactional(readOnly = true)
    public Merchant findByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(GifCardFacade.InvalidMerchant));
    }

    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return repository.findByCode(code).isPresent();
    }
}
