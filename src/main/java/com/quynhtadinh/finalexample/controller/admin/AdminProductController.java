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
import org.springframework.web.bind.annotation.RequestParam;

import com.quynhtadinh.finalexample.entity.Product;
import com.quynhtadinh.finalexample.entity.StatusProduct;
import com.quynhtadinh.finalexample.repository.ProductRepository;
import com.quynhtadinh.finalexample.repository.StatusProductRepository;
import com.quynhtadinh.finalexample.repository.SubCategoryRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired private ProductRepository productRepository;
    @Autowired private SubCategoryRepository subCategoryRepository;
    @Autowired private StatusProductRepository statusProductRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("activeMenu", "products");
        model.addAttribute("title", "Sản phẩm");
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("activeMenu", "products");
        model.addAttribute("title", "Thêm sản phẩm");
        model.addAttribute("product", new Product());
        model.addAttribute("subCategories", subCategoryRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("activeMenu", "products");
        model.addAttribute("title", "Sửa sản phẩm");
        model.addAttribute("product", productRepository.findById(id).orElseThrow());
        model.addAttribute("subCategories", subCategoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") Product product, @RequestParam Long subCategoryId) {
        if (product.getId() != null) {
            productRepository.findById(product.getId()).ifPresent(existing -> product.setCreatedDate(existing.getCreatedDate()));
        }
        if (product.getCreatedDate() == null) {
            product.setCreatedDate(new Date(System.currentTimeMillis()));
        }
        product.setStatus(defaultStatus());
        product.setSubCategory(subCategoryRepository.findById(subCategoryId.intValue()).orElseThrow());
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    private StatusProduct defaultStatus() {
        return statusProductRepository.findAll().stream().findFirst().orElseGet(() -> {
            StatusProduct status = new StatusProduct();
            status.setStatus("Còn hàng");
            return statusProductRepository.save(status);
        });
    }
}
