package com.quynhtadinh.finalexample.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quynhtadinh.finalexample.entity.Cart;
import com.quynhtadinh.finalexample.entity.Category;
import com.quynhtadinh.finalexample.entity.Image;
import com.quynhtadinh.finalexample.entity.Order;
import com.quynhtadinh.finalexample.entity.OrderDetail;
import com.quynhtadinh.finalexample.entity.Product;
import com.quynhtadinh.finalexample.entity.Shipping;
import com.quynhtadinh.finalexample.entity.StatusOrder;
import com.quynhtadinh.finalexample.entity.Store;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.CategoryRepository;
import com.quynhtadinh.finalexample.repository.OrderDetailRepository;
import com.quynhtadinh.finalexample.repository.OrderRepository;
import com.quynhtadinh.finalexample.repository.ProductRepository;
import com.quynhtadinh.finalexample.repository.ShippingRepository;
import com.quynhtadinh.finalexample.repository.StoreRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;
import com.quynhtadinh.finalexample.util.MathFunction;

@Controller
public class WebController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ShippingRepository shippingRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/products")
    public String getListProducts(Model model,
                                  @RequestParam(name = "page", defaultValue = "1") Integer page,
                                  @RequestParam(name = "categoryId", defaultValue = "-1") Integer categoryId,
                                  @RequestParam(name = "subCategoryId", defaultValue = "-1") Integer subCategoryId) {
        final int PAGE_SIZE = 20;
        Page<Product> products;
        if (subCategoryId != -1) {
            products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(page - 1, PAGE_SIZE));
            if (products.getContent().size() == 0) {
                products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, PAGE_SIZE));
                page = 1;
            }
        } else if (categoryId != -1) {
            products = productRepository.findByCategoryId(categoryId, PageRequest.of(page - 1, PAGE_SIZE));
            if (products.getContent().size() == 0) {
                products = productRepository.findByCategoryId(categoryId, PageRequest.of(0, PAGE_SIZE));
                page = 1;
            }
        } else {
            products = productRepository.findAll(PageRequest.of(page - 1, PAGE_SIZE));
            if (products.getContent().size() == 0) {
                products = productRepository.findAll(PageRequest.of(0, PAGE_SIZE));
                page = 1;
            }
        }

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", products.getTotalPages());
        return "listProduct";
    }

    @GetMapping("/detail")
    public String detail(Model model, @RequestParam(name = "productId") Long productId) {
        Product product = productRepository.findById(productId).get();
        List<Image> images = product.getImages();
        Image image = new Image();
        image.setImageUrl(product.getImageUrl());
        images.add(0, image);
        product.setImages(images);
        model.addAttribute("product", product);
        return "detail";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(Model model, HttpSession session, @RequestParam("productId") Long productId) {
        Product product = productRepository.findById(productId).get();
        Cart cart = new Cart();
        cart.setProductId(product.getId());
        cart.setProductCode(product.getCode());
        cart.setProductName(product.getName());
        cart.setProductQuantity(product.getQuantity());
        cart.setProductPrice(product.getPrice());
        cart.setProductDescription(product.getDescription());
        cart.setProductImageUrl(product.getImageUrl());
        cart.setQuantity(1);

        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (listCart == null) {
            listCart = new ArrayList<>();
            listCart.add(cart);
        } else {
            //có nghĩa là list cart đã tồn tại

            //2 khả năng

            //kn1: sản phẩm chuẩn bị thêm đã có trên giỏ hàng
            boolean isExist = false;
            for (Cart C : listCart) {
                if (productId == C.getProductId()) {
                    //tồn tại sản phẩm đấy trên giỏ hàng rồi
                    isExist = true;
                    C.setQuantity(C.getQuantity() + 1);
                }
            }
            if (!isExist) {
                listCart.add(cart);
            }
        }

        session.setAttribute("listCart", listCart);
        return "redirect:/carts";
    }

    @GetMapping("/carts")
    public String getListCart(Model model, HttpSession session) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (listCart == null || listCart.size() == 0) {
            return "emptyCart";
        }
        //tính tổng tiền
        double totalMoney = 0;
        for (Cart c : listCart) {
            totalMoney += c.getProductPrice() * c.getQuantity();
        }
        model.addAttribute("listCart", listCart);
        model.addAttribute("totalMoney", MathFunction.getMoney(totalMoney));
        return "listCart";
    }

    @GetMapping("/delete-cart")
    public String deleteCart(Model model, HttpSession session, @RequestParam(value = "productId", required = false) Long productId) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (productId == null) {
            session.setAttribute("listCart", new ArrayList<Cart>());
        } else {
            for (Cart c : listCart) {
                if (c.getProductId() == productId) {
                    listCart.remove(c);
                    break;
                }
            }
            session.setAttribute("listCart", listCart);
        }
        return "redirect:/carts";
    }

    @PostMapping("/update-cart")
    public String updateCart(HttpServletRequest req, HttpSession session) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        for (int i = 0; i < listCart.size(); i++) {
            listCart.get(i).setQuantity(Integer.parseInt(req.getParameter("quantity" + i)));
        }
        session.setAttribute("listCart", listCart);
        return "redirect:/carts";
    }


    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (listCart == null || listCart.size() == 0) {
            return "emptyCart";
        }
        //tính tổng tiền
        double totalMoney = 0;
        for (Cart c : listCart) {
            totalMoney += c.getProductPrice() * c.getQuantity();
        }
        model.addAttribute("listCart", listCart);
        model.addAttribute("totalMoney", MathFunction.getMoney(totalMoney));
        model.addAttribute("stores", storeRepository.findByActiveTrue());
        return "checkout";
    }

    @PostMapping("/prepare-shipping")
    public String postPrepareShipping(Model model, @RequestParam(name = "name") String name,
                                      @RequestParam(name = "phone") String phone,
                                      @RequestParam(name = "address") String address,
                                      @RequestParam(name = "note") String note,
                                      @RequestParam(name = "storeId") Long storeId,
                                      @RequestParam(name = "paymentMethod") String paymentMethod, HttpSession session) {

        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (listCart == null || listCart.size() == 0) {
            return "emptyCart";
        }
        //tính tổng tiền
        double totalMoney = 0;
        for (Cart c : listCart) {
            totalMoney += c.getProductPrice() * c.getQuantity();
        }
        //lưu database
        Shipping shipping = new Shipping();
        shipping.setName(name);
        shipping.setPhone(phone);
        shipping.setAddress(address);
        shipping = shippingRepository.save(shipping);

        Order order = new Order();
        order.setTotalPrice(totalMoney);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        order.setCreatedDate(java.sql.Date.valueOf(sdf.format(now)));
        StatusOrder statusOrder = new StatusOrder();
        statusOrder.setId(1);
        order.setStatus(statusOrder);
        order.setShipping(shipping);
        storeRepository.findById(storeId).ifPresent(order::setStore);

        String username = getCurrentUsername();
        if (username != null) {
            order.setUser(userRepository.findByUsername(username));
        }

        order = orderRepository.save(order);

        for (Cart cart : listCart) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setProductName(cart.getProductName());
            orderDetail.setProductPrice(cart.getProductPrice());
            orderDetail.setProductImage(cart.getProductImageUrl());
            productRepository.findById(cart.getProductId()).ifPresent(orderDetail::setProduct);
            orderDetailRepository.save(orderDetail);
        }

        session.setAttribute("listCart", new ArrayList<Cart>());
        model.addAttribute("order", order);
        return "prepareShipping";
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            // Google/Facebook logins: our stored "username" is always the account's
            // email, but the OAuth2 principal's name may be its provider id instead.
            Object email = ((org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal())
                    .getAttributes().get("email");
            return email != null ? email.toString() : null;
        }
        return authentication.getName();
    }

    @GetMapping("/stores")
    public String stores(Model model) {
        model.addAttribute("stores", storeRepository.findByActiveTrue());
        return "stores";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        final int PAGE_SIZE = 20;
        Page<Product> products = productRepository.search("%" + keyword + "%", PageRequest.of(page - 1, PAGE_SIZE));
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", products.getTotalPages());
        return "listProduct";
    }

}
