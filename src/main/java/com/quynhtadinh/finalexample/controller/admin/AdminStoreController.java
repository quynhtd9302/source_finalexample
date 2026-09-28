package com.quynhtadinh.finalexample.controller.admin;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quynhtadinh.finalexample.entity.Store;
import com.quynhtadinh.finalexample.repository.StoreRepository;

@Controller
@RequestMapping("/admin/stores")
public class AdminStoreController {

    @Autowired private StoreRepository storeRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("title", "Cửa hàng");
        model.addAttribute("stores", storeRepository.findAll());
        return "admin/stores";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("title", "Thêm cửa hàng");
        model.addAttribute("store", new Store());
        return "admin/store-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "stores");
        model.addAttribute("title", "Sửa cửa hàng");
        model.addAttribute("store", storeRepository.findById(id).orElseThrow());
        return "admin/store-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("store") Store store) {
        if (store.getId() != null) {
            storeRepository.findById(store.getId()).ifPresent(existing -> store.setCreatedDate(existing.getCreatedDate()));
        }
        if (store.getCreatedDate() == null) {
            store.setCreatedDate(new Date(System.currentTimeMillis()));
        }
        storeRepository.save(store);
        return "redirect:/admin/stores";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        storeRepository.findById(id).ifPresent(store -> {
            store.setActive(!store.isActive());
            storeRepository.save(store);
        });
        return "redirect:/admin/stores";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        storeRepository.findById(id).ifPresent(store -> {
            if (store.getOrders() == null || store.getOrders().isEmpty()) {
                storeRepository.deleteById(id);
            } else {
                store.setActive(false);
                storeRepository.save(store);
            }
        });
        return "redirect:/admin/stores";
    }
}
