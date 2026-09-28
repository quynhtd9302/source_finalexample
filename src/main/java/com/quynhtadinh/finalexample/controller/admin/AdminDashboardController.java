package com.quynhtadinh.finalexample.controller.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quynhtadinh.finalexample.entity.Order;
import com.quynhtadinh.finalexample.repository.OrderRepository;
import com.quynhtadinh.finalexample.repository.ProductRepository;
import com.quynhtadinh.finalexample.repository.StoreRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Order> orders = orderRepository.findAll();

        double totalRevenue = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        Map<String, Double> revenueByStore = new LinkedHashMap<>();
        for (Order order : orders) {
            String storeName = order.getStore() != null ? order.getStore().getName() : "Chưa chọn cửa hàng";
            revenueByStore.merge(storeName, order.getTotalPrice(), Double::sum);
        }

        List<Order> recentOrders = new ArrayList<>(orders);
        recentOrders.sort(Comparator.comparing(Order::getId).reversed());
        if (recentOrders.size() > 8) {
            recentOrders = recentOrders.subList(0, 8);
        }

        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("title", "Dashboard");
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalStores", storeRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("revenueStoreLabels", revenueByStore.keySet());
        model.addAttribute("revenueStoreData", revenueByStore.values());
        model.addAttribute("recentOrders", recentOrders);
        return "admin/dashboard";
    }
}
