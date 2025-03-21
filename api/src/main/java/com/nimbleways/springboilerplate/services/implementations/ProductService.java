package com.nimbleways.springboilerplate.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    public void notifyDelay(Product product) {
        notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
    }

    public void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) product.setAvailable(product.getAvailable() - 1);
        else if (product.getLeadTime() > 0) notifyDelay(product);
        productRepository.save(product);
    }

    public void handleSeasonalProduct(Product product) {
        if (product.isSeasonalProductOutOfSeason() || product.isSeasonalProductNotInSeason()) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else notifyDelay(product);
    }

    public void handleExpiredProduct(Product product) {
        if (product.getAvailable() <= 0 || product.isExpirableProductExpired()) {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        } else {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        }
    }

}
