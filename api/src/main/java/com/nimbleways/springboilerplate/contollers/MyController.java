package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) throw new IllegalArgumentException("No order found!");

        Set<Product> products = order.getItems();

        products.forEach(product -> {
            switch (product.getType()) {
                case "NORMAL":
                    productService.handleNormalProduct(product);
                    break;
                case "SEASONAL":
                    productService.handleSeasonalProduct(product);
                    break;
                case "EXPIRABLE":
                    productService.handleExpiredProduct(product);
                    break;
                default:
                    throw new IllegalArgumentException("Product type error!");
            }
        });

        return new ProcessOrderResponse(order.getId());
    }
}
