package com.jtspringproject.JtSpringProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.*;
import com.jtspringproject.JtSpringProject.services.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final userService userService;
    private final categoryService categoryService;
    private final productService productService;

    @Autowired
    public AdminController(userService userService,
                            categoryService categoryService,
                            productService productService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView adminLogin(@RequestParam(required = false) String error) {
        ModelAndView mv = new ModelAndView("adminlogin");
        if ("true".equals(error)) {
            mv.addObject("msg", "Invalid username or password. Please try again.");
        }
        return mv;
    }

    @GetMapping(value = { "/", "Dashboard" })
    public ModelAndView adminHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView mv = new ModelAndView("adminHome");
        mv.addObject("admin", auth.getName());
        return mv;
    }

    @GetMapping("categories")
    public ModelAndView getCategories() {
        ModelAndView mv = new ModelAndView("categories");
        mv.addObject("categories", categoryService.getCategories());
        return mv;
    }

    @PostMapping("/categories")
    public String addCategory(@RequestParam("categoryname") String name) {
        categoryService.addCategory(name);
        return "redirect:/admin/categories";
    }

    @PostMapping("categories/delete")
    public String deleteCategory(@RequestParam("id") int id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("categories/update")
    public String updateCategory(@RequestParam("categoryid") int id,
                                 @RequestParam("categoryname") String name) {
        categoryService.updateCategory(id, name);
        return "redirect:/admin/categories";
    }

    @GetMapping("products")
    public ModelAndView getProducts() {
        ModelAndView mv = new ModelAndView("products");
        List<Product> products = productService.getProducts();

        if (products.isEmpty()) {
            mv.addObject("msg", "No products available");
        } else {
            mv.addObject("products", products);
        }
        return mv;
    }

    @GetMapping("products/add")
    public ModelAndView addProductPage() {
        ModelAndView mv = new ModelAndView("productsAdd");
        mv.addObject("categories", categoryService.getCategories());
        return mv;
    }

    @PostMapping("products/add")
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("categoryid") int categoryId,
                             @RequestParam("price") int price,
                             @RequestParam("weight") int weight,
                             @RequestParam("quantity") int quantity,
                             @RequestParam("description") String description,
                             @RequestParam("productImage") String image) {

        Product p = buildProduct(name, categoryId, price, weight, quantity, description, image);
        productService.addProduct(p);
        return "redirect:/admin/products";
    }

    @GetMapping("products/update/{id}")
    public ModelAndView updatePage(@PathVariable int id) {
        ModelAndView mv = new ModelAndView("productsUpdate");
        mv.addObject("product", productService.getProduct(id));
        mv.addObject("categories", categoryService.getCategories());
        return mv;
    }

    @PostMapping("products/update/{id}")
    public String updateProduct(@PathVariable int id,
                                @RequestParam("name") String name,
                                @RequestParam("categoryid") int categoryId,
                                @RequestParam("price") int price,
                                @RequestParam("weight") int weight,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("description") String description,
                                @RequestParam("productImage") String image) {

        Product p = buildProduct(name, categoryId, price, weight, quantity, description, image);
        productService.updateProduct(id, p);
        return "redirect:/admin/products";
    }

    @PostMapping("products/delete")
    public String deleteProduct(@RequestParam("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("customers")
    public ModelAndView customers() {
        ModelAndView mv = new ModelAndView("displayCustomers");
        mv.addObject("customers", userService.getUsers());
        return mv;
    }

    @GetMapping("profileDisplay")
    public String profile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        if (user != null) {
            model.addAttribute("userid", user.getId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("address", user.getAddress());
        }

        return "updateProfile";
    }

    @PostMapping("updateuser")
    public String updateUser(@RequestParam("userid") int id,
                             @RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("address") String address) {

        userService.updateUserProfile(id, username, email, password, address);
        return "redirect:/admin/index";
    }

    private Product buildProduct(String name, int categoryId, int price, int weight,
                                 int quantity, String description, String image) {

        Category c = categoryService.getCategory(categoryId);

        Product p = new Product();
        p.setName(name);
        p.setCategory(c);
        p.setPrice(price);
        p.setWeight(weight);
        p.setQuantity(quantity);
        p.setDescription(description);
        p.setImage(image);

        return p;
    }
}
