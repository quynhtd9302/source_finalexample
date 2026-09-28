package com.quynhtadinh.finalexample.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quynhtadinh.finalexample.entity.Category;
import com.quynhtadinh.finalexample.entity.StatusCategory;
import com.quynhtadinh.finalexample.entity.StatusSubCategory;
import com.quynhtadinh.finalexample.entity.SubCategory;
import com.quynhtadinh.finalexample.repository.CategoryRepository;
import com.quynhtadinh.finalexample.repository.StatusCategoryRepository;
import com.quynhtadinh.finalexample.repository.StatusSubCategoryRepository;
import com.quynhtadinh.finalexample.repository.SubCategoryRepository;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SubCategoryRepository subCategoryRepository;
    @Autowired private StatusCategoryRepository statusCategoryRepository;
    @Autowired private StatusSubCategoryRepository statusSubCategoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("activeMenu", "categories");
        model.addAttribute("title", "Danh mục");
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("activeMenu", "categories");
        model.addAttribute("title", "Thêm danh mục");
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@RequestParam(required = false) Integer id, @RequestParam String categoryName) {
        Category category = id != null ? categoryRepository.findById(id).orElse(new Category()) : new Category();
        category.setCategoryName(categoryName);
        category.setStatus(activeCategoryStatus());
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/delete")
    public String deleteCategory(@PathVariable int id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/subcategories/new")
    public String newSubCategoryForm(@RequestParam int categoryId, Model model) {
        model.addAttribute("activeMenu", "categories");
        model.addAttribute("title", "Thêm danh mục con");
        model.addAttribute("category", categoryRepository.findById(categoryId).orElseThrow());
        return "admin/subcategory-form";
    }

    @PostMapping("/subcategories/save")
    public String saveSubCategory(@RequestParam(required = false) Integer id, @RequestParam String subCategoryName,
                                   @RequestParam int categoryId) {
        SubCategory subCategory = id != null ? subCategoryRepository.findById(id).orElse(new SubCategory()) : new SubCategory();
        subCategory.setSubCategoryName(subCategoryName);
        subCategory.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        subCategory.setStatus(activeSubCategoryStatus());
        subCategoryRepository.save(subCategory);
        return "redirect:/admin/categories";
    }

    @GetMapping("/subcategories/{id}/delete")
    public String deleteSubCategory(@PathVariable int id) {
        subCategoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }

    private StatusCategory activeCategoryStatus() {
        return statusCategoryRepository.findAll().stream().findFirst().orElseGet(() -> {
            StatusCategory status = new StatusCategory();
            status.setStatus("Đang bán");
            return statusCategoryRepository.save(status);
        });
    }

    private StatusSubCategory activeSubCategoryStatus() {
        return statusSubCategoryRepository.findAll().stream().findFirst().orElseGet(() -> {
            StatusSubCategory status = new StatusSubCategory();
            status.setStatus("Đang bán");
            return statusSubCategoryRepository.save(status);
        });
    }
}
