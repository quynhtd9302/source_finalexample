package com.quynhtadinh.finalexample.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quynhtadinh.finalexample.entity.Order;
import com.quynhtadinh.finalexample.entity.StatusOrder;
import com.quynhtadinh.finalexample.repository.OrderRepository;
import com.quynhtadinh.finalexample.repository.StatusOrderRepository;
import com.quynhtadinh.finalexample.repository.StoreRepository;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private StatusOrderRepository statusOrderRepository;

    @GetMapping
    public String list(Model model,
                        @RequestParam(required = false) Long storeId,
                        @RequestParam(required = false) Integer statusId) {
        List<Order> orders = orderRepository.findAll();
        orders.sort((a, b) -> Long.compare(b.getId(), a.getId()));

        if (storeId != null) {
            orders.removeIf(o -> o.getStore() == null || !o.getStore().getId().equals(storeId));
        }
        if (statusId != null) {
            orders.removeIf(o -> o.getStatus() == null || o.getStatus().getId() != statusId);
        }

        model.addAttribute("activeMenu", "orders");
        model.addAttribute("title", "Đơn hàng");
        model.addAttribute("orders", orders);
        model.addAttribute("stores", storeRepository.findAll());
        model.addAttribute("statuses", statusOrderRepository.findAll());
        model.addAttribute("selectedStoreId", storeId);
        model.addAttribute("selectedStatusId", statusId);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("title", "Chi tiết đơn hàng");
        model.addAttribute("order", orderRepository.findById(id).orElseThrow());
        model.addAttribute("statuses", statusOrderRepository.findAll());
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam int statusId) {
        Order order = orderRepository.findById(id).orElseThrow();
        StatusOrder status = statusOrderRepository.findById(statusId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders/" + id;
    }
}
